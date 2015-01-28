package net.famzangl.minecraft.minebot.ai.task;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.net.MinebotNetHandler;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.network.play.server.S04PacketEntityEquipment;
import net.minecraft.network.play.server.S05PacketSpawnPosition;
import net.minecraft.network.play.server.S06PacketUpdateHealth;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S09PacketHeldItemChange;
import net.minecraft.network.play.server.S0APacketUseBed;
import net.minecraft.network.play.server.S0BPacketAnimation;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.minecraft.network.play.server.S0DPacketCollectItem;
import net.minecraft.network.play.server.S0EPacketSpawnObject;
import net.minecraft.network.play.server.S0FPacketSpawnMob;
import net.minecraft.network.play.server.S10PacketSpawnPainting;
import net.minecraft.network.play.server.S11PacketSpawnExperienceOrb;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S13PacketDestroyEntities;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.network.play.server.S19PacketEntityHeadLook;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.network.play.server.S1BPacketEntityAttach;
import net.minecraft.network.play.server.S1CPacketEntityMetadata;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S1EPacketRemoveEntityEffect;
import net.minecraft.network.play.server.S1FPacketSetExperience;
import net.minecraft.network.play.server.S20PacketEntityProperties;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.network.play.server.S22PacketMultiBlockChange;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.network.play.server.S24PacketBlockAction;
import net.minecraft.network.play.server.S25PacketBlockBreakAnim;
import net.minecraft.network.play.server.S26PacketMapChunkBulk;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.network.play.server.S28PacketEffect;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.network.play.server.S2CPacketSpawnGlobalEntity;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.network.play.server.S2EPacketCloseWindow;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.network.play.server.S30PacketWindowItems;
import net.minecraft.network.play.server.S31PacketWindowProperty;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.network.play.server.S33PacketUpdateSign;
import net.minecraft.network.play.server.S34PacketMaps;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.network.play.server.S36PacketSignEditorOpen;
import net.minecraft.network.play.server.S37PacketStatistics;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.network.play.server.S39PacketPlayerAbilities;
import net.minecraft.network.play.server.S3APacketTabComplete;
import net.minecraft.network.play.server.S3BPacketScoreboardObjective;
import net.minecraft.network.play.server.S3CPacketUpdateScore;
import net.minecraft.network.play.server.S3DPacketDisplayScoreboard;
import net.minecraft.network.play.server.S3EPacketTeams;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.network.play.server.S41PacketServerDifficulty;
import net.minecraft.network.play.server.S42PacketCombatEvent;
import net.minecraft.network.play.server.S43PacketCamera;
import net.minecraft.network.play.server.S44PacketWorldBorder;
import net.minecraft.network.play.server.S45PacketTitle;
import net.minecraft.network.play.server.S46PacketSetCompressionLevel;
import net.minecraft.network.play.server.S47PacketPlayerListHeaderFooter;
import net.minecraft.network.play.server.S48PacketResourcePackSend;
import net.minecraft.network.play.server.S49PacketUpdateEntityNBT;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IChatComponent;

/**
 * This task right-clicks as soon as a fish bit on the fishing rod. It assumes
 * the player is already holding the rod in water.
 * 
 * @author michael
 *
 */
public class DoFishTask extends AITask {

	private boolean revoked;
	private int rightMotion = 2;
	private boolean inThrowingPhase = true;
	private boolean sendReset = true;

	@Override
	public boolean isFinished(AIHelper h) {
		return revoked || h.getMinecraft().thePlayer.fishEntity == null;
	}

	@Override
	public void runTick(AIHelper h, TaskOperations o) {
		if (fishIsCaptured(h)) {
			h.overrideUseItem();
			revoked = true;
		}
	}

	@Override
	public int getGameTickTimeout() {
		return 1000; // max 900
	}

	private boolean fishIsCaptured(AIHelper helper) {
		// Called in client start thick phase.
		// We know that the next call to the player controller is to check for
		// network packages.
		// Best would be to intercept a wake particle package with i > 1 or e ==
		// g or h == 0.2
		final PlayerControllerMP pc = helper.getMinecraft().playerController;
		final NetworkManager manager = helper.getMinecraft().thePlayer.sendQueue
				.getNetworkManager();
		try {
			Field field = null;
			for (final Field f : manager.getClass().getDeclaredFields()) {
				if (f.getType() == INetHandler.class) {
					field = f;
				}
			}
			field.setAccessible(true);
			final INetHandlerPlayClient oldHandler = (INetHandlerPlayClient) field
					.get(manager);
			if (oldHandler instanceof MinebotNetHandler) {
				MinebotNetHandler minebotNetHandler = (MinebotNetHandler) oldHandler;
				if (sendReset) {
					minebotNetHandler.resetFishState();
					sendReset = false;
				}
				return minebotNetHandler
						.fishIsCaptured(helper.getMinecraft().thePlayer.fishEntity);
			} else {
				System.out.println("No minebot net handler found.");
			}
		} catch (final Throwable e) {
			for (final Field f : manager.getClass().getDeclaredFields()) {
				System.out.println("Field: " + f.getName() + " is "
						+ f.getType());
			}

			e.printStackTrace();
			return false;
		}

		return false;
	}

	/**
	 * Only works in single player. Injecting the net handler is our best
	 * option.
	 * 
	 * @param helper
	 * @return
	 */
	private boolean fishIsCapturedSP(AIHelper helper) {
		final EntityFishHook fishEntity = helper.getMinecraft().thePlayer.fishEntity;
		if (fishEntity == null) {
			return false;
		}
		if (fishEntity.motionY < -0.05) {
			System.out.println(fishEntity.motionY + ", " + fishEntity.posY);
			rightMotion--;
		} else {
			rightMotion = 2;
			inThrowingPhase = false;
		}
		return !inThrowingPhase && rightMotion <= 0;
		// try {
		// Field field = fishEntity.getClass().getDeclaredField(
		// "field_146045_ax");
		// field.setAccessible(true);
		// int value = field.getInt(fishEntity);
		// return value == 0;
		// } catch (Throwable e) {
		// for (Field f : fishEntity.getClass().getDeclaredFields()) {
		// System.out.println("Field: " + f.getName() + " is "
		// + f.getType());
		// }
		//
		// e.printStackTrace();
		// return false;
		// }
	}

}
