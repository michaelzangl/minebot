package net.famzangl.minecraft.minebot.ai.path.world;

import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

/**
 * This is a helper class that holds lots of block sets and helper functions to
 * check blocks for safety.
 * 
 * @author Michael Zangl
 */
public class BlockSets {

	public static final BlockSet EMPTY = new BlockSet(new int[0]);

	/**
	 * Blocks we can just walk over/next to without problems.
	 */
	public static final BlockSet SIMPLE_CUBE = new BlockSet(
			Blocks.BEDROCK,
			Blocks.BOOKSHELF,
			Blocks.BRICK_BLOCK,
			Blocks.BROWN_MUSHROOM_BLOCK,
			Blocks.CAKE,
			Blocks.CLAY,
			Blocks.COAL_BLOCK,
			Blocks.COAL_ORE,
			Blocks.COBBLESTONE,
			Blocks.CRAFTING_TABLE,
			Blocks.DIAMOND_BLOCK,
			Blocks.DIAMOND_ORE,
			Blocks.DIRT,
			Blocks.DOUBLE_STONE_SLAB,
			Blocks.DOUBLE_WOODEN_SLAB,
			Blocks.EMERALD_BLOCK,
			Blocks.EMERALD_ORE,
			// FIXME: Not a cube.
			Blocks.FARMLAND, Blocks.FURNACE, Blocks.GLASS, Blocks.GLOWSTONE,
			Blocks.GRASS, Blocks.GOLD_BLOCK, Blocks.GOLD_ORE,
			Blocks.HARDENED_CLAY, Blocks.IRON_BLOCK, Blocks.IRON_ORE,
			Blocks.LAPIS_BLOCK, Blocks.LAPIS_ORE, Blocks.LEAVES,
			Blocks.LEAVES2, Blocks.LIT_PUMPKIN, Blocks.LIT_FURNACE,
			Blocks.LIT_REDSTONE_LAMP, Blocks.LIT_REDSTONE_ORE, Blocks.LOG,
			Blocks.LOG2,
			Blocks.MELON_BLOCK,
			Blocks.MOSSY_COBBLESTONE,
			Blocks.MYCELIUM,
			Blocks.NETHER_BRICK,
			Blocks.NETHERRACK,
			// Watch out, this cannot be broken easily !
			Blocks.OBSIDIAN, Blocks.PACKED_ICE, Blocks.PLANKS, Blocks.PUMPKIN,
			Blocks.QUARTZ_BLOCK, Blocks.QUARTZ_ORE, Blocks.RED_MUSHROOM_BLOCK,
			Blocks.REDSTONE_BLOCK, Blocks.REDSTONE_LAMP, Blocks.REDSTONE_ORE,
			Blocks.SANDSTONE,
			Blocks.SNOW,
			// FIXME: Not a cube.
			Blocks.SOUL_SAND, Blocks.STAINED_GLASS,
			Blocks.STAINED_HARDENED_CLAY, Blocks.STONE, Blocks.STONEBRICK,
			Blocks.WOOL);

	/**
	 * Blocks that fall down.
	 */
	public static final BlockSet FALLING = new BlockSet(Blocks.GRAVEL,
			Blocks.SAND);

	public static final BlockSet AIR = new BlockSet(Blocks.AIR);
	/**
	 * All stairs. It is no problem to walk on them.
	 */
	public static final BlockSet STAIRS = new BlockSet(Blocks.ACACIA_STAIRS,
			Blocks.BIRCH_STAIRS, Blocks.BRICK_STAIRS, Blocks.DARK_OAK_STAIRS,
			Blocks.JUNGLE_STAIRS, Blocks.NETHER_BRICK_STAIRS,
			Blocks.OAK_STAIRS, Blocks.SANDSTONE_STAIRS, Blocks.SPRUCE_STAIRS,
			Blocks.STONE_BRICK_STAIRS, Blocks.STONE_STAIRS, Blocks.STONE_SLAB,
			Blocks.WOODEN_SLAB, Blocks.QUARTZ_STAIRS);

	/**
	 * All rail blocks.
	 */
	public static final BlockSet RAILS = new BlockSet(
			Blocks.GOLDEN_RAIL,
			Blocks.DETECTOR_RAIL, 
			Blocks.RAIL, 
			Blocks.ACTIVATOR_RAIL);

	/**
	 * Flowers and stuff like that
	 */
	private static final BlockSet explicitFootWalkableBlocks = new BlockSet(
			Blocks.TALLGRASS, 
			Blocks.YELLOW_FLOWER, 
			Blocks.RED_FLOWER,
			Blocks.WHEAT, 
			Blocks.CARROTS,
			Blocks.POTATOES,
			Blocks.PUMPKIN_STEM,
			Blocks.MELON_STEM, 
			Blocks.CARPET, 
			Blocks.DOUBLE_PLANT,
			Blocks.RED_MUSHROOM, 
			Blocks.BROWN_MUSHROOM, 
			Blocks.REDSTONE_WIRE,
			Blocks.SAPLING, 
			Blocks.SNOW_LAYER, 
			Blocks.NETHER_WART,
			Blocks.STANDING_SIGN, 
			Blocks.WALL_SIGN, 
			Blocks.DEADBUSH).unionWith(RAILS);

	/**
	 * Torches.
	 */
	public static final BlockSet TORCH = new BlockSet(
			Blocks.TORCH,
			Blocks.REDSTONE_TORCH);

	/**
	 * Blocks our head can walk though. Signs could be added here, but we stay
	 * away from them for now.
	 */
	public static final BlockSet HEAD_CAN_WALK_TRHOUGH = new BlockSet(
			Blocks.AIR,
			Blocks.DOUBLE_PLANT,
			Blocks.REEDS)
			.unionWith(TORCH);

	public static final BlockSet FEET_CAN_WALK_THROUGH = explicitFootWalkableBlocks
			.unionWith(HEAD_CAN_WALK_TRHOUGH);

	public static final BlockSet FENCE = new BlockSet(
			Blocks.OAK_FENCE,
			Blocks.SPRUCE_FENCE,
			Blocks.BIRCH_FENCE,
			Blocks.JUNGLE_FENCE,
			Blocks.DARK_OAK_FENCE,
			Blocks.ACACIA_FENCE,
			Blocks.NETHER_BRICK_FENCE);

	public static final BlockSet WOODEN_DOR = new BlockSet(
			Blocks.OAK_DOOR,
			Blocks.SPRUCE_DOOR,
			Blocks.BIRCH_DOOR,
			Blocks.JUNGLE_DOOR,
			Blocks.DARK_OAK_DOOR,
			Blocks.ACACIA_DOOR);

	public static final BlockSet FENCE_GATE = new BlockSet(
			Blocks.OAK_FENCE_GATE,
			Blocks.SPRUCE_FENCE_GATE,
			Blocks.BIRCH_FENCE_GATE, 
			Blocks.JUNGLE_FENCE_GATE,
			Blocks.DARK_OAK_FENCE_GATE, 
			Blocks.ACACIA_FENCE_GATE);

	private static final BlockSet explicitSafeSideBlocks = new BlockSet(
			Blocks.ANVIL, 
			Blocks.COBBLESTONE_WALL, 
			Blocks.CACTUS, 
			Blocks.REEDS,
			Blocks.WEB, 
			Blocks.GLASS_PANE, 
			Blocks.BED, 
			Blocks.ENCHANTING_TABLE,
			Blocks.WATERLILY,
			Blocks.BREWING_STAND, 
			Blocks.VINE,
			Blocks.CHEST,
			Blocks.TRAPPED_CHEST, 
			Blocks.TRIPWIRE, 
			Blocks.TRIPWIRE_HOOK,
			Blocks.WOODEN_PRESSURE_PLATE, 
			Blocks.STONE_PRESSURE_PLATE,
			Blocks.WOODEN_BUTTON, 
			Blocks.STONE_BUTTON, 
			Blocks.MONSTER_EGG)
			.unionWith(FENCE).unionWith(FENCE_GATE);

	/**
	 * Blocks that form a solid ground.
	 */
	public static final BlockSet SAFE_GROUND = SIMPLE_CUBE.unionWith(FALLING)
			.unionWith(STAIRS);

	public static final BlockSet SAFE_SIDE = explicitSafeSideBlocks
			.unionWith(SAFE_GROUND).unionWith(FEET_CAN_WALK_THROUGH)
			.unionWith(AIR);

	public static final BlockSet SAFE_CEILING = STAIRS
			.unionWith(FEET_CAN_WALK_THROUGH).unionWith(SIMPLE_CUBE)
			.unionWith(AIR).unionWith(new BlockSet(Blocks.VINE, Blocks.CACTUS));

	/**
	 * Blocks you need to destroy but that are then safe.
	 */
	public static final BlockSet SAFE_AFTER_DESTRUCTION = new BlockSet(
			Blocks.VINE, Blocks.CACTUS);

	/**
	 * Blocks that are considered indestructable and should be avoided.
	 */
	public static final BlockSet INDESTRUCTABLE = new BlockSet(
			Blocks.BEDROCK,
			Blocks.BARRIER,
			Blocks.OBSIDIAN);

	/**
	 * All leaves. FIXME: Only consider leaves that do not decay as safe ground.
	 */
	public static final BlockSet LEAVES = new BlockSet(
			Blocks.LEAVES,
			Blocks.LEAVES2);
	public static final BlockSet LOGS = new BlockSet(
			Blocks.LOG, 
			Blocks.LOG2);

	public static final BlockSet LOWER_SLABS;
	static {
		BlockSet lower = BlockSets.EMPTY;
		for (int i = 0; i < 8; i++) {
			lower = lower.unionWith(new BlockMetaSet(Blocks.STONE_SLAB, i));
			lower = lower.unionWith(new BlockMetaSet(Blocks.WOODEN_SLAB, i));
		}
		lower = lower.unionWith(new BlockMetaSet(Blocks.STONE_SLAB2, 0));
		LOWER_SLABS = lower;
	}

	public static final BlockSet UPPER_SLABS;

	public static final BlockSet WATER = new BlockSet(Blocks.WATER,
			Blocks.FLOWING_WATER);

	public static final BlockSet TREE_BLOCKS = LOGS.unionWith(LEAVES);
	public static final BlockSet FURNACE = new BlockSet(Blocks.FURNACE, Blocks.LIT_FURNACE);

	static {
		BlockSet upper = BlockSets.EMPTY;
		for (int i = 0; i < 8; i++) {
			upper = upper.unionWith(new BlockMetaSet(Blocks.STONE_SLAB, i + 8));
			upper = upper
					.unionWith(new BlockMetaSet(Blocks.WOODEN_SLAB, i + 8));
		}
		upper = upper.unionWith(new BlockMetaSet(Blocks.STONE_SLAB2, 8));
		UPPER_SLABS = upper;
	}

	public static boolean safeSideAround(WorldData world, int x, int y, int z) {
		return SAFE_SIDE.isAt(world, x + 1, y, z)
				&& SAFE_SIDE.isAt(world, x - 1, y, z)
				&& SAFE_SIDE.isAt(world, x, y, z + 1)
				&& SAFE_SIDE.isAt(world, x, y, z - 1);
	}

	public static boolean safeSideAround(WorldData world, BlockPos pos) {
		return safeSideAround(world, pos.getX(), pos.getY(), pos.getZ());
	}

	public static boolean safeSideAndCeilingAround(WorldData world, int x,
			int y, int z) {
		return safeSideAround(world, x, y, z)
				&& SAFE_CEILING.isAt(world, x, y + 1, z);
	}

	public static boolean safeSideAndCeilingAround(WorldData world, BlockPos pos) {
		return safeSideAndCeilingAround(world, pos.getX(), pos.getY(),
				pos.getZ());
	}

	private BlockSets() {
	}
}
