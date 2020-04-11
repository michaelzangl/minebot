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

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.suggestion.Suggestions;
import net.famzangl.minecraft.minebot.ai.AIController;
import net.famzangl.minecraft.minebot.ai.command.AIChatController;
import net.famzangl.minecraft.minebot.ai.utils.PrivateFieldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.IPacket;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.client.CChatMessagePacket;
import net.minecraft.network.play.client.CTabCompletePacket;
import net.minecraft.network.play.server.SBlockActionPacket;
import net.minecraft.network.play.server.SChangeBlockPacket;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.network.play.server.SChunkDataPacket;
import net.minecraft.network.play.server.SMultiBlockChangePacket;
import net.minecraft.network.play.server.SPlaySoundEffectPacket;
import net.minecraft.network.play.server.SPlaySoundEventPacket;
import net.minecraft.network.play.server.SPlayerPositionLookPacket;
import net.minecraft.network.play.server.SSpawnParticlePacket;
import net.minecraft.network.play.server.STabCompletePacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

public class MinebotNetHandler extends ClientPlayNetHandler implements
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

		public PersistentChat(SChatPacket packetIn) {
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

	public MinebotNetHandler(Minecraft mcIn, Screen p_i46300_2_,
			NetworkManager p_i46300_3_, GameProfile p_i46300_4_) {
		super(mcIn, p_i46300_2_, p_i46300_3_, p_i46300_4_);
		this.mcIn = mcIn;
	}

	@Override
	public void sendPacket(IPacket<?> packetIn) {
		if (packetIn instanceof CChatMessagePacket) {
			CChatMessagePacket chatMessage = (CChatMessagePacket) packetIn;
			// Intercept chat message.
			String message = chatMessage.getMessage();
			if (message.startsWith("/")) {
				if (AIChatController.getRegistry().interceptCommand(message)) {
					return;
				}
			}
		} else if (packetIn instanceof CTabCompletePacket) {
			CTabCompletePacket complete = (CTabCompletePacket) packetIn;
			String message = complete.getCommand();
			if (message.startsWith("/") && message.indexOf(" ") >= 0) {
				if (AIChatController.getRegistry().interceptTab(complete, this)) {
					return;
				}
			}
			lastSendTabComplete = message;
		}
		super.sendPacket(packetIn);
	}

	@Override
	public void handleTabComplete(STabCompletePacket packetIn) {
		if (lastSendTabComplete != null && lastSendTabComplete.startsWith("/")
				&& !lastSendTabComplete.contains(" ")) {
			Suggestions newStrings = AIChatController.getRegistry().fillTabComplete(this, packetIn.getSuggestions(), lastSendTabComplete);
			packetIn = new STabCompletePacket(packetIn.getTransactionId(), newStrings);
			lastSendTabComplete = null;
		}
		super.handleTabComplete(packetIn);
	}

	public static NetworkHelper inject(AIController aiController,
									   IClientPlayNetHandler oldHandler) {
		ClientPlayNetHandler netHandler = (ClientPlayNetHandler) oldHandler;
		if (netHandler != null && netHandler instanceof ClientPlayNetHandler) {

			if (!(netHandler instanceof MinebotNetHandler)) {
				Screen screen = PrivateFieldUtils.getFieldValue(netHandler,
						ClientPlayNetHandler.class, Screen.class);
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
	public void handleParticles(SSpawnParticlePacket packetIn) {
		// For detecting fishing rod events.
		// TODO: Check particle type
		if (packetIn.getParticle().getType() == ParticleTypes.DRIPPING_WATER) {
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
	public void handleEffect(SPlaySoundEventPacket packetIn) {
		super.handleEffect(packetIn);
	}

	@Override
	public void handleSoundEffect(SPlaySoundEffectPacket packetIn) {
		ResourceLocation name = packetIn.getSound().getName();
		//TODO: Check this name
		// System.out.println(name.getPath());
		if ("entity.bobber.splash".equals(name.getPath())) {
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

	public void resetFishState() {
		LOGGER.trace(MARKER_FISH, "reset");
		foundFishPositions.clear();
	}

	@Override
	public void handleChunkData(SChunkDataPacket packetIn) {
		int x = packetIn.getChunkX();
		int z = packetIn.getChunkZ();
		fireChunkChange(x, z);
		super.handleChunkData(packetIn);
	}

	@Override
	public void handleBlockChange(SChangeBlockPacket packetIn) {
		blockChange(packetIn.getPos());
		super.handleBlockChange(packetIn);
	}

	@Override
	public void handleBlockAction(SBlockActionPacket packetIn) {
		blockChange(packetIn.getBlockPosition());
		super.handleBlockAction(packetIn);
	}

	@Override
	public void handleMultiBlockChange(SMultiBlockChangePacket packetIn) {
		for (SMultiBlockChangePacket.UpdateData b : packetIn.getChangedBlocks()) {
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
	public void handleChat(SChatPacket packetIn) {
		if (false /* TODO mcIn.isCallingFromMinecraftThread() */) {
			LOGGER.trace(MARKER_CHAT, "Received chat package: " + packetIn.hashCode() + ": " + packetIn.getChatComponent());
			chatMessages.add(new PersistentChat(packetIn));
		} // else: super passes it on to mc thread.
		super.handleChat(packetIn);
	}

	public List<PersistentChat> getChatMessages() {
		return Collections.unmodifiableList(chatMessages);
	}
	
	@Override
	public void handlePlayerPosLook(SPlayerPositionLookPacket packetIn) {
		LOGGER.trace(MARKER_POS, "Forced move to: " + packetIn.getX() + "," + packetIn.getZ());
		super.handlePlayerPosLook(packetIn);
	}
}
