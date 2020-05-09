package net.famzangl.minecraft.minebot.ai.path.world;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SlabBlock;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.math.BlockPos;

import java.util.stream.Stream;

/**
 * This is a helper class that holds lots of block sets and helper functions to
 * check blocks for safety.
 * 
 * @author Michael Zangl
 */
public class BlockSets {

	public static final BlockSet EMPTY = BlockSet.builder().build();

	public static final BlockSet AIR = BlockSet.builder().add(Blocks.AIR, Blocks.VOID_AIR, Blocks.CAVE_AIR).build();
	/**
	 * All rail blocks.
	 */
	public static final BlockSet RAILS = BlockSet.builder().add(
			Blocks.POWERED_RAIL,
			Blocks.DETECTOR_RAIL, 
			Blocks.RAIL, 
			Blocks.ACTIVATOR_RAIL).build();
    public static final BlockSet WOOL = BlockSet.builder().add(
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
	).build();

	public static final BlockSet TERRACOTTA = BlockSet.builder().add(
			Blocks.BLACK_TERRACOTTA,
			Blocks.BLUE_TERRACOTTA,
			Blocks.BROWN_TERRACOTTA,
			Blocks.CYAN_TERRACOTTA,
			Blocks.GRAY_TERRACOTTA,
			Blocks.GREEN_TERRACOTTA,
			Blocks.LIGHT_BLUE_TERRACOTTA,
			Blocks.LIGHT_GRAY_TERRACOTTA,
			Blocks.LIME_TERRACOTTA,
			Blocks.MAGENTA_TERRACOTTA,
			Blocks.ORANGE_TERRACOTTA,
			Blocks.PINK_TERRACOTTA,
			Blocks.PURPLE_TERRACOTTA,
			Blocks.RED_TERRACOTTA,
			Blocks.WHITE_TERRACOTTA,
			Blocks.YELLOW_TERRACOTTA
	).build();
	public static final BlockSet STAINED_GLASS = BlockSet.builder().add(
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
	).build();
    public static final BlockSet STAINED_GLASS_PANE = BlockSet.builder().add(
    		Blocks.BLACK_STAINED_GLASS_PANE,
			Blocks.BLUE_STAINED_GLASS_PANE,
			Blocks.BROWN_STAINED_GLASS_PANE,
			Blocks.CYAN_STAINED_GLASS_PANE,
			Blocks.GRAY_STAINED_GLASS_PANE,
			Blocks.GREEN_STAINED_GLASS_PANE,
			Blocks.LIGHT_BLUE_STAINED_GLASS_PANE,
			Blocks.LIGHT_GRAY_STAINED_GLASS_PANE,
			Blocks.LIME_STAINED_GLASS_PANE,
			Blocks.MAGENTA_STAINED_GLASS_PANE,
			Blocks.ORANGE_STAINED_GLASS_PANE,
			Blocks.PINK_STAINED_GLASS_PANE,
			Blocks.PURPLE_STAINED_GLASS_PANE,
			Blocks.RED_STAINED_GLASS_PANE,
			Blocks.WHITE_STAINED_GLASS_PANE,
			Blocks.YELLOW_STAINED_GLASS_PANE
	).build();
    
    public static final BlockSet GLAZED_TERRACOTTA = BlockSet.builder().add(
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
	).build();
    public static final BlockSet CARPET = BlockSet.builder().add(
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
	).build();

	public static final BlockSet BED = BlockSet.builder().add(
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
	).build();

	public static final BlockSet CONCRETE = BlockSet.builder().add(
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
	).build();

	public static final BlockSet CONCRETE_POWDER = BlockSet.builder().add(
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
	).build();

	public static final BlockSet FENCE = BlockSet.builder().add(
			Blocks.OAK_FENCE,
			Blocks.SPRUCE_FENCE,
			Blocks.BIRCH_FENCE,
			Blocks.JUNGLE_FENCE,
			Blocks.DARK_OAK_FENCE,
			Blocks.ACACIA_FENCE,
			Blocks.NETHER_BRICK_FENCE).build();

	public static final BlockSet WOODEN_DOR = BlockSet.builder().add(
			Blocks.OAK_DOOR,
			Blocks.SPRUCE_DOOR,
			Blocks.BIRCH_DOOR,
			Blocks.JUNGLE_DOOR,
			Blocks.DARK_OAK_DOOR,
			Blocks.ACACIA_DOOR).build();

	public static final BlockSet WOODEN_SLAB = BlockSet.builder().add(
			Blocks.OAK_SLAB,
			Blocks.SPRUCE_SLAB,
			Blocks.BIRCH_SLAB,
			Blocks.JUNGLE_SLAB,
			Blocks.DARK_OAK_SLAB,
			Blocks.ACACIA_SLAB).build();

	public static final BlockSet WOODEN_PRESSURE_PLATE = BlockSet.builder().add(
			Blocks.OAK_PRESSURE_PLATE,
			Blocks.SPRUCE_PRESSURE_PLATE,
			Blocks.BIRCH_PRESSURE_PLATE,
			Blocks.JUNGLE_PRESSURE_PLATE,
			Blocks.DARK_OAK_PRESSURE_PLATE,
			Blocks.ACACIA_PRESSURE_PLATE).build();

	public static final BlockSet WOODEN_BUTTON = BlockSet.builder().add(
			Blocks.OAK_BUTTON,
			Blocks.SPRUCE_BUTTON,
			Blocks.BIRCH_BUTTON,
			Blocks.JUNGLE_BUTTON,
			Blocks.DARK_OAK_BUTTON,
			Blocks.ACACIA_BUTTON).build();

	public static final BlockSet SAPLING = BlockSet.builder().add(
			Blocks.OAK_SAPLING,
			Blocks.SPRUCE_SAPLING,
			Blocks.BIRCH_SAPLING,
			Blocks.JUNGLE_SAPLING,
			Blocks.DARK_OAK_SAPLING,
			Blocks.ACACIA_SAPLING).build();

	public static final BlockSet SIGN = BlockSet.builder().add(
			Blocks.OAK_SIGN,
			Blocks.SPRUCE_SIGN,
			Blocks.BIRCH_SIGN,
			Blocks.JUNGLE_SIGN,
			Blocks.DARK_OAK_SIGN,
			Blocks.ACACIA_SIGN).build();

	public static final BlockSet FENCE_GATE = BlockSet.builder().add(
			Blocks.OAK_FENCE_GATE,
			Blocks.SPRUCE_FENCE_GATE,
			Blocks.BIRCH_FENCE_GATE,
			Blocks.JUNGLE_FENCE_GATE,
			Blocks.DARK_OAK_FENCE_GATE,
			Blocks.ACACIA_FENCE_GATE).build();

	/**
	 * All leaves. FIXME: Only consider leaves that do not decay as safe ground.
	 */
	public static final BlockSet LEAVES = BlockSet.builder().add(
			Blocks.OAK_LEAVES,
			Blocks.SPRUCE_LEAVES,
			Blocks.BIRCH_LEAVES,
			Blocks.JUNGLE_LEAVES,
			Blocks.DARK_OAK_LEAVES,
			Blocks.ACACIA_LEAVES).build();

	public static final BlockSet PLANKS = BlockSet.builder().add(
			Blocks.OAK_PLANKS,
			Blocks.SPRUCE_PLANKS,
			Blocks.BIRCH_PLANKS,
			Blocks.JUNGLE_PLANKS,
			Blocks.DARK_OAK_PLANKS,
			Blocks.ACACIA_PLANKS).build();

	public static final BlockSet LOGS = BlockSet.builder().add(
			Blocks.OAK_LOG,
			Blocks.SPRUCE_LOG,
			Blocks.BIRCH_LOG,
			Blocks.JUNGLE_LOG,
			Blocks.DARK_OAK_LOG,
			Blocks.ACACIA_LOG).build();

	public static final BlockSet STRIPPED_WOOD = BlockSet.builder().add(
			Blocks.STRIPPED_OAK_WOOD,
			Blocks.STRIPPED_SPRUCE_WOOD,
			Blocks.STRIPPED_BIRCH_WOOD,
			Blocks.STRIPPED_JUNGLE_WOOD,
			Blocks.STRIPPED_DARK_OAK_WOOD,
			Blocks.STRIPPED_ACACIA_WOOD).build();

	public static final BlockSet STRIPPED_LOGS = BlockSet.builder().add(
			Blocks.STRIPPED_OAK_LOG,
			Blocks.STRIPPED_SPRUCE_LOG,
			Blocks.STRIPPED_BIRCH_LOG,
			Blocks.STRIPPED_JUNGLE_LOG,
			Blocks.STRIPPED_DARK_OAK_LOG,
			Blocks.STRIPPED_ACACIA_LOG).build();


	public static final BlockSet LOWER_SLABS;
	public static final BlockSet UPPER_SLABS;
	public static final BlockSet DOUBLE_SLABS;
	static {
		BlockSet.Builder lower = BlockSet.builder();
		BlockSet.Builder upper = BlockSet.builder();
		BlockSet.Builder both = BlockSet.builder();
		Stream.of(
				Blocks.OAK_SLAB,
				Blocks.SPRUCE_SLAB,
				Blocks.BIRCH_SLAB,
				Blocks.JUNGLE_SLAB,
				Blocks.ACACIA_SLAB,
				Blocks.DARK_OAK_SLAB,
				Blocks.STONE_SLAB,
				Blocks.SMOOTH_STONE_SLAB,
				Blocks.SANDSTONE_SLAB,
				Blocks.CUT_SANDSTONE_SLAB,
				Blocks.PETRIFIED_OAK_SLAB,
				Blocks.COBBLESTONE_SLAB,
				Blocks.BRICK_SLAB,
				Blocks.STONE_BRICK_SLAB,
				Blocks.NETHER_BRICK_SLAB,
				Blocks.QUARTZ_SLAB,
				Blocks.RED_SANDSTONE_SLAB,
				Blocks.CUT_RED_SANDSTONE_SLAB,
				Blocks.PURPUR_SLAB,
				Blocks.POLISHED_GRANITE_SLAB,
				Blocks.SMOOTH_RED_SANDSTONE_SLAB,
				Blocks.MOSSY_STONE_BRICK_SLAB,
				Blocks.POLISHED_DIORITE_SLAB,
				Blocks.MOSSY_COBBLESTONE_SLAB,
				Blocks.END_STONE_BRICK_SLAB,
				Blocks.SMOOTH_SANDSTONE_SLAB,
				Blocks.SMOOTH_QUARTZ_SLAB,
				Blocks.GRANITE_SLAB,
				Blocks.ANDESITE_SLAB,
				Blocks.RED_NETHER_BRICK_SLAB,
				Blocks.POLISHED_ANDESITE_SLAB,
				Blocks.DIORITE_SLAB,
				Blocks.PRISMARINE_SLAB,
				Blocks.PRISMARINE_BRICK_SLAB,
				Blocks.DARK_PRISMARINE_SLAB).map(Block::getDefaultState)
				.flatMap(state -> Stream.of(state, state.with(SlabBlock.WATERLOGGED, true)))
				.forEach(state -> {
			lower.add(state.with(SlabBlock.TYPE, SlabType.BOTTOM));
			upper.add(state.with(SlabBlock.TYPE, SlabType.TOP));
			both.add(state.with(SlabBlock.TYPE, SlabType.DOUBLE));
		});
		LOWER_SLABS = lower.build();
		UPPER_SLABS = upper.build();
		DOUBLE_SLABS = both.build();
	}
	public static final BlockSet HALF_SLABS = BlockSet.builder().add(LOWER_SLABS).add(UPPER_SLABS).build();


	public static final BlockSet WALL_SIGN = BlockSet.builder().add(Blocks.ACACIA_WALL_SIGN, Blocks.BIRCH_WALL_SIGN, Blocks.DARK_OAK_WALL_SIGN, Blocks.JUNGLE_WALL_SIGN, Blocks.OAK_WALL_SIGN, Blocks.SPRUCE_WALL_SIGN).build();

	/**
	 * Flowers and stuff like that
	 */
	private static final BlockSet explicitFootWalkableBlocks = BlockSet.builder().add(
			Blocks.TALL_GRASS,
			Blocks.CORNFLOWER,
			Blocks.CHORUS_FLOWER,
			Blocks.SUNFLOWER,
			Blocks.POPPY,
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
			Blocks.ROSE_BUSH,
			Blocks.GRASS_BLOCK).add(RAILS).add(CARPET).add(SAPLING).add(SIGN).add(WALL_SIGN).build();

	/**
	 * Blocks that fall down.
	 */
	public static final BlockSet FALLING = BlockSet.builder().add(Blocks.GRAVEL,
			Blocks.SAND).add(CONCRETE_POWDER).build();

	public static final BlockSet WALL_TORCHES = BlockSet.builder()
			.add(Blocks.WALL_TORCH, Blocks.REDSTONE_WALL_TORCH).build();

	/**
	 * Torches.
	 */
	public static final BlockSet FLOOR_TORCHES = BlockSet.builder().add(
			Blocks.TORCH,
			Blocks.REDSTONE_TORCH).build();

	public static final BlockSet TORCH = BlockSet.builder().add(FLOOR_TORCHES).add(WALL_TORCHES).build();


	/**
	 * Blocks our head can walk though. Signs could be added here, but we stay
	 * away from them for now.
	 */
	public static final BlockSet HEAD_CAN_WALK_THROUGH = BlockSet.builder().add(
			Blocks.KELP_PLANT,
			Blocks.CHORUS_PLANT,
			Blocks.SUGAR_CANE).add(AIR).add(TORCH).build();

	public static final BlockSet FEET_CAN_WALK_THROUGH = BlockSet.builder().add(explicitFootWalkableBlocks).add(HEAD_CAN_WALK_THROUGH).build();

	private static final BlockSet explicitSafeSideBlocks = BlockSet.builder().add(
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
			Blocks.TURTLE_EGG).add(FENCE).add(FENCE_GATE).add(BED).add(WOODEN_PRESSURE_PLATE).add(WOODEN_BUTTON).build();

	/**
	 * Blocks we can just walk over/next to without problems.
	 */
	public static final BlockSet SIMPLE_CUBE = BlockSet.builder().add(
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
			Blocks.PODZOL,
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
			Blocks.GRASS_BLOCK, Blocks.GOLD_BLOCK, Blocks.GOLD_ORE,
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
			// FIXME: Both are not cubes
			Blocks.SOUL_SAND, Blocks.GRASS_PATH,
			Blocks.STONE, Blocks.BRICKS,
			Blocks.SNOW_BLOCK,
			Blocks.DIORITE,
			Blocks.POLISHED_ANDESITE,
			Blocks.POLISHED_DIORITE,
			Blocks.POLISHED_GRANITE).add(WOOL).add(LEAVES)
			.add(LOGS).add(STRIPPED_LOGS).add(STRIPPED_WOOD)
			.add(CONCRETE).add(STAINED_GLASS).add(TERRACOTTA).add(GLAZED_TERRACOTTA).add(PLANKS).add(DOUBLE_SLABS).build();

	/**
	 * All stairs. It is no problem to walk on them.
	 */
	public static final BlockSet STAIRS = BlockSet.builder().add(Blocks.ACACIA_STAIRS,
			Blocks.BIRCH_STAIRS, Blocks.BRICK_STAIRS, Blocks.DARK_OAK_STAIRS,
			Blocks.JUNGLE_STAIRS, Blocks.NETHER_BRICK_STAIRS, Blocks.PURPUR_STAIRS,
			Blocks.OAK_STAIRS, Blocks.SANDSTONE_STAIRS, Blocks.SPRUCE_STAIRS,
			Blocks.STONE_BRICK_STAIRS, Blocks.STONE_STAIRS, Blocks.STONE_SLAB,
			Blocks.QUARTZ_STAIRS).build();

	/**
	 * Blocks that form a solid ground.
	 */
	public static final BlockSet SAFE_GROUND = BlockSet.builder().add(SIMPLE_CUBE).add(FALLING).add(HALF_SLABS).build();

	public static final BlockSet SAFE_SIDE = 
			BlockSet.builder().add(explicitSafeSideBlocks).add(SAFE_GROUND).add(FEET_CAN_WALK_THROUGH).add(AIR).build();

	public static final BlockSet SAFE_CEILING = BlockSet.builder()
			.add(STAIRS)
			.add(HALF_SLABS)
			.add(FEET_CAN_WALK_THROUGH)
			.add(SIMPLE_CUBE)
			.add(AIR).add(Blocks.VINE, Blocks.CACTUS).build();

	/**
	 * Blocks you need to destroy but that are then safe.
	 */
	public static final BlockSet SAFE_AFTER_DESTRUCTION = BlockSet.builder().add(
			Blocks.VINE, Blocks.CACTUS).build();


	/**
	 * Blocks that are considered indestructable and should be avoided.
	 */
	public static final BlockSet INDESTRUCTABLE = BlockSet.builder().add(
			Blocks.BEDROCK,
			Blocks.BARRIER,
            //Fix SHULKER_BOX !!!!! Deals Damage to walking Player but found no matching category.
            // Blocks.SHULKER_BOX,
            //Fix NETHER_WART_BLOCK !!!!! Deals Damage to walking Player but found no matching category.
            Blocks.NETHER_WART_BLOCK,
			Blocks.OBSIDIAN).build();

	public static final BlockSet WATER = BlockSet.builder().add(Blocks.WATER).build();

	public static final BlockSet TREE_BLOCKS = BlockSet.builder().add(LOGS).add(LEAVES).build();
	public static final BlockSet FURNACE = BlockSet.builder().add(Blocks.FURNACE).build();

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
