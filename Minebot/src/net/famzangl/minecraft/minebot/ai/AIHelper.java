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
package net.famzangl.minecraft.minebot.ai;

import java.io.File;
import java.util.List;
import java.util.Random;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.command.AIChatController;
import net.famzangl.minecraft.minebot.ai.net.MinebotNetHandler;
import net.famzangl.minecraft.minebot.ai.net.NetworkHelper;
import net.famzangl.minecraft.minebot.ai.path.world.WorldData;
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy;
import net.famzangl.minecraft.minebot.ai.task.BlockSide;
import net.famzangl.minecraft.minebot.build.BuildManager;
import net.famzangl.minecraft.minebot.map.MapReader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockWall;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovementInput;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

import com.google.common.base.Predicate;

/**
 * Lots and lots of helpful methods to control the current player. This contains
 * the current marked positions ({@link #pos1}, {@link #pos2}), fast methods to
 * access/check minecraft blocks ({@link #getBlockId(BlockPos)}), many lists of
 * different block types/presets useful for filtering, methods to control the
 * player ({@link #face(double, double, double)}, {@link #overrideUseItem()}),
 * some methods to get the player state ({@link #isAlive()},
 * {@link #getPlayerPosition()}) and some to simplify block handling (
 * {@link #getSignDirection(IBlockState)})
 * 
 * @author michael
 * 
 */
public abstract class AIHelper {
	private static final double SNEAK_OFFSET = .2;
	private static final double WALK_PER_STEP = 4.3 / 20;
	private static final double MIN_DISTANCE_ERROR = 0.05;
	private static final float MAX_PITCH_CHANGE = 20.0f;
	private static final float MAX_YAW_CHANGE = (float) (Math.PI / 6);
	private static Minecraft mc = Minecraft.getMinecraft();
	/**
	 * A world that never gets a delta applied to it.
	 */
	private static WorldData minecraftWorld;
	private final Random rand = new Random();

	public final BuildManager buildManager = new BuildManager();
	private boolean objectMouseOverInvalidated;

	protected BlockPos pos1 = null;
	protected BlockPos pos2 = null;

	private MovementInput resetMovementInput;
	private KeyBinding resetAttackKey;
	private KeyBinding resetUseItemKey;
	private boolean useItemKeyJustPressed;
	private boolean attackKeyJustPressed;
	protected boolean doUngrab;
	private KeyBinding resetSneakKey;
	private boolean sneakKeyJustPressed;
	private KeyBinding resetSprintKey;
	private boolean sprintKeyJustPressed;

	protected MapReader activeMapReader;

	/**
	 * A random number in a range, that does not exactly hit the sides.
	 * 
	 * @param minY
	 * @param maxY
	 * @return
	 */
	private double randBetweenNice(double minY, double maxY) {
		return maxY - minY < 0.1 ? (maxY + minY) / 2 : randBetween(minY + 0.03,
				maxY - 0.03);
	}

	private double randBetween(double a, double b) {
		return rand.nextDouble() * (b - a) + a;
	}

	protected void invalidateChunkCache() {
		if (minecraftWorld == null || mc.theWorld != minecraftWorld.getBackingWorld()) {
			minecraftWorld = mc.theWorld == null ? null : new WorldData(mc.theWorld, mc.thePlayer); 
		}
		if (minecraftWorld != null) {
			minecraftWorld.invalidateChunkCache();
		}
	}

	public Minecraft getMinecraft() {
		return mc;
	}

	public static File getMinebotDir() {
		File dir = new File(mc.mcDataDir, "minebot");
		if (!dir.exists()) {
			dir.mkdir();
		}
		return dir;
	}

	/**
	 * Gets the pos1 marker.
	 * 
	 * @return The marker or <code>null</code> if it is unset.
	 */
	public BlockPos getPos1() {
		return pos1;
	}

	/**
	 * Gets the pos2 marker.
	 * 
	 * @return The marker or <code>null</code> if it is unset.
	 */
	public BlockPos getPos2() {
		return pos2;
	}

	/**
	 * Sets the pos1 or pos2 marker.
	 * 
	 * @param pos
	 *            The new position.
	 * @param isPos2
	 */
	public void setPosition(BlockPos pos, boolean isPos2) {
		int posIndex;
		if (isPos2) {
			pos2 = pos;
			posIndex = 2;
		} else {
			pos1 = pos;
			posIndex = 1;
		}
		AIChatController.addChatLine("Set position" + posIndex + " to " + pos);
	}

	/**
	 * Needs to be called whenever the player moved and the block faced might
	 * have changed.
	 */
	public void invalidateObjectMouseOver() {
		objectMouseOverInvalidated = true;
	}

	/**
	 * Gets the object the player is currently facing. This should always be
	 * used instead of the Minecraft version, since it handles player movement
	 * updates in the same game tick.
	 * 
	 * @return
	 */
	public MovingObjectPosition getObjectMouseOver() {
		if (objectMouseOverInvalidated) {
			objectMouseOverInvalidated = false;
			mc.entityRenderer.getMouseOver(1.0F);
		}
		return mc.objectMouseOver;
	}

	/**
	 * Gets the block at that position.
	 * 
	 * @param pos
	 * @return
	 */
	public Block getBlock(BlockPos pos) {
		//TODO: Warn that no delta is used.
		return getBlock(pos.getX(), pos.getY(), pos.getZ());
	}

	/**
	 * Gets the block at that position.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return The block.
	 */
	public Block getBlock(int x, int y, int z) {
		//TODO: Warn that no delta is used.
		return mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
	}

	public WorldData getWorld() {
		return minecraftWorld;
	}
	
	/**
	 * Check if this is basically a block that cannot harm us if it is next to
	 * us.
	 * 
	 * @param block
	 * @return
	 */
	// public boolean isSafeBlock(Block block) {
	// return isSafeStandableBlock(block) || canWalkOn(block)
	// || Block.isEqualTo(block, Blocks.air);
	// }

	/**
	 * Check if this is a Block we could walk through as if it was air.
	 * 
	 * @param block
	 * @return
	 */
//	public boolean canWalkThrough(Block block) {
//		return HEAD_CAN_WALK_TRHOUGH.contains(block);
//	}

	/**
	 * Check if the block is a block that we could walk on as if it was air.
	 * Carpets, torches, ...
	 * 
	 * @param block
	 * @return
	 */
//	public boolean canWalkOn(Block block) {
//		return FEET_CAN_WALK_THROUGH.contains(block);
//	}

	/**
	 * Check if we can stand on the block.
	 * 
	 * @param block
	 * @return
	 */
//	public boolean isSafeStandableBlock(Block block) {
//		return SAFE_GROUND.contains(block);
//	}


	private Block getBoundsBlock(BlockPos pos) {
		Block block = getBlock(pos);
		if (block instanceof BlockStairs) {
			// Stairs have crazy bounds
			block = Blocks.dirt;
		}
		block.setBlockBoundsBasedOnState(mc.theWorld, pos);
		return block;
	}

	/**
	 * Faces an exact position in space.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public void face(double x, double y, double z) {
		face(x, y, z, 1, 1);
	}

	private void face(double x, double y, double z, float yawInfluence, float pitchInfluence) {
		final double d0 = x - mc.thePlayer.posX;
		final double d1 = z - mc.thePlayer.posZ;
		final double d2 = y - mc.thePlayer.posY - mc.thePlayer.getEyeHeight();
		final double d3 = d0 * d0 + d2 * d2 + d1 * d1;

		if (d3 >= 2.500000277905201E-7D) {
			final float rotationYaw = mc.thePlayer.rotationYaw;
			final float rotationPitch = mc.thePlayer.rotationPitch;

			final float yaw = (float) (Math.atan2(d1, d0) * 180.0D / Math.PI) - 90.0F;
			final float pitch = (float) -(Math.atan2(d2,
					Math.sqrt(d0 * d0 + d1 * d1)) * 180.0D / Math.PI);
			float rotations = fullRotations(yaw - rotationYaw);
			float yawChange = yaw - rotationYaw - rotations;
			float pitchChange = pitch - rotationPitch;
			yawInfluence = Math.min(yawInfluence, Math.min(Math.abs(MAX_YAW_CHANGE / yawChange), 1));
			pitchInfluence = Math.min(pitchInfluence, Math.min(Math.abs(MAX_PITCH_CHANGE / pitchChange), 1));
			System.out.println("Change: " + yawChange + "," + pitchChange +" => " + yawInfluence + "," + pitchInfluence );

			mc.thePlayer.setAngles(rotations / .15f + yawChange / 0.15f * yawInfluence,
					-pitchChange / 0.15f * pitchInfluence);
			invalidateObjectMouseOver();
		}
	}
	
	private float fullRotations(float yaw) {
		return (float) (((int)(yaw / (Math.PI * 2) )) * Math.PI * 2);
	}

	/*
	 * public void moveTo(int x, int y, int z) { face(x + .5, mc.thePlayer.posY,
	 * z + .5); MovementInput i = new MovementInput(); i.moveForward = 0.8f;
	 * overrideMovement(i); }
	 */

	public boolean isFacingBlock(BlockPos pos) {
		return isFacingBlock(pos.getX(), pos.getY(), pos.getZ());
	}

	/**
	 * Checks if the player is currently facing the block so that it can be
	 * interacted with.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return <code>true</code> if it is facing the Block.
	 */
	public boolean isFacingBlock(int x, int y, int z) {
		final MovingObjectPosition o = getObjectMouseOver();
		return o != null && o.typeOfHit == MovingObjectType.BLOCK
				&& new BlockPos(x, y, z).equals(o.getBlockPos());
	}

	public boolean isFacingBlock(BlockPos pos, EnumFacing blockSide,
			BlockSide half) {
		return isFacingBlock(pos.getX(), pos.getY(), pos.getZ(), blockSide,
				half);
	}

	/**
	 * Checks if the player is facing a specific side of the block.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param blockSide
	 *            The side of the block.
	 * @param half
	 *            Can restrict the check to the upper or lower half of the side
	 *            (not useful for top/bottom)
	 * @return <code>true</code> if the player faces the block.
	 */
	public boolean isFacingBlock(int x, int y, int z, EnumFacing blockSide,
			BlockSide half) {
		if (!isFacingBlock(x, y, z, blockSide)) {
			return false;
		} else {
			final double fy = getObjectMouseOver().hitVec.yCoord - y;
			return half != BlockSide.LOWER_HALF && fy > .5
					|| half != BlockSide.UPPER_HALF && fy <= .5;
		}
	}

	public boolean isFacingBlock(BlockPos pos, EnumFacing side) {
		return isFacingBlock(pos.getX(), pos.getY(), pos.getZ(), side);
	}

	/**
	 * Checks if the player is facing a specific side of the block.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param blockSide
	 *            The side of the block as game integer.
	 * @return <code>true</code> if the player faces the block.
	 */
	public boolean isFacingBlock(int x, int y, int z, EnumFacing side) {
		final MovingObjectPosition o = getObjectMouseOver();
		return o != null && o.typeOfHit == MovingObjectType.BLOCK
				&& new BlockPos(x, y, z).equals(o.getBlockPos())
				&& o.sideHit == side;
	}

	public boolean isStandingOn(BlockPos pos) {
		return isStandingOn(pos.getX(), pos.getY(), pos.getZ());
	}

	/**
	 * Are the feet standing on that block?
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public boolean isStandingOn(int x, int y, int z) {
		// boolean isFence = blockIsOneOf(getBlock(x, y - 1, z),
		// FenceBuildTask.BLOCKS);
		return Math.abs(x + 0.5 - mc.thePlayer.posX) < 0.2
				&& Math.abs(z + 0.5 - mc.thePlayer.posZ) < 0.2
				&& Math.abs(mc.thePlayer.getEntityBoundingBox().minY - y) < 0.52;
	}

	public double realBlockTopY(BlockPos pos) {
		return realBlockTopY(pos.getX(), pos.getY(), pos.getZ());
	}

	/**
	 * The real top y cord of the block we stand on. Watch out: Minecraft blocks
	 * do not always provide the right bounds with it's block objects.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public double realBlockTopY(int x, int y, int z) {
		final Block block = getBlock(x, y - 1, z);
		// Fence bounds are not exposed...
		double maxY;
		if (block instanceof BlockFence || block instanceof BlockFenceGate
				|| block instanceof BlockWall) {
			maxY = 1.5;
		} else if (block instanceof BlockSlab) {
			final int blockMetadata = getWorld().getBlockIdWithMeta(x, y, z) & 0xf;
			maxY = (blockMetadata & 0x8) == 0 ? 0.5 : 1;
		} else {
			maxY = block.getBlockBoundsMaxY();
		}

		return y - 1 + maxY;
	}

	/**
	 * Get the current position the player is at.
	 * 
	 * @return The position.
	 */
	public BlockPos getPlayerPosition() {
		return getWorld().getPlayerPosition();
	}

	/*
	 * public void moveTo(int x, int y, int z) { face(x + .5, mc.thePlayer.posY,
	 * z + .5); MovementInput i = new MovementInput(); i.moveForward = 0.8f;
	 * overrideMovement(i); }
	 */

	/**
	 * Checks if an item is on the hotbar.
	 * 
	 * @param f
	 *            The item to search
	 * @return <code>true</code> if it is selectable.
	 */
	public boolean canSelectItem(ItemFilter f) {
		for (int i = 0; i < 9; ++i) {
			if (f.matches(mc.thePlayer.inventory.getStackInSlot(i))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Selects an item.
	 * 
	 * @param f
	 *            The item to search for.
	 * @return <code>true</code> if the player is now holding that item.
	 */
	public boolean selectCurrentItem(ItemFilter f) {
		if (f.matches(mc.thePlayer.inventory.getCurrentItem())) {
			return true;
		}
		for (int i = 0; i < 9; ++i) {
			if (f.matches(mc.thePlayer.inventory.getStackInSlot(i))) {
				mc.thePlayer.inventory.currentItem = i;
				return true;
			}
		}
		return false;
	}

	/**
	 * Selects a good tool for mining the given Block.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public void selectToolFor(final BlockPos pos) {
		selectCurrentItem(new ItemFilter() {
			@Override
			public boolean matches(ItemStack itemStack) {
				return itemStack != null
						&& itemStack.getItem() != null
						&& itemStack.getItem().getStrVsBlock(itemStack,
								getBlock(pos)) > 1;
			}
		});
	}

	/**
	 * Faces a block and destroys it if possible.
	 * 
	 * @param pos the Position of that block.
	 */
	public void faceAndDestroy(final BlockPos pos) {
		if (!isFacingBlock(pos)) {
			faceBlock(pos);
		}

		if (isFacingBlock(pos)) {
			selectToolFor(pos);
			overrideAttack();
		}
	}

	public void faceAndDestroyWithHangingBlock(final BlockPos pos) {
		faceAndDestroy(pos);
		if (!isFacingBlock(pos)) {
			// Check if there is a block hanging here.
			for (EnumFacing d : EnumFacing.values()) {
				BlockPos offseted = pos.offset(d);
				if (isFacingBlock(offseted)) {
					BlockPos hanging = getWorld().getHangingOnBlock(offseted);
					if (hanging != null && hanging.equals(pos)) {
						overrideAttack();
					}
				}
			}
		}
	}

	public void faceBlock(BlockPos pos) {
		faceBlock(pos.getX(), pos.getY(), pos.getZ());
	}

	/**
	 * Faces a full block.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public void faceBlock(final int x, final int y, final int z) {
		face(x + randBetween(0.1, 0.9), y + randBetween(0.1, 0.9), z
				+ randBetween(0.1, 0.9));
	}

	/**
	 * Face the side of a block.
	 * 
	 * @param pos
	 * @param sideToFace
	 */
	public void faceSideOf(BlockPos pos, EnumFacing sideToFace) {
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		double faceX = x + randBetween(0.1, 0.9);
		double faceY = y + randBetween(0.1, 0.9);
		double faceZ = z + randBetween(0.1, 0.9);

		final Block block = getBoundsBlock(pos);
		switch (sideToFace) {
		case UP:
			faceY = y + block.getBlockBoundsMaxY();
			break;
		case DOWN:
			faceY = y + block.getBlockBoundsMinY();
			break;
		case EAST:
			faceX = x + block.getBlockBoundsMaxX();
			break;
		case WEST:
			faceX = x + block.getBlockBoundsMinX();
			break;
		case SOUTH:
			faceZ = z + block.getBlockBoundsMaxZ();
			break;
		case NORTH:
			faceZ = z + block.getBlockBoundsMinZ();
			break;
		default:
			break;
		}
		face(faceX, faceY, faceZ);
	}

	/**
	 * Advanced version to face a specific side of a block.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param sideToFace
	 *            To restrict facing on a given side. Can be <code>null</code>
	 *            to disable.
	 * @param minY
	 *            Limits the Y coordinate that is faced.
	 * @param maxY
	 *            Limits the Y coordinate that is faced.
	 * @param centerX
	 * @param centerZ
	 * @param xzdir
	 *            The direction the xz restriction affects.
	 */
	public void faceSideOf(BlockPos pos, EnumFacing sideToFace, double minY,
			double maxY, double centerX, double centerZ, EnumFacing xzdir) {
		// System.out.println("x = " + x + " y=" + y + " z=" + z + " dir="
		// + sideToFace);
		final Block block = getBoundsBlock(pos);

		minY = Math.max(minY, block.getBlockBoundsMinY());
		maxY = Math.min(maxY, block.getBlockBoundsMaxY());
		double faceY = randBetweenNice(minY, maxY);
		double faceX, faceZ;

		if (xzdir == EnumFacing.EAST) {
			faceX = randBetween(Math.max(block.getBlockBoundsMinX(), centerX),
					block.getBlockBoundsMaxX());
			faceZ = centerZ;
		} else if (xzdir == EnumFacing.WEST) {
			faceX = randBetween(block.getBlockBoundsMinX(),
					Math.min(block.getBlockBoundsMaxX(), centerX));
			faceZ = centerZ;
		} else if (xzdir == EnumFacing.SOUTH) {
			faceZ = randBetween(Math.max(block.getBlockBoundsMinZ(), centerZ),
					block.getBlockBoundsMaxZ());
			faceX = centerX;
		} else if (xzdir == EnumFacing.NORTH) {
			faceZ = randBetween(block.getBlockBoundsMinZ(),
					Math.min(block.getBlockBoundsMaxZ(), centerZ));
			faceX = centerX;
		} else {
			faceX = randBetweenNice(block.getBlockBoundsMinX(),
					block.getBlockBoundsMaxX());
			faceZ = randBetweenNice(block.getBlockBoundsMinZ(),
					block.getBlockBoundsMaxZ());
		}
		switch (sideToFace) {
		case UP:
			faceY = block.getBlockBoundsMaxY();
			break;
		case DOWN:
			faceY = block.getBlockBoundsMinY();
			break;
		case EAST:
			faceX = block.getBlockBoundsMaxX();
			break;
		case WEST:
			faceX = block.getBlockBoundsMinX();
			break;
		case SOUTH:
			faceZ = block.getBlockBoundsMaxZ();
			break;
		case NORTH:
			faceZ = block.getBlockBoundsMinZ();
			break;
		default:
			break;
		}
		face(faceX + pos.getX(), faceY + pos.getY(), faceZ + pos.getZ());
	}

	/**
	 * Checks if an item is in the main inventory.
	 * 
	 * @param itemFiler
	 * @return
	 */
	public boolean hasItemInInvetory(ItemFilter itemFiler) {
		for (final ItemStack s : mc.thePlayer.inventory.mainInventory) {
			if (itemFiler.matches(s)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * overrides the keyboard input in the next game tick.
	 * 
	 * @param i
	 */
	public void overrideMovement(MovementInput i) {
		if (resetMovementInput == null) {
			resetMovementInput = mc.thePlayer.movementInput;
		}
		mc.thePlayer.movementInput = i;
	}

	/**
	 * Presses the use item key in the next game tick.
	 */
	public void overrideUseItem() {
		if (resetUseItemKey == null) {
			resetUseItemKey = mc.gameSettings.keyBindUseItem;
			// useItemKeyJustPressed |= resetUseItemKey.getIsKeyPressed();
		}
		mc.gameSettings.keyBindUseItem = new InteractAlways(
				mc.gameSettings.keyBindAttack.getKeyDescription(), 501,
				mc.gameSettings.keyBindAttack.getKeyCategory(),
				!useItemKeyJustPressed);
	}

	/**
	 * Presses the attack key in the next game tick.
	 */
	public void overrideAttack() {
		if (mc.thePlayer.isUsingItem()) {
			System.err
					.println("WARNING: Player is currently using an item, but attack was requested.");
		}
		if (resetAttackKey == null) {
			resetAttackKey = mc.gameSettings.keyBindAttack;
			// if (resetAttackKey.getIsKeyPressed()) {
			// System.out.println("Attack was pressed.");
			// }
			// This just made problems...
			// attackKeyJustPressed |= resetAttackKey.getIsKeyPressed();
		}
		mc.gameSettings.keyBindAttack = new InteractAlways(
				mc.gameSettings.keyBindAttack.getKeyDescription(), 502,
				mc.gameSettings.keyBindAttack.getKeyCategory(),
				!attackKeyJustPressed);
	}

	/**
	 * Presses the sneak key in the next game step.
	 */
	public void overrideSneak() {
		if (resetSneakKey == null) {
			resetSneakKey = mc.gameSettings.keyBindSneak;
			// sneakKeyJustPressed |= resetSneakKey.getIsKeyPressed();
		}
		mc.gameSettings.keyBindSneak = new InteractAlways(
				mc.gameSettings.keyBindSneak.getKeyDescription(), 503,
				mc.gameSettings.keyBindSneak.getKeyCategory(),
				!sneakKeyJustPressed);
	}

	/**
	 * Presses the sneak key in the next game step.
	 */
	public void overrideSprint() {
		if (resetSprintKey == null) {
			resetSprintKey = mc.gameSettings.keyBindSprint;
			// sneakKeyJustPressed |= resetSneakKey.getIsKeyPressed();
		}
		mc.gameSettings.keyBindSprint = new InteractAlways(
				mc.gameSettings.keyBindSprint.getKeyDescription(), 504,
				mc.gameSettings.keyBindSprint.getKeyCategory(),
				!sprintKeyJustPressed);
	}

	/**
	 * Restore all inputs to the default minecraft inputs.
	 */
	protected void resetAllInputs() {
		if (resetMovementInput != null) {
			mc.thePlayer.movementInput = resetMovementInput;
			resetMovementInput = null;
		}
		attackKeyJustPressed = resetAttackKey != null;
		if (resetAttackKey != null) {
			mc.gameSettings.keyBindAttack = resetAttackKey;
			resetAttackKey = null;
		}
		useItemKeyJustPressed = resetUseItemKey != null;
		if (resetUseItemKey != null) {
			mc.gameSettings.keyBindUseItem = resetUseItemKey;
			resetUseItemKey = null;
		}
		sneakKeyJustPressed = resetSneakKey != null;
		if (resetSneakKey != null) {
			mc.gameSettings.keyBindSneak = resetSneakKey;
			resetSneakKey = null;
		}
		sprintKeyJustPressed = resetSprintKey != null;
		if (resetSprintKey != null) {
			mc.gameSettings.keyBindSprint = resetSprintKey;
			resetSprintKey = null;
		}
	}

	protected boolean userTookOver() {
		final MovementInput mi = resetMovementInput == null ? mc.thePlayer.movementInput
				: resetMovementInput;
		final KeyBinding attack = resetAttackKey == null ? mc.gameSettings.keyBindAttack
				: resetAttackKey;
		final KeyBinding use = resetUseItemKey == null ? mc.gameSettings.keyBindUseItem
				: resetUseItemKey;
		final KeyBinding sneak = resetSneakKey == null ? mc.gameSettings.keyBindSneak
				: resetSneakKey;

		return mi.moveForward != 0 || mi.moveStrafe != 0 || mi.jump
				|| attack.isKeyDown() || use.isKeyDown() || sneak.isKeyDown();
	}

	/**
	 * Ungrabs the mouse.
	 */
	public void ungrab() {
		doUngrab = true;
	}

	/**
	 * Gets a list of entities in a given distance filtered with a filter.
	 * 
	 * @param dist
	 * @param selector
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Entity> getEntities(int dist, Predicate<Entity> selector) {
		return mc.theWorld.func_175674_a(
				mc.getRenderViewEntity(),
				mc.getRenderViewEntity().getEntityBoundingBox()
						.addCoord(-dist, -dist, -dist)
						.addCoord(dist, dist, dist).expand(1, 1, 1), selector);
	}

	/**
	 * Gets the closest entity limited by a given distance.
	 * 
	 * @param dist
	 * @param selector
	 * @return
	 * @see #getEntities(int, Predicate<Entity>)
	 */
	public Entity getClosestEntity(int dist, Predicate<Entity> selector) {
		final List<Entity> entities = getEntities(dist, selector);

		double mindist = Double.MAX_VALUE;
		Entity found = null;
		for (final Entity e : entities) {
			final double mydist = e.getDistanceSqToEntity(mc.thePlayer);
			if (mydist < mindist) {
				found = e;
				mindist = mydist;
			}
		}

		return found;
	}

	/**
	 * Sneak while standing on that block.
	 * 
	 * @param blockX
	 * @param blockY
	 * @param blockZ
	 * @param inDirection
	 * @return
	 */
	public boolean sneakFrom(BlockPos pos, EnumFacing inDirection) {
		final Block block = getBoundsBlock(pos);
		double destX = pos.getX() + .5;
		double destZ = pos.getZ() + .5;
		switch (inDirection) {
		case EAST:
			destX = pos.getX() + block.getBlockBoundsMaxX() + SNEAK_OFFSET;
			break;
		case WEST:
			destX = pos.getX() + block.getBlockBoundsMinX() - SNEAK_OFFSET;
			break;
		case SOUTH:
			destZ = pos.getZ() + block.getBlockBoundsMaxZ() + SNEAK_OFFSET;
			break;
		case NORTH:
			destZ = pos.getZ() + block.getBlockBoundsMinZ() - SNEAK_OFFSET;
			break;
		default:
			throw new IllegalArgumentException("Cannot handle " + inDirection);
		}
		return walkTowards(destX, destZ, false);
	}

	/**
	 * Walks towards a given point. Automatically slows down at the point
	 * 
	 * @param x
	 * @param z
	 * @param jump
	 *            If we should jump.
	 * @return <code>true</code> after arrival.
	 */
	public boolean walkTowards(double x, double z, boolean jump) {
		return walkTowards(x, z, jump, true);
	}

	public boolean walkTowards(double x, double z, boolean jump, boolean face) {
		final double dx = x - mc.thePlayer.posX;
		final double dz = z - mc.thePlayer.posZ;
		final double distTo = Math.sqrt(dx * dx + dz * dz);
		boolean arrived = distTo > MIN_DISTANCE_ERROR;
		if (arrived) {
			if (face) {
				face(x, mc.thePlayer.getEyeHeight() + mc.thePlayer.posY, z, 1, .1f);
			}
			double speed = 1;
			if (distTo < 4 * WALK_PER_STEP) {
				speed = Math.max(distTo / WALK_PER_STEP / 4, 0.1);
			}
			final double yaw = mc.thePlayer.rotationYaw / 180 * Math.PI;
			final double lookX = -Math.sin(yaw);
			final double lookZ = Math.cos(yaw);
			final double dlength = Math.sqrt(dx * dx + dz * dz);
			final double same = (lookX * dx + lookZ * dz) / dlength;
			final double strafe = (lookZ * dx - lookX * dz) / dlength;
			// System.out.println("look: " + lookX + "," + lookZ + "; d = " + dx
			// + "," + dz + "; walk: " + same + "," + strafe);
			final MovementInput movement = new MovementInput();
			movement.moveForward = (float) (speed * same);
			movement.moveStrafe = (float) (speed * strafe);
			movement.jump = jump;
			overrideMovement(movement);
			if (distTo < 0.5 || mc.thePlayer.isSprinting() && distTo < 0.8) {
				overrideSneak();
			} else if (distTo > 6) {
				overrideSprint();
			}
			return false;
		} else {
			return true;
		}
	}

	public boolean arrivedAt(double x, double z) {
		final double dx = x - mc.thePlayer.posX;
		final double dz = z - mc.thePlayer.posZ;
		final double distTo = Math.sqrt(dx * dx + dz * dz);
		return distTo <= MIN_DISTANCE_ERROR;
	}

	public boolean isJumping() {
		return !mc.thePlayer.onGround;
	}

	/**
	 * Gets the horizontal direction we look at.
	 * 
	 * @return
	 */
	public EnumFacing getLookDirection() {
		switch (MathHelper
				.floor_double(getMinecraft().thePlayer.rotationYaw / 360 * 4 + .5) & 3) {
		case 1:
			return EnumFacing.WEST;
		case 2:
			return EnumFacing.NORTH;
		case 3:
			return EnumFacing.EAST;
		default:
			return EnumFacing.SOUTH;
		}
	}

	/**
	 * See if the player has not died (=> the game over screen would be
	 * displayed).
	 * 
	 * @return <code>true</code> if it is alive.
	 */
	public boolean isAlive() {
		return mc.thePlayer != null && mc.thePlayer.getHealth() > 0.0F;
	}

	public void respawn() {
		if (!isAlive()) {
			mc.thePlayer.respawnPlayer();
			mc.displayGuiScreen(null);
		}
	}

	/**
	 * Converts an valid x/z pair to a direction if possible.
	 * 
	 * @param x
	 * @param z
	 * @return
	 */
	public static EnumFacing getDirectionForXZ(int x, int z) {
		if (x != 0 || z != 0) {
			for (final EnumFacing d : EnumFacing.values()) {
				if (d.getFrontOffsetX() == x && d.getFrontOffsetZ() == z) {
					return d;
				}
			}
		}
		throw new IllegalArgumentException("Cannot convert to direction: " + x
				+ " " + z);
	}

	public static EnumFacing getDirectionFor(BlockPos delta) {
		for (final EnumFacing d : EnumFacing.values()) {
			if (Pos.fromDir(d).equals(delta)) {
				return d;
			}
		}
		throw new IllegalArgumentException("Cannot convert to direction: "
				+ delta);
	}

	//TODO: Move this to WorldData
	public int getLightAt(BlockPos pos) {
		final Chunk chunk = mc.theWorld.getChunkFromChunkCoords(
				pos.getX() >> 4, pos.getZ() >> 4);
		final ExtendedBlockStorage storage = chunk.getBlockStorageArray()[pos
				.getY() >> 4];
		if (storage == null) {
			return 0;
		} else {
			return storage.getExtBlocklightValue(pos.getX() & 15,
					pos.getY() & 15, pos.getZ() & 15);
		}
	}

	public static String getBlockName(Block block) {
		final ResourceLocation name = ((ResourceLocation) Block.blockRegistry
				.getNameForObject(block));
		String domain = name.getResourceDomain().equals("minecraft") ? ""
				: name.getResourceDomain() + ":";
		String blockName = domain + name.getResourcePath();
		return blockName;
	}
	
	public void setActiveMapReader(MapReader activeMapReader) {
		if (this.activeMapReader != null) {
			this.activeMapReader.onStop();
		}
		this.activeMapReader = activeMapReader;
	}

	public abstract AIStrategy getResumeStrategy();

	public abstract NetworkHelper getNetworkHelper();

	public MapReader getActiveMapReader() {
		return activeMapReader;
	}
}
