package net.famzangl.minecraft.minebot;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;

public class PlantKeyHandler {

	private static final int X_D = 5;

	private KeyBinding plant = new KeyBinding("Plant",
			Keyboard.getKeyIndex("P"), "Command Mod");

	private static Minecraft mc = Minecraft.getMinecraft();

	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent evt) {
		if (plant.getIsKeyPressed()) {
			if (!plantOnBlock()) {
				if (!faceEmptyBlock()) {
					lookNorth();
				}
			}
		}
	}

	private void lookNorth() {
		float rotationYaw = mc.thePlayer.rotationYaw;
		float rotationPitch = mc.thePlayer.rotationPitch;
		int yaw = 180;
		int pitch = 0;
		System.out.println("Move to yaw = " + yaw + ", pitch=" + pitch
				+ "(currently: " + rotationYaw + "," + rotationPitch + ")");
		mc.thePlayer.setAngles((yaw - rotationYaw) / 0.15f,
				-(pitch - rotationPitch) / 0.15f);
	}

	private boolean faceEmptyBlock() {
		int cx = MathHelper.floor_double(mc.thePlayer.posX);
		int cy = MathHelper.floor_double(mc.thePlayer.posY);
		int cz = MathHelper.floor_double(mc.thePlayer.posZ);
		int posX = Integer.MIN_VALUE;
		int posY = Integer.MIN_VALUE;
		int posZ = Integer.MIN_VALUE;
		for (int x = cx - X_D; x <= cx + X_D; x++) {
			for (int z = cz - X_D; z <= cz + X_D; z++) {
				for (int y = cy - 3; y <= cy; y++) {
					if (mc.thePlayer.getDistance(x + 0.5, y + 1, z + 0.5) > 3.5) {
						continue;
					}
					Block block = mc.theWorld.getBlock(x, y, z);
					if (isPlantable(block, x, y, z)) {
						posX = x;
						posY = y;
						posZ = z;
					}
				}
			}
		}
		if (posZ == Integer.MIN_VALUE) {
			System.out.println("Could not find other plantable block around ("
					+ cx + "," + cy + "," + cz + ").");
			return false;
		}

		face(posX + 0.5, posY + 1, posZ + 0.5);
		return true;
	}

	private boolean plantOnBlock() {
		ItemStack itemstack = mc.thePlayer.inventory.getCurrentItem();
		MovingObjectPosition objectMouseOver = mc.objectMouseOver;
		if (objectMouseOver == null
				|| objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) {
			System.out.println("Not facing any block.");
			return false;
		}
		Block block = mc.theWorld.getBlock(objectMouseOver.blockX,
				objectMouseOver.blockY, objectMouseOver.blockZ);
		if (!isPlantable(block, objectMouseOver.blockX, objectMouseOver.blockY,
				objectMouseOver.blockZ)) {
			System.out.println("Not plantable here.");
			return false;
		}

		mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld,
				itemstack, objectMouseOver.blockX, objectMouseOver.blockY,
				objectMouseOver.blockZ, objectMouseOver.sideHit,
				objectMouseOver.hitVec);
		return true;
	}

	private boolean isPlantable(Block block, int x, int y, int z) {
		/*
		 * ItemStack currentItem = mc.thePlayer.inventory.getCurrentItem(); if
		 * (currentItem != null && (Object) currentItem instanceof IPlantable &&
		 * block.canSustainPlant(mc.theWorld, x, y, z, ForgeDirection.UP,
		 * (IPlantable) (Object) currentItem)) { return true; }
		 */
		return Block.isEqualTo(block, Blocks.farmland)
				&& mc.theWorld.getBlock(x, y + 1, z).isAir(mc.theWorld, x,
						y + 1, z);
	}

	private void face(double x, double y, double z) {
		double d0 = x - mc.thePlayer.posX;
		double d1 = z - mc.thePlayer.posZ;
		double d2 = y - mc.thePlayer.posY;
		double d3 = d0 * d0 + d2 * d2 + d1 * d1;

		if (d3 >= 2.500000277905201E-7D) {
			float rotationYaw = mc.thePlayer.rotationYaw;
			float rotationPitch = mc.thePlayer.rotationPitch;

			float yaw = (float) (Math.atan2(d1, d0) * 180.0D / Math.PI) - 90.0F;
			float pitch = (float) -(Math
					.atan2(d2, Math.sqrt(d0 * d0 + d1 * d1)) * 180.0D / Math.PI);
			System.out.println("Move to yaw = " + yaw + ", pitch=" + pitch
					+ "(currently: " + rotationYaw + "," + rotationPitch + ")");
			mc.thePlayer.setAngles((yaw - rotationYaw) / 0.15f,
					-(pitch - rotationPitch) / 0.15f);
		}
	}

}
