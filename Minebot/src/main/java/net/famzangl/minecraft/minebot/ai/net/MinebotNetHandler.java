/*******************************************************************************
 * This file is part of Minebot.
 *
 * Minebot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Minebot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Minebot.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package net.famzangl.minecraft.minebot.ai.net;

import com.mojang.brigadier.tree.RootCommandNode;
import mcp.MethodsReturnNonnullByDefault;
import net.famzangl.minecraft.minebot.ai.command.AIChatController;
import net.famzangl.minecraft.minebot.ai.net.Intercepts.InterceptResult;
import net.famzangl.minecraft.minebot.ai.utils.PrivateFieldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.entity.Entity;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.client.CChatMessagePacket;
import net.minecraft.network.play.server.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MinebotNetHandler implements NetworkHelper {
	private static final Marker MARKER_CHAT = MarkerManager
			.getMarker("chat");
	private static final Marker MARKER_FISH = MarkerManager
			.getMarker("fish");
	private static final Marker MARKER_COMPLETE = MarkerManager
			.getMarker("complete");
	private static final Marker MARKER_POS = MarkerManager
			.getMarker("pos");
	private static final Logger LOGGER = LogManager.getLogger(MinebotNetHandler.class);

	private final MinebotPacketInterceptors interceptors = new MinebotPacketInterceptors();

	private static final double MAX_FISH_DISTANCE = 10;

	private final ConcurrentLinkedQueue<BlockPos> foundFishPositions = new ConcurrentLinkedQueue<BlockPos>();

	private final CopyOnWriteArrayList<ChunkListener> listeners = new CopyOnWriteArrayList<>();

	private LinkedHashMap<Integer, String> lastSendTabComplete = new LinkedHashMap<>();

	private final ArrayList<PersistentChat> chatMessages = new ArrayList<PersistentChat>();

	public MinebotNetHandler() {
		interceptors.addOutgoingInterceptor(CChatMessagePacket.class, this::sendPacketChatMessage);
		//interceptors.addOutgoingInterceptor(CTabCompletePacket.class, this::sendPacketTabComplete);

		//interceptors.addIncomingInterceptor(STabCompletePacket.class, this::handleTabComplete);
		interceptors.addIncomingInterceptor(SCommandListPacket.class, this::addMinebotCommands);
		// unused interceptors.addIncomingInterceptor(SPlaySoundEventPacket.class, this::handleEffect);
		interceptors.addIncomingInterceptor(SPlaySoundEffectPacket.class, this::handleSoundEffect);
		interceptors.addIncomingInterceptor(SChunkDataPacket.class, this::handleChunkData);
		interceptors.addIncomingInterceptor(SChangeBlockPacket.class, this::handleBlockChange);
		interceptors.addIncomingInterceptor(SBlockActionPacket.class, this::handleBlockAction);
		interceptors.addIncomingInterceptor(SMultiBlockChangePacket.class, this::handleMultiBlockChange);
		interceptors.addIncomingInterceptor(SChatPacket.class, this::handleChat);
		interceptors.addIncomingInterceptor(SPlayerPositionLookPacket.class, this::handlePlayerPosLook);
	}

	public InterceptResult<CChatMessagePacket> sendPacketChatMessage(CChatMessagePacket chatMessage) {
		// Intercept chat message.
		String message = chatMessage.getMessage();
		if (message.startsWith("/") && AIChatController.getRegistry().interceptCommand(message)) {
			// Do not send the command
			return InterceptResult.drop();
		}
		return InterceptResult.pass();
	}

	public InterceptResult<SCommandListPacket> addMinebotCommands(SCommandListPacket packet) {
		RootCommandNode<ISuggestionProvider> root = packet.getRoot();

		AIChatController.getRegistry().addCommandsTo(root);

		return InterceptResult.replaceBy(new SCommandListPacket(root));
	}

	/*
	protected InterceptResult sendPacketTabComplete(CTabCompletePacket complete) {
		String message = complete.getCommand();
		if (message.startsWith("/") && message.contains(" ")) {
			if (AIChatController.getRegistry().interceptTab(complete, this)) {
				// Do not send the tab complete => We don't need to let the server know
				return InterceptResult.drop();
			}
		}
		lastSendTabComplete.put(complete.getTransactionId(), complete.getCommand());
		while (lastSendTabComplete.size() > 10) {
			lastSendTabComplete.remove(lastSendTabComplete.keySet().iterator().next());
		}
		return InterceptResult.pass();
	}

	public InterceptResult handleTabComplete(STabCompletePacket packetIn) {
		String requestForPacket = lastSendTabComplete.remove(packetIn.getTransactionId());
		if (requestForPacket != null && requestForPacket.startsWith("/")
				&& !requestForPacket.contains(" ")) {
			Suggestions newStrings = AIChatController.getRegistry().fillTabComplete(this, packetIn.getSuggestions(), requestForPacket);
			packetIn = new STabCompletePacket(packetIn.getTransactionId(), newStrings);
			return InterceptResult.replaceBy(packetIn);
		}
		return InterceptResult.pass();
	}
	 */

	public InterceptResult handleSoundEffect(SPlaySoundEffectPacket packetIn) {
		ResourceLocation name = packetIn.getSound().getName();
		if ("entity.fishing_bobber.splash".equals(name.getPath())) {
			double x = packetIn.getX();
			double y = packetIn.getY();
			double z = packetIn.getZ();
			foundFishPositions.add(new BlockPos(x, y, z));
			LOGGER.trace(MARKER_FISH, "fish at " + new BlockPos(x, y, z));
		}
		return InterceptResult.pass();
	}

	@Override
	public boolean fishIsCaptured(Entity expectedPos) {
		while (true) {
			BlockPos next = foundFishPositions.poll();
			if (next == null) {
				return false;
			} else if (expectedPos.getDistanceSq(next.getX() + 0.5,
					next.getY() + 0.5, next.getZ() + 0.5) < MAX_FISH_DISTANCE * MAX_FISH_DISTANCE) {
				LOGGER.trace(MARKER_FISH, "found fish for " + expectedPos + ": " + next);
				return true;
			} else {
				LOGGER.trace(MARKER_FISH, "Found a fish bite at " + next
						+ " but fishing at " + expectedPos + ".");
			}
		}
	}

	@Override
	public void resetFishState() {
		LOGGER.trace(MARKER_FISH, "reset");
		foundFishPositions.clear();
	}

	public InterceptResult handleChunkData(SChunkDataPacket packetIn) {
		int x = packetIn.getChunkX();
		int z = packetIn.getChunkZ();
		fireChunkChange(x, z);
		return InterceptResult.pass();
	}

	public InterceptResult handleBlockChange(SChangeBlockPacket packetIn) {
		blockChange(packetIn.getPos());
		return InterceptResult.pass();
	}

	public InterceptResult handleBlockAction(SBlockActionPacket packetIn) {
		blockChange(packetIn.getBlockPosition());
		return InterceptResult.pass();
	}

	public InterceptResult handleMultiBlockChange(SMultiBlockChangePacket packetIn) {
		for (SMultiBlockChangePacket.UpdateData b : packetIn.getChangedBlocks()) {
			blockChange(b.getPos());
		}
		return InterceptResult.pass();
	}

	private void blockChange(BlockPos pos) {
		int chunkPosX = pos.getX() >> 4;
		int chunkPosZ = pos.getZ() >> 4;
		fireChunkChange(chunkPosX, chunkPosZ);
	}

	private void fireChunkChange(int chunkPosX, int chunkPosZ) {
		for (ChunkListener l : listeners) {
			l.chunkChanged(chunkPosX, chunkPosZ);
		}
	}

	@Override
	public void addChunkChangeListener(ChunkListener l) {
		listeners.add(l);
	}

	@Override
	public void removeChunkChangeListener(ChunkListener l) {
		listeners.remove(l);
	}

	public InterceptResult handleChat(SChatPacket packetIn) {
		LOGGER.trace(MARKER_CHAT, "Received chat package: " + packetIn.hashCode() + ": " + packetIn.getChatComponent());
		chatMessages.add(new PersistentChat(packetIn));
		return InterceptResult.pass();
	}

	public InterceptResult handlePlayerPosLook(SPlayerPositionLookPacket packetIn) {
		LOGGER.trace(MARKER_POS, "Forced move to: " + packetIn.getX() + "," + packetIn.getZ());
		return InterceptResult.pass();
	}

	public List<PersistentChat> getChatMessages() {
		return Collections.unmodifiableList(chatMessages);
	}

	private void injectInto(ClientPlayNetHandler oldHandler) {
		// Intercepts incoming packages
		NetworkManager originalManager = oldHandler.getNetworkManager();
		// Set the field packetListener
		MinebotClientPlayNetHandler playNetHandler = new MinebotClientPlayNetHandler(oldHandler, interceptors.getIncoming());
		PrivateFieldUtils.setFieldValue(originalManager, NetworkManager.class, INetHandler.class,
				playNetHandler);

		// Intercept outgoing packages
		MinebotNetworkManager mnm = new MinebotNetworkManager(originalManager, interceptors.getOutgoing());
		// Set it to the client only => the client packages is what we want to intercept. The rest of MC will only see the original handler
		PrivateFieldUtils.setFieldValue(oldHandler, ClientPlayNetHandler.class, NetworkManager.class, mnm);

		// Problem is that commands package is most likely in the queue. So get it from old handler and pass it thorough the new handler again
		// It is important to use enqueue here so that this statemnt is executed after the commands received from the server left the queue
		Minecraft.getInstance().enqueue(() -> {
			if (playNetHandler.getNetworkManager().isChannelOpen()) {
				new SCommandListPacket(oldHandler.getCommandDispatcher().getRoot()).processPacket(playNetHandler);
			}
		});
	}

	public static NetworkHelper inject(ClientPlayNetHandler into) {
		NetworkManager networkManager = into.getNetworkManager();
		if (!(networkManager instanceof MinebotNetworkManager)) {
			// Do the injection to intercept all network packages
			MinebotNetHandler handler = new MinebotNetHandler();
			handler.injectInto(into);
			LOGGER.info("Minebot network handler injected.");
			return handler;
		} else {
			throw new IllegalStateException("Attempted to inject network helpers twice.");
		}
	}

}
