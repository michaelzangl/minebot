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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.mojang.authlib.GameProfile;

import net.famzangl.minecraft.minebot.ai.AIController;
import net.famzangl.minecraft.minebot.ai.command.AIChatController;
import net.famzangl.minecraft.minebot.ai.utils.PrivateFieldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketTabComplete;
import net.minecraft.network.play.server.SPacketBlockAction;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.network.play.server.SPacketEffect;
import net.minecraft.network.play.server.SPacketMultiBlockChange;
import net.minecraft.network.play.server.SPacketMultiBlockChange.BlockUpdateData;
import net.minecraft.network.play.server.SPacketParticles;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketTabComplete;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;

public class MinebotNetHandler extends NetHandlerPlayClient implements
		NetworkHelper {
	private static final Marker MARKER_CHAT = MarkerManager
			.getMarker("chat");
	private static final Marker MARKER_FISH = MarkerManager
			.getMarker("fish");
	private static final Marker MARKER_COMPLETE = MarkerManager
			.getMarker("complete");
	private static final Marker MARKER_POS = MarkerManager
			.getMarker("pos");
	private static final Logger LOGGER = LogManager.getLogger(MinebotNetHandler.class);

	public static class PersistentChat {

		private final ITextComponent message;

		private final boolean chat;

		private final long time = System.currentTimeMillis();

		public PersistentChat(SPacketChat packetIn) {
			chat = !packetIn.isSystem();
			message = packetIn.getChatComponent();
		}

		public ITextComponent getMessage() {
			return message;
		}

		public boolean isChat() {
			return chat;
		}

		public long getTime() {
			return time;
		}

		@Override
		public String toString() {
			return "PersistentChat [message=" + message + ", chat=" + chat
					+ "]";
		}

	}

	private static final double MAX_FISH_DISTANCE = 10;

	private final ConcurrentLinkedQueue<BlockPos> foundFishPositions = new ConcurrentLinkedQueue<BlockPos>();

	private final CopyOnWriteArrayList<ChunkListener> listeners = new CopyOnWriteArrayList<ChunkListener>();

	private String lastSendTabComplete;

	private final ArrayList<PersistentChat> chatMessages = new ArrayList<PersistentChat>();
	private Minecraft mcIn;

	public MinebotNetHandler(Minecraft mcIn, GuiScreen p_i46300_2_,
			NetworkManager p_i46300_3_, GameProfile p_i46300_4_) {
		super(mcIn, p_i46300_2_, p_i46300_3_, p_i46300_4_);
		this.mcIn = mcIn;
	}

	@Override
	public void sendPacket(Packet<?> packetIn) {
		if (packetIn instanceof CPacketChatMessage) {
			CPacketChatMessage chatMessage = (CPacketChatMessage) packetIn;
			// Intercept chat message.
			String message = chatMessage.getMessage();
			if (message.startsWith("/")) {
				if (AIChatController.getRegistry().interceptCommand(message)) {
					return;
				}
			}
		} else if (packetIn instanceof CPacketTabComplete) {
			CPacketTabComplete complete = (CPacketTabComplete) packetIn;
			String message = complete.getMessage();
			if (message.startsWith("/") && message.indexOf(" ") >= 0) {
				if (AIChatController.getRegistry().interceptTab(message, this)) {
					return;
				}
			}
			lastSendTabComplete = message;
		}
		super.sendPacket(packetIn);
	}

	@Override
	public void handleTabComplete(SPacketTabComplete packetIn) {
		if (lastSendTabComplete != null && lastSendTabComplete.startsWith("/")
				&& !lastSendTabComplete.contains(" ")) {
			String[] newStrings = AIChatController.getRegistry()
					.fillTabComplete(this, packetIn.getMatches(),
							lastSendTabComplete);
			packetIn = new SPacketTabComplete(newStrings);
			lastSendTabComplete = null;
		}
		super.handleTabComplete(packetIn);
	}

	public static NetworkHelper inject(AIController aiController,
			NetworkManager manager, INetHandlerPlayClient oldHandler) {
		NetHandlerPlayClient netHandler = (NetHandlerPlayClient) oldHandler;
		if (netHandler != null && netHandler instanceof NetHandlerPlayClient) {

			if (!(netHandler instanceof MinebotNetHandler)) {
				GuiScreen screen = PrivateFieldUtils.getFieldValue(netHandler,
						NetHandlerPlayClient.class, GuiScreen.class);
				MinebotNetHandler handler = new MinebotNetHandler(
						aiController.getMinecraft(), screen,
						netHandler.getNetworkManager(),
						netHandler.getGameProfile());
				netHandler.getNetworkManager().setNetHandler(handler);
				LOGGER.info("Minebot network handler injected.");
				return handler;
			} else {
				return (NetworkHelper) netHandler;
			}
		}
		return null;
	}

	@Override
	public void handleParticles(SPacketParticles packetIn) {
		// For detecting fishing rod events.
		if (packetIn.getParticleType() == EnumParticleTypes.WATER_SPLASH) {
			if (packetIn.getParticleCount() > 0) {
				double x = packetIn.getXCoordinate();
				double y = packetIn.getYCoordinate();
				double z = packetIn.getZCoordinate();
				LOGGER.trace(MARKER_FISH, "fish particle (?) at " + new BlockPos(x, y, z));
				// foundFishPositions.add(new BlockPos(x, y, z));
				// System.out.println("NetHandler: fish at " + new BlockPos(x,
				// y, z) + ", packet: " +
				// Arrays.toString(packetIn.getParticleArgs()) + "; " +
				// packetIn.getParticleCount()+ "; " + packetIn.getXOffset()+
				// "; " + packetIn.getYOffset()+ "; " + packetIn.getZOffset());
			}
		}
		super.handleParticles(packetIn);
	}

	@Override
	public void handleEffect(SPacketEffect packetIn) {
		super.handleEffect(packetIn);
	}

	@Override
	public void handleSoundEffect(SPacketSoundEffect packetIn) {
		ResourceLocation name = packetIn.getSound().getSoundName();
		//TODO: Check this name
		System.out.println(name.getResourcePath());
		if ("entity.bobber.splash".equals(name.getResourcePath())) {
			double x = packetIn.getX();
			double y = packetIn.getY();
			double z = packetIn.getZ();
			foundFishPositions.add(new BlockPos(x, y, z));
			LOGGER.trace(MARKER_FISH, "fish at " + new BlockPos(x, y, z));
		}
		super.handleSoundEffect(packetIn);
	}

	public boolean fishIsCaptured(Entity expectedPos) {
		while (true) {
			BlockPos next = foundFishPositions.poll();
			if (next == null) {
				return false;
			} else if (expectedPos.getDistance(next.getX() + 0.5,
					next.getY() + 0.5, next.getZ() + 0.5) < MAX_FISH_DISTANCE) {
				LOGGER.trace(MARKER_FISH, "found fish for " + expectedPos + ": " + next);
				return true;
			} else {
				LOGGER.trace(MARKER_FISH, "Found a fish bite at " + next
						+ " but fishing at " + expectedPos + ".");
			}
		}
	}

	public void resetFishState() {
		LOGGER.trace(MARKER_FISH, "reset");
		foundFishPositions.clear();
	}

	@Override
	public void handleChunkData(SPacketChunkData packetIn) {
		int x = packetIn.getChunkX();
		int z = packetIn.getChunkZ();
		fireChunkChange(x, z);
		super.handleChunkData(packetIn);
	}

	@Override
	public void handleBlockChange(SPacketBlockChange packetIn) {
		blockChange(packetIn.getBlockPosition());
		super.handleBlockChange(packetIn);
	}

	@Override
	public void handleBlockAction(SPacketBlockAction packetIn) {
		blockChange(packetIn.getBlockPosition());
		super.handleBlockAction(packetIn);
	}
	
	@Override
	public void handleMultiBlockChange(SPacketMultiBlockChange packetIn) {
		for (BlockUpdateData b : packetIn.getChangedBlocks()) {
			blockChange(b.getPos());
		}
		super.handleMultiBlockChange(packetIn);
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

	@Override
	public void handleChat(SPacketChat packetIn) {
		if (mcIn.isCallingFromMinecraftThread()) {
			LOGGER.trace(MARKER_CHAT, "Received chat package: " + packetIn.hashCode() + ": " + packetIn.getChatComponent());
			chatMessages.add(new PersistentChat(packetIn));
		} // else: super passes it on to mc thread.
		super.handleChat(packetIn);
	}

	public List<PersistentChat> getChatMessages() {
		return Collections.unmodifiableList(chatMessages);
	}
	
	@Override
	public void handlePlayerPosLook(SPacketPlayerPosLook packetIn) {
		LOGGER.trace(MARKER_POS, "Forced move to: " + packetIn.getX() + "," + packetIn.getZ());
		super.handlePlayerPosLook(packetIn);
	}
}
