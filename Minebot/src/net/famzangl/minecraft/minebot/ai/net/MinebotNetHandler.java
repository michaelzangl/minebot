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

import java.util.concurrent.ConcurrentLinkedQueue;

import scala.actors.threadpool.Arrays;
import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIController;
import net.famzangl.minecraft.minebot.ai.command.AIChatController;
import net.famzangl.minecraft.minebot.ai.utils.PrivateFieldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.network.play.server.S28PacketEffect;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraft.network.play.server.S3APacketTabComplete;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;

import com.mojang.authlib.GameProfile;

public class MinebotNetHandler extends NetHandlerPlayClient {
	private static final double MAX_FISH_DISTANCE = 10;

	private final ConcurrentLinkedQueue<BlockPos> foundFishPositions = new ConcurrentLinkedQueue<BlockPos>();

	public MinebotNetHandler(Minecraft mcIn, GuiScreen p_i46300_2_,
			NetworkManager p_i46300_3_, GameProfile p_i46300_4_) {
		super(mcIn, p_i46300_2_, p_i46300_3_, p_i46300_4_);
	}

	@Override
	public void addToSendQueue(Packet p_147297_1_) {
		if (p_147297_1_ instanceof C01PacketChatMessage) {
			C01PacketChatMessage message = (C01PacketChatMessage) p_147297_1_;
			// Intercept chat message.
			String m = message.getMessage();
			if (m.startsWith("/")) {
				if (AIChatController.getRegistry().interceptCommand(m)) {
					return;
				}
			}
		} else if (p_147297_1_ instanceof C14PacketTabComplete) {
			System.out.println("Tab!");
			C14PacketTabComplete complete = (C14PacketTabComplete) p_147297_1_;
			String m = complete.getMessage();
			if (m.startsWith("/") && m.indexOf(" ") >= 0) {
				if (AIChatController.getRegistry().interceptTab(m, this)) {
					return;
				}
			}
		}
		super.addToSendQueue(p_147297_1_);
	}

	@Override
	public void handleTabComplete(S3APacketTabComplete packetIn) {
		// FIXME: Intercept /mine...
		super.handleTabComplete(packetIn);
	}

	public static void inject(AIController aiController,
			NetworkManager manager, INetHandlerPlayClient oldHandler) {
		NetHandlerPlayClient netHandler = (NetHandlerPlayClient) oldHandler;
		if (netHandler != null && netHandler instanceof NetHandlerPlayClient
				&& !(netHandler instanceof MinebotNetHandler)) {
			GuiScreen screen = PrivateFieldUtils.getFieldValue(netHandler,
					NetHandlerPlayClient.class, GuiScreen.class);
			INetHandler handler = new MinebotNetHandler(
					aiController.getMinecraft(), screen,
					netHandler.getNetworkManager(), netHandler.getGameProfile());
			netHandler.getNetworkManager().setNetHandler(handler);
			System.out.println("Minebot network handler injected.");
		}
	}

	@Override
	public void handleParticles(S2APacketParticles packetIn) {
		// For detecting fishing rod events.
		if (packetIn.func_179749_a() == EnumParticleTypes.WATER_SPLASH) {
			if (packetIn.getParticleCount() > 0) {
				double x = packetIn.getXCoordinate();
				double y = packetIn.getYCoordinate();
				double z = packetIn.getZCoordinate();
				//foundFishPositions.add(new BlockPos(x, y, z));
				//System.out.println("NetHandler: fish at " + new BlockPos(x, y, z) + ", packet: " + Arrays.toString(packetIn.getParticleArgs()) + "; " + packetIn.getParticleCount()+ "; " + packetIn.getXOffset()+ "; " + packetIn.getYOffset()+ "; " + packetIn.getZOffset());
			}
		}
		super.handleParticles(packetIn);
	}
	
	@Override
	public void handleEffect(S28PacketEffect packetIn) {
		super.handleEffect(packetIn);
	}
	
	@Override
	public void handleSoundEffect(S29PacketSoundEffect packetIn) {
	    String name = packetIn.func_149212_c();
		if ("random.splash".equals(name)) {
			double x = packetIn.func_149207_d();
			double y = packetIn.func_149211_e();
			double z = packetIn.func_149210_f();
			foundFishPositions.add(new BlockPos(x,y,z));
			System.out.println("NetHandler: fish at " + new BlockPos(x,y,z));
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
				return true;
			} else {
				System.out.println("Found a fish bite at " + next
						+ " but fishing at " + expectedPos + ".");
			}
		}
	}

	public void resetFishState() {
		System.out.println("NetHandler: reset");
		foundFishPositions.clear();
	}
}
