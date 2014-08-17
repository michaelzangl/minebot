package net.famzangl.minecraft.minebot.ai;

import java.util.List;
import java.util.Random;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.command.AIChatController;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.famzangl.minecraft.minebot.ai.task.BlockSide;
import net.famzangl.minecraft.minebot.build.BuildManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockWall;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovementInput;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Everything needed to control the AI.
 * 
 * @author michael
 * 
 */
public abstract class AIHelper {
	private static final double SNEAK_OFFSET = .2;
	private static final double WALK_PER_STEP = 4.3 / 20;
	private static final double MIN_DISTANCE_ERROR = 0.05;
	private static Minecraft mc = Minecraft.getMinecraft();
	private final Random rand = new Random();

	/**
	 * Blocks we can just walk over/next to without problems.
	 */
	public static final Block[] normalBlocks = new Block[] { Blocks.bedrock,
			Blocks.bookshelf, Blocks.brick_block, Blocks.brown_mushroom_block,
			Blocks.cake, Blocks.coal_block, Blocks.coal_ore,
			Blocks.cobblestone, Blocks.crafting_table, Blocks.diamond_block,
			Blocks.diamond_ore, Blocks.dirt, Blocks.double_stone_slab,
			Blocks.double_wooden_slab, Blocks.emerald_block,
			Blocks.emerald_ore, Blocks.farmland, Blocks.glass,
			Blocks.glowstone, Blocks.grass, Blocks.gold_block, Blocks.gold_ore,
			Blocks.hardened_clay, Blocks.iron_block, Blocks.iron_ore,
			Blocks.lapis_block, Blocks.lapis_ore, Blocks.leaves,
			Blocks.leaves2, Blocks.log, Blocks.log2,
			Blocks.melon_block,
			Blocks.mossy_cobblestone,
			Blocks.mycelium,
			Blocks.nether_brick,
			Blocks.nether_brick_fence,
			Blocks.netherrack,
			// Watch out, this cannot be broken easilly !
			Blocks.obsidian, Blocks.packed_ice, Blocks.planks, Blocks.pumpkin,
			Blocks.quartz_block, Blocks.quartz_ore, Blocks.red_mushroom_block,
			Blocks.redstone_block, Blocks.redstone_lamp, Blocks.redstone_ore,
			Blocks.sandstone, Blocks.snow, Blocks.soul_sand,
			Blocks.stained_glass, Blocks.stained_hardened_clay, Blocks.stone,
			Blocks.stonebrick, Blocks.web, Blocks.wool };

	public static final Block[] fallingBlocks = new Block[] { Blocks.gravel,
			Blocks.sand, };

	/**
	 * All stairs. It is no problem to walk on them.
	 */
	public static final Block[] stairBlocks = new Block[] {
			Blocks.acacia_stairs, Blocks.birch_stairs, Blocks.brick_stairs,
			Blocks.dark_oak_stairs, Blocks.jungle_stairs,
			Blocks.nether_brick_stairs, Blocks.oak_stairs,
			Blocks.sandstone_stairs, Blocks.spruce_stairs,
			Blocks.stone_brick_stairs, Blocks.stone_stairs, Blocks.stone_slab,
			Blocks.wooden_slab, Blocks.quartz_stairs };

	/**
	 * Flowers and stuff like that
	 */
	public static final Block[] walkableBlocks = new Block[] {
			Blocks.tallgrass, Blocks.yellow_flower, Blocks.red_flower,
			Blocks.wheat, Blocks.carrots, Blocks.potatoes, Blocks.pumpkin_stem,
			Blocks.melon_stem, Blocks.torch, Blocks.carpet, Blocks.golden_rail,
			Blocks.detector_rail, Blocks.rail, Blocks.activator_rail,
			Blocks.double_plant, Blocks.red_mushroom, Blocks.brown_mushroom,
			Blocks.redstone_wire };
	public static final Block[] safeSideBlocks = new Block[] { Blocks.fence,
			Blocks.fence_gate, Blocks.cobblestone_wall, Blocks.cactus,
			Blocks.reeds, };

	public final BuildManager buildManager = new BuildManager();
	private boolean objectMouseOverInvalidated;

	protected Pos pos1 = null;
	protected Pos pos2 = null;

	public Minecraft getMinecraft() {
		return mc;
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
		return mc.theWorld.getBlock(x, y, z);
	}

	/**
	 * Gets the block at that position.
	 * 
	 * @param pos
	 * @return
	 */
	public Block getBlock(Pos pos) {
		return getBlock(pos.x, pos.y, pos.z);
	}

	/**
	 * Adds a task that should be executed.
	 * 
	 * @param task
	 *            The new task
	 */
	public abstract void addTask(AITask task);

	/**
	 * This should be called whenever the current task could not achieve it's
	 * goal. All following tasks are unscheduled.
	 */
	public abstract void desync();

	/**
	 * Gets the pos1 marker.
	 * 
	 * @return The marker or <code>null</code> if it is unset.
	 */
	public Pos getPos1() {
		return pos1;
	}

	/**
	 * Gets the pos2 marker.
	 * 
	 * @return The marker or <code>null</code> if it is unset.
	 */
	public Pos getPos2() {
		return pos2;
	}

	/**
	 * Sets the pos1 or pos2 marker.
	 * 
	 * @param pos
	 *            The new position.
	 * @param isPos2
	 */
	public void setPosition(Pos pos, boolean isPos2) {
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
	 * Faces an exact position in space.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public void face(double x, double y, double z) {
		final double d0 = x - mc.thePlayer.posX;
		final double d1 = z - mc.thePlayer.posZ;
		final double d2 = y - mc.thePlayer.posY;
		final double d3 = d0 * d0 + d2 * d2 + d1 * d1;

		if (d3 >= 2.500000277905201E-7D) {
			final float rotationYaw = mc.thePlayer.rotationYaw;
			final float rotationPitch = mc.thePlayer.rotationPitch;

			final float yaw = (float) (Math.atan2(d1, d0) * 180.0D / Math.PI) - 90.0F;
			final float pitch = (float) -(Math.atan2(d2,
					Math.sqrt(d0 * d0 + d1 * d1)) * 180.0D / Math.PI);
			mc.thePlayer.setAngles((yaw - rotationYaw) / 0.15f,
					-(pitch - rotationPitch) / 0.15f);
			invalidateObjectMouseOver();
		}
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
	 * Finds any Block of that type directly around the player.
	 * 
	 * @param blockType
	 *            The block to search.
	 * @return the position or <code>null</code> if it was not found.
	 */
	public Pos findBlock(Block blockType) {
		Pos current = getPlayerPosition();
		Pos pos = null;
		for (int x = current.x - 2; x <= current.x + 2; x++) {
			for (int z = current.z - 2; z <= current.z + 2; z++) {
				for (int y = current.y - 1; y <= current.y + 2; y++) {
					final Block block = mc.theWorld.getBlock(x, y, z);
					if (Block.isEqualTo(block, blockType)) {
						pos = new Pos(x, y, z);
					}
				}
			}
		}
		return pos;
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
				&& o.blockX == x && o.blockY == y && o.blockZ == z;
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
	public boolean isFacingBlock(int x, int y, int z, ForgeDirection blockSide,
			BlockSide half) {
		if (!isFacingBlock(x, y, z, sideToDir(blockSide))) {
			return false;
		} else {
			final double fy = getObjectMouseOver().hitVec.yCoord - y;
			return half != BlockSide.LOWER_HALF && fy > .5
					|| half != BlockSide.UPPER_HALF && fy <= .5;
		}
	}

	/**
	 * Checks if the player is facing a specific side of the block.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param blockSide
	 *            The side of the block.
	 * @return <code>true</code> if the player faces the block.
	 */
	public boolean isFacingBlock(int x, int y, int z, ForgeDirection blockSide) {
		return isFacingBlock(x, y, z, sideToDir(blockSide));
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
	public boolean isFacingBlock(int x, int y, int z, int side) {
		final MovingObjectPosition o = getObjectMouseOver();
		return o != null && o.typeOfHit == MovingObjectType.BLOCK
				&& o.blockX == x && o.blockY == y && o.blockZ == z
				&& o.sideHit == side;
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
				&& Math.abs(mc.thePlayer.boundingBox.minY - y) < 0.52;
	}

	/**
	 * Get the current position the player is at.
	 * 
	 * @return The position.
	 */
	public Pos getPlayerPosition() {
		final int x = (int) Math.floor(getMinecraft().thePlayer.posX);
		final int y = (int) Math.floor(getMinecraft().thePlayer.boundingBox.minY + 0.05);
		final int z = (int) Math.floor(getMinecraft().thePlayer.posZ);
		return new Pos(x, y, z);
	}

	/**
	 * Selects a good tool for mining the given Block.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public void selectToolFor(final int x, final int y, final int z) {
		selectCurrentItem(new ItemFilter() {
			@Override
			public boolean matches(ItemStack itemStack) {
				return itemStack != null
						&& itemStack.getItem() != null
						&& itemStack.getItem().func_150893_a(itemStack,
								getBlock(x, y, z)) > 1;
			}
		});
	}

	/**
	 * Check if this block is air
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public boolean isAirBlock(int x, int y, int z) {
		return Block.isEqualTo(mc.theWorld.getBlock(x, y, z), Blocks.air);
	}

	/**
	 * Check if this block is safe to build upwards on a path.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public boolean isSafeUpwardsGround(int x, int y, int z) {
		return isSafeGroundBlock(x, y, z) || isAirBlock(x, y, z);
	}

	/**
	 * Check if this block is safe to stand on it on a path.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public boolean isSafeGroundBlock(int x, int y, int z) {
		final Block block = mc.theWorld.getBlock(x, y, z);
		return isSafeStandableBlock(block);
	}

	/**
	 * Check if we would want this block over our head.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public boolean isSafeHeadBlock(int x, int y, int z) {
		final Block block = mc.theWorld.getBlock(x, y, z);
		return blockIsOneOf(block, stairBlocks)
				|| blockIsOneOf(block, walkableBlocks)
				|| blockIsOneOf(block, normalBlocks) || isAirBlock(x, y, z);
	}

	public boolean isFallingBlock(int x, int y, int z) {
		final Block block = mc.theWorld.getBlock(x, y, z);
		return blockIsOneOf(block, fallingBlocks);
	}

	/**
	 * Check if all sides of this block are considered safe.
	 * 
	 * @see AIHelper#isSafeSideBlock(int, int, int)
	 * @param cx
	 * @param cy
	 * @param cz
	 * @return
	 */
	public boolean hasSafeSides(int cx, int cy, int cz) {
		return isSafeSideBlock(cx - 1, cy, cz)
				&& isSafeSideBlock(cx + 1, cy, cz)
				&& isSafeSideBlock(cx, cy, cz + 1)
				&& isSafeSideBlock(cx, cy, cz - 1);
	}

	public boolean isSafeSideBlock(int x, int y, int z) {
		final Block block = mc.theWorld.getBlock(x, y, z);
		return isSafeStandableBlock(block) || canWalkOn(block)
				|| canWalkThrough(block) || blockIsOneOf(block, safeSideBlocks)
				|| isAirBlock(x, y, z);
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
	public boolean canWalkThrough(Block block) {
		return blockIsOneOf(block, Blocks.air, Blocks.torch,
				Blocks.double_plant, Blocks.redstone_torch);
	}

	/**
	 * Check if the block is a block that we could walk on as if it was air.
	 * Carpets, torches, ...
	 * 
	 * @param block
	 * @return
	 */
	public boolean canWalkOn(Block block) {
		return blockIsOneOf(block, walkableBlocks) || canWalkThrough(block);
	}

	/**
	 * Check if it is a railway line.
	 * 
	 * @param block
	 * @return
	 */
	@Deprecated
	public boolean isRailBlock(Block block) {
		return blockIsOneOf(block, Blocks.golden_rail, Blocks.detector_rail,
				Blocks.rail, Blocks.activator_rail);
	}

	/**
	 * Check if we can stand on the block.
	 * 
	 * @param block
	 * @return
	 */
	public boolean isSafeStandableBlock(Block block) {
		return blockIsOneOf(block, normalBlocks)
				|| blockIsOneOf(block, fallingBlocks)
				|| blockIsOneOf(block, stairBlocks);
	}

	/**
	 * Faces a block and destroys it if possible.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public void faceAndDestroy(final int x, final int y, final int z) {
		if (isFacingBlock(x, y, z)) {
			selectToolFor(x, y, z);
			overrideAttack();
		} else {
			faceBlock(x, y, z);
		}
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
	 * Faces the side of a block.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param sideToFace
	 */
	public void faceSideOf(int x, int y, int z, ForgeDirection sideToFace) {
		double faceX = x + randBetween(0.1, 0.9);
		double faceY = y + randBetween(0.1, 0.9);
		double faceZ = z + randBetween(0.1, 0.9);

		final Block block = getBoundsBlock(x, y, z);
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
	public void faceSideOf(int x, int y, int z, ForgeDirection sideToFace,
			double minY, double maxY, double centerX, double centerZ,
			ForgeDirection xzdir) {
		System.out.println("x = " + x + " y=" + y + " z=" + z + " dir="
				+ sideToFace);
		final Block block = getBoundsBlock(x, y, z);

		minY = Math.max(minY, block.getBlockBoundsMinY());
		maxY = Math.min(maxY, block.getBlockBoundsMaxY());
		double faceY = randBetweenNice(minY, maxY);
		double faceX, faceZ;

		if (xzdir == ForgeDirection.EAST) {
			faceX = randBetween(Math.max(block.getBlockBoundsMinX(), centerX),
					block.getBlockBoundsMaxX());
			faceZ = centerZ;
		} else if (xzdir == ForgeDirection.WEST) {
			faceX = randBetween(block.getBlockBoundsMinX(),
					Math.min(block.getBlockBoundsMaxX(), centerX));
			faceZ = centerZ;
		} else if (xzdir == ForgeDirection.SOUTH) {
			faceZ = randBetween(Math.max(block.getBlockBoundsMinZ(), centerZ),
					block.getBlockBoundsMaxZ());
			faceX = centerX;
		} else if (xzdir == ForgeDirection.NORTH) {
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
		face(faceX + x, faceY + y, faceZ + z);
	}

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

	private MovementInput resetMovementInput;
	private KeyBinding resetAttackKey;
	private KeyBinding resetUseItemKey;
	private boolean useItemKeyJustPressed;
	private boolean attackKeyJustPressed;
	protected boolean doUngrab;
	private KeyBinding resetSneakKey;
	private boolean sneakKeyJustPressed;

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
			useItemKeyJustPressed = resetUseItemKey.getIsKeyPressed();
		}
		mc.gameSettings.keyBindUseItem = new InteractAlways(
				mc.gameSettings.keyBindAttack.getKeyDescription(), 501,
				mc.gameSettings.keyBindAttack.getKeyCategory(),
				useItemKeyJustPressed);
	}

	/**
	 * Presses the attack key in the next game tick.
	 */
	public void overrideAttack() {
		if (resetAttackKey == null) {
			resetAttackKey = mc.gameSettings.keyBindAttack;
			attackKeyJustPressed = resetAttackKey.getIsKeyPressed();
		}
		mc.gameSettings.keyBindAttack = new InteractAlways(
				mc.gameSettings.keyBindAttack.getKeyDescription(), 502,
				mc.gameSettings.keyBindAttack.getKeyCategory(),
				attackKeyJustPressed);
	}

	/**
	 * Presses the sneak key in the next game step.
	 */
	public void overrideSneak() {
		if (resetSneakKey == null) {
			resetSneakKey = mc.gameSettings.keyBindSneak;
			sneakKeyJustPressed = resetSneakKey.getIsKeyPressed();
		}
		mc.gameSettings.keyBindSneak = new InteractAlways(
				mc.gameSettings.keyBindSneak.getKeyDescription(), 503,
				mc.gameSettings.keyBindSneak.getKeyCategory(),
				sneakKeyJustPressed);
	}

	/**
	 * Restore all inputs to the default minecraft inputs.
	 */
	protected void resetAllInputs() {
		if (resetMovementInput != null) {
			mc.thePlayer.movementInput = resetMovementInput;
		}
		attackKeyJustPressed = resetAttackKey != null;
		if (resetAttackKey != null) {
			mc.gameSettings.keyBindAttack = resetAttackKey;
		}
		useItemKeyJustPressed = resetUseItemKey != null;
		if (resetUseItemKey != null) {
			mc.gameSettings.keyBindUseItem = resetUseItemKey;
		}
		sneakKeyJustPressed = resetSneakKey != null;
		if (resetSneakKey != null) {
			mc.gameSettings.keyBindSneak = resetSneakKey;
		}
	}

	protected boolean userTookOver() {
		MovementInput mi = resetMovementInput == null ? mc.thePlayer.movementInput
				: resetMovementInput;
		KeyBinding attack = resetAttackKey == null ? mc.gameSettings.keyBindAttack
				: resetAttackKey;
		KeyBinding use = resetUseItemKey == null ? mc.gameSettings.keyBindUseItem
				: resetUseItemKey;
		KeyBinding sneak = resetSneakKey == null ? mc.gameSettings.keyBindSneak
				: resetSneakKey;

		return mi.moveForward != 0 || mi.moveStrafe != 0 || mi.jump
				|| attack.getIsKeyPressed() || use.getIsKeyPressed()
				|| sneak.getIsKeyPressed();
	}

	/**
	 * Checks if a block is in a list of blocks.
	 * 
	 * @param needle
	 *            The block to search for
	 * @param haystack
	 *            The blocks allowed.
	 * @return <code>true</code> if it is the list.
	 */
	public static boolean blockIsOneOf(Block needle, Block... haystack) {
		for (final Block h : haystack) {
			if (Block.isEqualTo(needle, h)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * A fast method that gets a block id for a given position. Use it if you
	 * need to scan many blocks, since the default minecraft block lookup is
	 * slow.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public int getBlockId(int x, int y, int z) {
		final Chunk chunk = mc.theWorld.getChunkFromChunkCoords(x >> 4, z >> 4);
		chunk.getBlock(x & 15, y, z & 15);

		int blockId = 0;

		final ExtendedBlockStorage[] sa = chunk.getBlockStorageArray();
		if (y >> 4 < sa.length) {
			final ExtendedBlockStorage extendedblockstorage = sa[y >> 4];

			if (extendedblockstorage != null) {
				final int lx = x & 15;
				final int ly = y & 15;
				final int lz = z & 15;

				blockId = extendedblockstorage.getBlockLSBArray()[ly << 8
						| lz << 4 | lx] & 255;

				final NibbleArray blockMSBArray = extendedblockstorage
						.getBlockMSBArray();
				if (blockMSBArray != null) {
					blockId |= blockMSBArray.get(lx, ly, lz) << 8;
				}
			}
		}

		return blockId;
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
			final int blockMetadata = getMinecraft().theWorld.getBlockMetadata(
					x, y, z);
			maxY = (blockMetadata & 0x8) == 0 ? 0.5 : 1;
		} else {
			maxY = block.getBlockBoundsMaxY();
		}

		return y - 1 + maxY;
	}

	/**
	 * Gets a list of entities in a given distance filtered with a filter.
	 * 
	 * @param dist
	 * @param selector
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Entity> getEntities(int dist, IEntitySelector selector) {
		return mc.theWorld.getEntitiesWithinAABBExcludingEntity(
				mc.renderViewEntity,
				mc.renderViewEntity.boundingBox.addCoord(-dist, -dist, -dist)
						.addCoord(dist, dist, dist).expand(1, 1, 1), selector);
	}

	/**
	 * Gets the closest entity limited by a given distance.
	 * 
	 * @param dist
	 * @param selector
	 * @return
	 * @see #getEntities(int, IEntitySelector)
	 */
	public Entity getClosestEntity(int dist, IEntitySelector selector) {
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
	 * Ungrabs the mouse.
	 */
	public void ungrab() {
		doUngrab = true;
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
	public boolean sneakFrom(int blockX, int blockY, int blockZ,
			ForgeDirection inDirection) {
		final Block block = getBoundsBlock(blockX, blockY, blockZ);
		double destX = blockX + .5;
		double destZ = blockZ + .5;
		switch (inDirection) {
		case EAST:
			destX = blockX + block.getBlockBoundsMaxX() + SNEAK_OFFSET;
			break;
		case WEST:
			destX = blockX + block.getBlockBoundsMinX() - SNEAK_OFFSET;
			break;
		case SOUTH:
			destZ = blockZ + block.getBlockBoundsMaxZ() + SNEAK_OFFSET;
			break;
		case NORTH:
			destZ = blockZ + block.getBlockBoundsMinZ() - SNEAK_OFFSET;
			break;
		default:
			throw new IllegalArgumentException("Cannot handle " + inDirection);
		}
		return walkTowards(destX, destZ, false);
	}

	private Block getBoundsBlock(int blockX, int blockY, int blockZ) {
		Block block = getBlock(blockX, blockY, blockZ);
		if (block instanceof BlockStairs) {
			// Stairs have crazy bounds
			block = Blocks.dirt;
		}
		block.setBlockBoundsBasedOnState(mc.theWorld, blockX, blockY, blockZ);
		return block;
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
		if (distTo > MIN_DISTANCE_ERROR) {
			if (face) {
				face(x, mc.thePlayer.posY, z);
			}
			double speed = 1;
			if (distTo < 4 * WALK_PER_STEP) {
				speed = Math.max(distTo / WALK_PER_STEP / 4, 0.1);
			}
			double yaw = mc.thePlayer.rotationYaw / 180 * Math.PI;
			double lookX = -Math.sin(yaw);
			double lookZ = Math.cos(yaw);
			double dlength = Math.sqrt(dx * dx + dz * dz);
			double same = (lookX * dx + lookZ * dz) / dlength;
			double strafe = (lookZ * dx - lookX * dz) / dlength;
			System.out.println("look: " + lookX + "," + lookZ + "; d = " + dx
					+ "," + dz + "; walk: " + same + "," + strafe);
			final MovementInput movement = new MovementInput();
			movement.moveForward = (float) (speed * same);
			movement.moveStrafe = (float) (speed * strafe);
			movement.jump = jump;
			overrideMovement(movement);
			if (distTo < 0.5) {
				overrideSneak();
			}
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Bottom = 0, Top = 1, East = 2, West = 3, North = 4, South = 5.
	 */
	public static int sideToDir(ForgeDirection blockSide) {
		switch (blockSide) {
		case DOWN:
			return 0;
		case UP:
			return 1;
		case EAST:
			return 5;
		case WEST:
			return 4;
		case NORTH:
			return 2;
		case SOUTH:
			return 3;
		default:
			throw new IllegalArgumentException("Cannot handle: " + blockSide);
		}
	}

	public boolean isJumping() {
		return !mc.thePlayer.onGround;
	}

	/**
	 * Gets the horizontal direction we look at.
	 * 
	 * @return
	 */
	public ForgeDirection getLookDirection() {
		switch (MathHelper
				.floor_double(getMinecraft().thePlayer.rotationYaw / 360 * 4 + .5) & 3) {
		case 1:
			return ForgeDirection.WEST;
		case 2:
			return ForgeDirection.NORTH;
		case 3:
			return ForgeDirection.EAST;
		default:
			return ForgeDirection.SOUTH;
		}
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
	 * Converts an valid x/z pair to a direction if possible.
	 * 
	 * @param x
	 * @param z
	 * @return
	 */
	public static ForgeDirection getDirectionForXZ(int x, int z) {
		if (x != 0 || z != 0) {
			for (final ForgeDirection d : ForgeDirection.VALID_DIRECTIONS) {
				if (d.offsetX == x && d.offsetZ == z) {
					return d;
				}
			}
		}
		throw new IllegalArgumentException("Cannot convert to direction: " + x
				+ " " + z);
	}

	public static ForgeDirection getDirectionFor(Pos pos) {
		for (final ForgeDirection d : ForgeDirection.VALID_DIRECTIONS) {
			if (d.offsetX == pos.x && d.offsetY == pos.y && d.offsetZ == pos.z) {
				return d;
			}
		}
		throw new IllegalArgumentException("Cannot convert to direction: "
				+ pos);
	}

	public int getLightAt(Pos pos) {
        Chunk chunk = mc.theWorld.getChunkFromChunkCoords(pos.x >> 4, pos.z >> 4);
        ExtendedBlockStorage storage = chunk.getBlockStorageArray()[pos.y >> 4];
        if (storage == null) {
        	return 0;
        } else {
        	return storage.getExtBlocklightValue(pos.x & 15, pos.y & 15, pos.z & 15);
        }
	}

	/**
	 * This can be called by the current task to do a look ahead and already let
	 * the next task to it's face and destroy. Use with care.
	 * 
	 * @return
	 */
	public boolean faceAndDestroyForNextTask() {
		return false;
	}
}
