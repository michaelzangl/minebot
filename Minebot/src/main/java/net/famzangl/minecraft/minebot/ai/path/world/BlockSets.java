package net.famzangl.minecraft.minebot.ai.path.world;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;

/**
 * This is a helper class that holds lots of block sets and helper functions to
 * check blocks for safety.
 * 
 * @author Michael Zangl
 */
public class BlockSets {

	public static final BlockSet EMPTY = new BlockSet(new int[0]);

	public static final BlockSet AIR = new BlockSet(Blocks.AIR);
	/**
	 * All rail blocks.
	 */
	public static final BlockSet RAILS = new BlockSet(
			Blocks.POWERED_RAIL,
			Blocks.DETECTOR_RAIL, 
			Blocks.RAIL, 
			Blocks.ACTIVATOR_RAIL);
    public static final BlockSet WOOL = new BlockSet(
    		Blocks.BLACK_WOOL,
			Blocks.BLUE_WOOL,
			Blocks.BROWN_WOOL,
			Blocks.CYAN_WOOL,
			Blocks.GRAY_WOOL,
			Blocks.GREEN_WOOL,
			Blocks.LIGHT_BLUE_WOOL,
			Blocks.LIGHT_GRAY_WOOL,
			Blocks.LIME_WOOL,
			Blocks.MAGENTA_WOOL,
			Blocks.ORANGE_WOOL,
			Blocks.PINK_WOOL,
			Blocks.PURPLE_WOOL,
			Blocks.RED_WOOL,
			Blocks.WHITE_WOOL,
			Blocks.YELLOW_WOOL
	);

    public static final BlockSet STAINED_GLASS = new BlockSet(
    		Blocks.BLACK_STAINED_GLASS,
			Blocks.BLUE_STAINED_GLASS,
			Blocks.BROWN_STAINED_GLASS,
			Blocks.CYAN_STAINED_GLASS,
			Blocks.GRAY_STAINED_GLASS,
			Blocks.GREEN_STAINED_GLASS,
			Blocks.LIGHT_BLUE_STAINED_GLASS,
			Blocks.LIGHT_GRAY_STAINED_GLASS,
			Blocks.LIME_STAINED_GLASS,
			Blocks.MAGENTA_STAINED_GLASS,
			Blocks.ORANGE_STAINED_GLASS,
			Blocks.PINK_STAINED_GLASS,
			Blocks.PURPLE_STAINED_GLASS,
			Blocks.RED_STAINED_GLASS,
			Blocks.WHITE_STAINED_GLASS,
			Blocks.YELLOW_STAINED_GLASS
	);
    public static final BlockSet GLAZED_TERRACOTTA = new BlockSet(
    		Blocks.BLACK_GLAZED_TERRACOTTA,
			Blocks.BLUE_GLAZED_TERRACOTTA,
			Blocks.BROWN_GLAZED_TERRACOTTA,
			Blocks.CYAN_GLAZED_TERRACOTTA,
			Blocks.GRAY_GLAZED_TERRACOTTA,
			Blocks.GREEN_GLAZED_TERRACOTTA,
			Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA,
			Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA,
			Blocks.LIME_GLAZED_TERRACOTTA,
			Blocks.MAGENTA_GLAZED_TERRACOTTA,
			Blocks.ORANGE_GLAZED_TERRACOTTA,
			Blocks.PINK_GLAZED_TERRACOTTA,
			Blocks.PURPLE_GLAZED_TERRACOTTA,
			Blocks.RED_GLAZED_TERRACOTTA,
			Blocks.WHITE_GLAZED_TERRACOTTA,
			Blocks.YELLOW_GLAZED_TERRACOTTA
	);
    public static final BlockSet CARPET = new BlockSet(
    		Blocks.BLACK_CARPET,
			Blocks.BLUE_CARPET,
			Blocks.BROWN_CARPET,
			Blocks.CYAN_CARPET,
			Blocks.GRAY_CARPET,
			Blocks.GREEN_CARPET,
			Blocks.LIGHT_BLUE_CARPET,
			Blocks.LIGHT_GRAY_CARPET,
			Blocks.LIME_CARPET,
			Blocks.MAGENTA_CARPET,
			Blocks.ORANGE_CARPET,
			Blocks.PINK_CARPET,
			Blocks.PURPLE_CARPET,
			Blocks.RED_CARPET,
			Blocks.WHITE_CARPET,
			Blocks.YELLOW_CARPET
	);

	public static final BlockSet BED = new BlockSet(
			Blocks.BLACK_BED,
			Blocks.BLUE_BED,
			Blocks.BROWN_BED,
			Blocks.CYAN_BED,
			Blocks.GRAY_BED,
			Blocks.GREEN_BED,
			Blocks.LIGHT_BLUE_BED,
			Blocks.LIGHT_GRAY_BED,
			Blocks.LIME_BED,
			Blocks.MAGENTA_BED,
			Blocks.ORANGE_BED,
			Blocks.PINK_BED,
			Blocks.PURPLE_BED,
			Blocks.RED_BED,
			Blocks.WHITE_BED,
			Blocks.YELLOW_BED
	);

	public static final BlockSet CONCRETE = new BlockSet(
			Blocks.BLACK_CONCRETE,
			Blocks.BLUE_CONCRETE,
			Blocks.BROWN_CONCRETE,
			Blocks.CYAN_CONCRETE,
			Blocks.GRAY_CONCRETE,
			Blocks.GREEN_CONCRETE,
			Blocks.LIGHT_BLUE_CONCRETE,
			Blocks.LIGHT_GRAY_CONCRETE,
			Blocks.LIME_CONCRETE,
			Blocks.MAGENTA_CONCRETE,
			Blocks.ORANGE_CONCRETE,
			Blocks.PINK_CONCRETE,
			Blocks.PURPLE_CONCRETE,
			Blocks.RED_CONCRETE,
			Blocks.WHITE_CONCRETE,
			Blocks.YELLOW_CONCRETE
	);

	public static final BlockSet CONCRETE_POWDER = new BlockSet(
			Blocks.BLACK_CONCRETE_POWDER,
			Blocks.BLUE_CONCRETE_POWDER,
			Blocks.BROWN_CONCRETE_POWDER,
			Blocks.CYAN_CONCRETE_POWDER,
			Blocks.GRAY_CONCRETE_POWDER,
			Blocks.GREEN_CONCRETE_POWDER,
			Blocks.LIGHT_BLUE_CONCRETE_POWDER,
			Blocks.LIGHT_GRAY_CONCRETE_POWDER,
			Blocks.LIME_CONCRETE_POWDER,
			Blocks.MAGENTA_CONCRETE_POWDER,
			Blocks.ORANGE_CONCRETE_POWDER,
			Blocks.PINK_CONCRETE_POWDER,
			Blocks.PURPLE_CONCRETE_POWDER,
			Blocks.RED_CONCRETE_POWDER,
			Blocks.WHITE_CONCRETE_POWDER,
			Blocks.YELLOW_CONCRETE_POWDER
	);

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

	public static final BlockSet WOODEN_SLAB = new BlockSet(
			Blocks.OAK_SLAB,
			Blocks.SPRUCE_SLAB,
			Blocks.BIRCH_SLAB,
			Blocks.JUNGLE_SLAB,
			Blocks.DARK_OAK_SLAB,
			Blocks.ACACIA_SLAB);

	public static final BlockSet WOODEN_PRESSURE_PLATE = new BlockSet(
			Blocks.OAK_PRESSURE_PLATE,
			Blocks.SPRUCE_PRESSURE_PLATE,
			Blocks.BIRCH_PRESSURE_PLATE,
			Blocks.JUNGLE_PRESSURE_PLATE,
			Blocks.DARK_OAK_PRESSURE_PLATE,
			Blocks.ACACIA_PRESSURE_PLATE);

	public static final BlockSet WOODEN_BUTTON = new BlockSet(
			Blocks.OAK_BUTTON,
			Blocks.SPRUCE_BUTTON,
			Blocks.BIRCH_BUTTON,
			Blocks.JUNGLE_BUTTON,
			Blocks.DARK_OAK_BUTTON,
			Blocks.ACACIA_BUTTON);

	public static final BlockSet SAPLING = new BlockSet(
			Blocks.OAK_SAPLING,
			Blocks.SPRUCE_SAPLING,
			Blocks.BIRCH_SAPLING,
			Blocks.JUNGLE_SAPLING,
			Blocks.DARK_OAK_SAPLING,
			Blocks.ACACIA_SAPLING);

	public static final BlockSet SIGN = new BlockSet(
			Blocks.OAK_SIGN,
			Blocks.SPRUCE_SIGN,
			Blocks.BIRCH_SIGN,
			Blocks.JUNGLE_SIGN,
			Blocks.DARK_OAK_SIGN,
			Blocks.ACACIA_SIGN);

	public static final BlockSet FENCE_GATE = new BlockSet(
			Blocks.OAK_FENCE_GATE,
			Blocks.SPRUCE_FENCE_GATE,
			Blocks.BIRCH_FENCE_GATE,
			Blocks.JUNGLE_FENCE_GATE,
			Blocks.DARK_OAK_FENCE_GATE,
			Blocks.ACACIA_FENCE_GATE);

	/**
	 * All leaves. FIXME: Only consider leaves that do not decay as safe ground.
	 */
	public static final BlockSet LEAVES = new BlockSet(
			Blocks.OAK_LEAVES,
			Blocks.SPRUCE_LEAVES,
			Blocks.BIRCH_LEAVES,
			Blocks.JUNGLE_LEAVES,
			Blocks.DARK_OAK_LEAVES,
			Blocks.ACACIA_LEAVES);

	public static final BlockSet PLANKS = new BlockSet(
			Blocks.OAK_PLANKS,
			Blocks.SPRUCE_PLANKS,
			Blocks.BIRCH_PLANKS,
			Blocks.JUNGLE_PLANKS,
			Blocks.DARK_OAK_PLANKS,
			Blocks.ACACIA_PLANKS);

	public static final BlockSet LOGS = new BlockSet(
			Blocks.OAK_LOG,
			Blocks.SPRUCE_LOG,
			Blocks.BIRCH_LOG,
			Blocks.JUNGLE_LOG,
			Blocks.DARK_OAK_LOG,
			Blocks.ACACIA_LOG);

	public static final BlockSet WALL_SIGN = new BlockSet(Blocks.ACACIA_WALL_SIGN, Blocks.BIRCH_WALL_SIGN, Blocks.DARK_OAK_WALL_SIGN, Blocks.JUNGLE_WALL_SIGN, Blocks.OAK_WALL_SIGN, Blocks.SPRUCE_WALL_SIGN);

	/**
	 * Flowers and stuff like that
	 */
	private static final BlockSet explicitFootWalkableBlocks = new BlockSet(
			Blocks.TALL_GRASS,
			Blocks.CORNFLOWER,
			Blocks.CHORUS_FLOWER,
			Blocks.SUNFLOWER,
			Blocks.WHEAT,
			Blocks.CARROTS,
			Blocks.POTATOES,
			Blocks.PUMPKIN_STEM,
			Blocks.MELON_STEM, 
			Blocks.CHORUS_PLANT,
			Blocks.KELP_PLANT,
			Blocks.RED_MUSHROOM,
			Blocks.BROWN_MUSHROOM, 
			Blocks.REDSTONE_WIRE,
			Blocks.SNOW,
			Blocks.NETHER_WART,
            Blocks.CHORUS_FLOWER,
            Blocks.CHORUS_PLANT,
            Blocks.END_ROD,
            Blocks.BEETROOTS,
			Blocks.DEAD_BUSH,
			Blocks.ROSE_BUSH).unionWith(RAILS).unionWith(CARPET).unionWith(SAPLING).unionWith(SIGN).unionWith(WALL_SIGN);

	/**
	 * Blocks that fall down.
	 */
	public static final BlockSet FALLING = new BlockSet(Blocks.GRAVEL,
			Blocks.SAND).unionWith(CONCRETE_POWDER);

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
	public static final BlockSet HEAD_CAN_WALK_THROUGH = new BlockSet(
			Blocks.AIR,
			Blocks.KELP_PLANT,
			Blocks.CHORUS_PLANT,
			Blocks.SUGAR_CANE)
			.unionWith(TORCH);

	public static final BlockSet FEET_CAN_WALK_THROUGH = explicitFootWalkableBlocks
			.unionWith(HEAD_CAN_WALK_THROUGH);

	private static final BlockSet explicitSafeSideBlocks = new BlockSet(
			Blocks.ANVIL, 
			Blocks.COBBLESTONE_WALL, 
			Blocks.CACTUS, 
			Blocks.SUGAR_CANE,
			Blocks.COBWEB,
			Blocks.GLASS_PANE,
			Blocks.ENCHANTING_TABLE,
			Blocks.LILY_PAD,
			Blocks.BREWING_STAND, 
			Blocks.VINE,
			Blocks.CHEST,
			Blocks.TRAPPED_CHEST, 
			Blocks.TRIPWIRE, 
			Blocks.TRIPWIRE_HOOK,
			Blocks.STONE_PRESSURE_PLATE,
			Blocks.STONE_BUTTON,
			Blocks.DRAGON_EGG,
			Blocks.TURTLE_EGG)
			.unionWith(FENCE).unionWith(FENCE_GATE)
			.unionWith(BED)
			.unionWith(WOODEN_PRESSURE_PLATE)
			.unionWith(WOODEN_BUTTON);

	/**
	 * Blocks we can just walk over/next to without problems.
	 */
	public static final BlockSet SIMPLE_CUBE = new BlockSet(
			Blocks.BEDROCK,
			Blocks.END_STONE_BRICKS,
			Blocks.BOOKSHELF,
			Blocks.BRICKS,
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
			Blocks.EMERALD_BLOCK,
			Blocks.PURPUR_BLOCK,
			Blocks.PURPUR_PILLAR,
			Blocks.PURPUR_SLAB,
			Blocks.NETHER_BRICKS,
			Blocks.RED_NETHER_BRICKS,
			Blocks.BONE_BLOCK,
			Blocks.EMERALD_ORE,
			Blocks.OBSERVER,
			// FIXME: Not a cube.
			Blocks.FARMLAND, Blocks.FURNACE, Blocks.GLASS, Blocks.GLOWSTONE,
			Blocks.GRASS, Blocks.GOLD_BLOCK, Blocks.GOLD_ORE,
			Blocks.CLAY, Blocks.IRON_BLOCK, Blocks.IRON_ORE,
			Blocks.LAPIS_BLOCK, Blocks.LAPIS_ORE,
			Blocks.MELON,
			Blocks.MOSSY_COBBLESTONE,
			Blocks.MYCELIUM,
			Blocks.NETHER_BRICKS,
			Blocks.RED_NETHER_BRICKS,
			Blocks.END_STONE_BRICKS,
			Blocks.NETHERRACK,
			// Watch out, this cannot be broken easily !
			Blocks.OBSIDIAN, Blocks.PACKED_ICE, Blocks.PUMPKIN,
			Blocks.QUARTZ_BLOCK, Blocks.NETHER_QUARTZ_ORE, Blocks.RED_MUSHROOM_BLOCK,
			Blocks.REDSTONE_BLOCK, Blocks.REDSTONE_LAMP, Blocks.REDSTONE_ORE,
			Blocks.SANDSTONE, Blocks.FROSTED_ICE,
			Blocks.SNOW,
			// FIXME: Not a cube.
			Blocks.SOUL_SAND, Blocks.GRASS_PATH,
			Blocks.STONE, Blocks.BRICKS,
			Blocks.SNOW_BLOCK)
			.unionWith(WOOL)
			.unionWith(LEAVES)
			.unionWith(LOGS)
			.unionWith(CONCRETE)
			.unionWith(STAINED_GLASS)
			.unionWith(GLAZED_TERRACOTTA)
			.unionWith(PLANKS);

	/**
	 * All stairs. It is no problem to walk on them.
	 */
	public static final BlockSet STAIRS = new BlockSet(Blocks.ACACIA_STAIRS,
			Blocks.BIRCH_STAIRS, Blocks.BRICK_STAIRS, Blocks.DARK_OAK_STAIRS,
			Blocks.JUNGLE_STAIRS, Blocks.NETHER_BRICK_STAIRS, Blocks.PURPUR_STAIRS,
			Blocks.OAK_STAIRS, Blocks.SANDSTONE_STAIRS, Blocks.SPRUCE_STAIRS,
			Blocks.STONE_BRICK_STAIRS, Blocks.STONE_STAIRS, Blocks.STONE_SLAB,
			Blocks.QUARTZ_STAIRS)
			.unionWith(WOODEN_SLAB);

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
            //Fix SHULKER_BOX !!!!! Deals Damage to walking Player but found no matching category.
            // Blocks.SHULKER_BOX,
            //Fix NETHER_WART_BLOCK !!!!! Deals Damage to walking Player but found no matching category.
            Blocks.NETHER_WART_BLOCK,
			Blocks.OBSIDIAN);


	public static final BlockSet LOWER_SLABS;
	static {
		BlockSet lower = BlockSets.EMPTY;
		/* TODO for (int i = 0; i < 8; i++) {
			lower = lower.unionWith(new BlockMetaSet(Blocks.STONE_SLAB, i));
			lower = lower.unionWith(new BlockMetaSet(Blocks.WOODEN_SLAB, i));
		}
		lower = lower.unionWith(new BlockMetaSet(Blocks.STONE_SLAB2, 0));*/
		LOWER_SLABS = lower;
	}

	public static final BlockSet UPPER_SLABS;

	public static final BlockSet WATER = new BlockSet(Blocks.WATER);

	public static final BlockSet TREE_BLOCKS = LOGS.unionWith(LEAVES);
	public static final BlockSet FURNACE = new BlockSet(Blocks.FURNACE);

	static {
		BlockSet upper = BlockSets.EMPTY;
		/* TODO for (int i = 0; i < 8; i++) {
			upper = upper.unionWith(new BlockMetaSet(Blocks.STONE_SLAB, i + 8));
			upper = upper
					.unionWith(new BlockMetaSet(Blocks.WOODEN_SLAB, i + 8));
		}
		upper = upper.unionWith(new BlockMetaSet(Blocks.STONE_SLAB2, 8));*/
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
