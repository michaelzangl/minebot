package net.famzangl.minecraft.minebot.ai.animals;

import java.lang.reflect.Field;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.network.EnumConnectionState;
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
import net.minecraft.util.IChatComponent;

public class DoFishTask implements AITask {

	private boolean revoked;
	private int rightMotion = 2;
	private boolean inThrowingPhase = true;

	@Override
	public boolean isFinished(AIHelper h) {
		return revoked || h.getMinecraft().thePlayer.fishEntity == null;
	}

	@Override
	public void runTick(AIHelper h) {
		if (fishIsCaptured(h)) {
			h.overrideUseItem();
			revoked = true;
		}
	}

	private class MyNetHandler implements INetHandlerPlayClient {
		INetHandlerPlayClient root;
		private boolean foundFish;

		public MyNetHandler(INetHandlerPlayClient root) {
			super();
			this.root = root;
		}

		@Override
		public void onDisconnect(IChatComponent var1) {
			root.onDisconnect(var1);
		}

		@Override
		public void onConnectionStateTransition(EnumConnectionState var1,
				EnumConnectionState var2) {
			root.onConnectionStateTransition(var1, var2);
		}

		@Override
		public void onNetworkTick() {
			root.onNetworkTick();
		}

		@Override
		public void handleSpawnObject(S0EPacketSpawnObject var1) {
			root.handleSpawnObject(var1);
		}

		@Override
		public void handleSpawnExperienceOrb(S11PacketSpawnExperienceOrb var1) {
			root.handleSpawnExperienceOrb(var1);
		}

		@Override
		public void handleSpawnGlobalEntity(S2CPacketSpawnGlobalEntity var1) {
			root.handleSpawnGlobalEntity(var1);
		}

		@Override
		public void handleSpawnMob(S0FPacketSpawnMob var1) {
			root.handleSpawnMob(var1);
		}

		@Override
		public void handleScoreboardObjective(S3BPacketScoreboardObjective var1) {
			root.handleScoreboardObjective(var1);
		}

		@Override
		public void handleSpawnPainting(S10PacketSpawnPainting var1) {
			root.handleSpawnPainting(var1);
		}

		@Override
		public void handleSpawnPlayer(S0CPacketSpawnPlayer var1) {
			root.handleSpawnPlayer(var1);
		}

		@Override
		public void handleAnimation(S0BPacketAnimation var1) {
			root.handleAnimation(var1);
		}

		@Override
		public void handleStatistics(S37PacketStatistics var1) {
			root.handleStatistics(var1);
		}

		@Override
		public void handleBlockBreakAnim(S25PacketBlockBreakAnim var1) {
			root.handleBlockBreakAnim(var1);
		}

		@Override
		public void handleSignEditorOpen(S36PacketSignEditorOpen var1) {
			root.handleSignEditorOpen(var1);
		}

		@Override
		public void handleUpdateTileEntity(S35PacketUpdateTileEntity var1) {
			root.handleUpdateTileEntity(var1);
		}

		@Override
		public void handleBlockAction(S24PacketBlockAction var1) {
			root.handleBlockAction(var1);
		}

		@Override
		public void handleBlockChange(S23PacketBlockChange var1) {
			root.handleBlockChange(var1);
		}

		@Override
		public void handleChat(S02PacketChat var1) {
			root.handleChat(var1);
		}

		@Override
		public void handleTabComplete(S3APacketTabComplete var1) {
			root.handleTabComplete(var1);
		}

		@Override
		public void handleMultiBlockChange(S22PacketMultiBlockChange var1) {
			root.handleMultiBlockChange(var1);
		}

		@Override
		public void handleMaps(S34PacketMaps var1) {
			root.handleMaps(var1);
		}

		@Override
		public void handleConfirmTransaction(S32PacketConfirmTransaction var1) {
			root.handleConfirmTransaction(var1);
		}

		@Override
		public void handleCloseWindow(S2EPacketCloseWindow var1) {
			root.handleCloseWindow(var1);
		}

		@Override
		public void handleWindowItems(S30PacketWindowItems var1) {
			root.handleWindowItems(var1);
		}

		@Override
		public void handleOpenWindow(S2DPacketOpenWindow var1) {
			root.handleOpenWindow(var1);
		}

		@Override
		public void handleWindowProperty(S31PacketWindowProperty var1) {
			root.handleWindowProperty(var1);
		}

		@Override
		public void handleSetSlot(S2FPacketSetSlot var1) {
			root.handleSetSlot(var1);
		}

		@Override
		public void handleCustomPayload(S3FPacketCustomPayload var1) {
			root.handleCustomPayload(var1);
		}

		@Override
		public void handleDisconnect(S40PacketDisconnect var1) {
			root.handleDisconnect(var1);
		}

		@Override
		public void handleUseBed(S0APacketUseBed var1) {
			root.handleUseBed(var1);
		}

		@Override
		public void handleEntityStatus(S19PacketEntityStatus var1) {
			root.handleEntityStatus(var1);
		}

		@Override
		public void handleEntityAttach(S1BPacketEntityAttach var1) {
			root.handleEntityAttach(var1);
		}

		@Override
		public void handleExplosion(S27PacketExplosion var1) {
			root.handleExplosion(var1);
		}

		@Override
		public void handleChangeGameState(S2BPacketChangeGameState var1) {
			root.handleChangeGameState(var1);
		}

		@Override
		public void handleKeepAlive(S00PacketKeepAlive var1) {
			root.handleKeepAlive(var1);
		}

		@Override
		public void handleChunkData(S21PacketChunkData var1) {
			root.handleChunkData(var1);
		}

		@Override
		public void handleMapChunkBulk(S26PacketMapChunkBulk var1) {
			root.handleMapChunkBulk(var1);
		}

		@Override
		public void handleEffect(S28PacketEffect var1) {
			root.handleEffect(var1);
		}

		@Override
		public void handleJoinGame(S01PacketJoinGame var1) {
			root.handleJoinGame(var1);
		}

		@Override
		public void handleEntityMovement(S14PacketEntity var1) {
			root.handleEntityMovement(var1);
		}

		@Override
		public void handlePlayerPosLook(S08PacketPlayerPosLook var1) {
			root.handlePlayerPosLook(var1);
		}

		@Override
		public void handleParticles(S2APacketParticles var1) {
			if ("wake".equals(var1.func_149228_c())) {
				System.out.println("Got the particles! i= " + var1.func_149222_k());
				if (var1.func_149222_k() > 0) {
					foundFish = true;
				}
			}
			root.handleParticles(var1);
		}

		@Override
		public void handlePlayerAbilities(S39PacketPlayerAbilities var1) {
			root.handlePlayerAbilities(var1);
		}

		@Override
		public void handlePlayerListItem(S38PacketPlayerListItem var1) {
			root.handlePlayerListItem(var1);
		}

		@Override
		public void handleDestroyEntities(S13PacketDestroyEntities var1) {
			root.handleDestroyEntities(var1);
		}

		@Override
		public void handleRemoveEntityEffect(S1EPacketRemoveEntityEffect var1) {
			root.handleRemoveEntityEffect(var1);
		}

		@Override
		public void handleRespawn(S07PacketRespawn var1) {
			root.handleRespawn(var1);
		}

		@Override
		public void handleEntityHeadLook(S19PacketEntityHeadLook var1) {
			root.handleEntityHeadLook(var1);
		}

		@Override
		public void handleHeldItemChange(S09PacketHeldItemChange var1) {
			root.handleHeldItemChange(var1);
		}

		@Override
		public void handleDisplayScoreboard(S3DPacketDisplayScoreboard var1) {
			root.handleDisplayScoreboard(var1);
		}

		@Override
		public void handleEntityMetadata(S1CPacketEntityMetadata var1) {
			root.handleEntityMetadata(var1);
		}

		@Override
		public void handleEntityVelocity(S12PacketEntityVelocity var1) {
			root.handleEntityVelocity(var1);
		}

		@Override
		public void handleEntityEquipment(S04PacketEntityEquipment var1) {
			root.handleEntityEquipment(var1);
		}

		@Override
		public void handleSetExperience(S1FPacketSetExperience var1) {
			root.handleSetExperience(var1);
		}

		@Override
		public void handleUpdateHealth(S06PacketUpdateHealth var1) {
			root.handleUpdateHealth(var1);
		}

		@Override
		public void handleTeams(S3EPacketTeams var1) {
			root.handleTeams(var1);
		}

		@Override
		public void handleUpdateScore(S3CPacketUpdateScore var1) {
			root.handleUpdateScore(var1);
		}

		@Override
		public void handleSpawnPosition(S05PacketSpawnPosition var1) {
			root.handleSpawnPosition(var1);
		}

		@Override
		public void handleTimeUpdate(S03PacketTimeUpdate var1) {
			root.handleTimeUpdate(var1);
		}

		@Override
		public void handleUpdateSign(S33PacketUpdateSign var1) {
			root.handleUpdateSign(var1);
		}

		@Override
		public void handleSoundEffect(S29PacketSoundEffect var1) {
			root.handleSoundEffect(var1);
		}

		@Override
		public void handleCollectItem(S0DPacketCollectItem var1) {
			root.handleCollectItem(var1);
		}

		@Override
		public void handleEntityTeleport(S18PacketEntityTeleport var1) {
			root.handleEntityTeleport(var1);
		}

		@Override
		public void handleEntityProperties(S20PacketEntityProperties var1) {
			root.handleEntityProperties(var1);
		}

		@Override
		public void handleEntityEffect(S1DPacketEntityEffect var1) {
			root.handleEntityEffect(var1);
		}

		public boolean fishIsCaptured() {
			boolean foundFish2 = foundFish;
			foundFish = false;
			return foundFish2;
		}
	}

	private boolean fishIsCaptured(AIHelper helper) {
		// Called in client start thick phase.
		// We know that the next call to the player controller is to check for
		// network packages.
		// Best would be to intercept a wake particle package with i > 1 or e ==
		// g or h == 0.2
		PlayerControllerMP pc = helper.getMinecraft().playerController;
		NetworkManager manager = helper.getMinecraft().thePlayer.sendQueue
				.getNetworkManager();
		try {
			Field field = null;
			for (Field f : manager.getClass().getDeclaredFields()) {
				if (f.getType() == INetHandler.class) {
					field = f;
				}
			}
			field.setAccessible(true);
			INetHandlerPlayClient oldHandler = (INetHandlerPlayClient) field
					.get(manager);
			if (oldHandler instanceof NetHandlerPlayClient) {
				System.out.println("Injecting new network handler.");
				INetHandlerPlayClient newHandler = new MyNetHandler(oldHandler);
				field.set(manager, newHandler);
			} else {
				MyNetHandler myHandler = (MyNetHandler) oldHandler;
				return myHandler.fishIsCaptured();
			}
		} catch (Throwable e) {
			for (Field f : manager.getClass().getDeclaredFields()) {
				System.out.println("Field: " + f.getName() + " is "
						+ f.getType());
			}

			e.printStackTrace();
			return false;
		}

		return false;
	}

	private boolean fishIsCapturedSP(AIHelper helper) {
		EntityFishHook fishEntity = helper.getMinecraft().thePlayer.fishEntity;
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
