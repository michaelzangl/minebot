package net.famzangl.minecraft.minebot.ai.path.world;

import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

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
			Blocks.bedrock,
			Blocks.bookshelf,
			Blocks.brick_block,
			Blocks.brown_mushroom_block,
			Blocks.cake,
			Blocks.clay,
			Blocks.coal_block,
			Blocks.coal_ore,
			Blocks.cobblestone,
			Blocks.crafting_table,
			Blocks.diamond_block,
			Blocks.diamond_ore,
			Blocks.dirt,
			Blocks.double_stone_slab,
			Blocks.double_wooden_slab,
			Blocks.emerald_block,
			Blocks.emerald_ore,
			// FIXME: Not a cube.
			Blocks.farmland, Blocks.furnace, Blocks.glass, Blocks.glowstone,
			Blocks.grass, Blocks.gold_block, Blocks.gold_ore,
			Blocks.hardened_clay, Blocks.iron_block, Blocks.iron_ore,
			Blocks.lapis_block, Blocks.lapis_ore, Blocks.leaves,
			Blocks.leaves2, Blocks.lit_pumpkin, Blocks.lit_furnace,
			Blocks.lit_redstone_lamp, Blocks.lit_redstone_ore, Blocks.log,
			Blocks.log2,
			Blocks.melon_block,
			Blocks.mossy_cobblestone,
			Blocks.mycelium,
			Blocks.nether_brick,
			Blocks.netherrack,
			// Watch out, this cannot be broken easily !
			Blocks.obsidian, Blocks.packed_ice, Blocks.planks, Blocks.pumpkin,
			Blocks.quartz_block, Blocks.quartz_ore, Blocks.red_mushroom_block,
			Blocks.redstone_block, Blocks.redstone_lamp, Blocks.redstone_ore,
			Blocks.sandstone,
			Blocks.snow,
			// FIXME: Not a cube.
			Blocks.soul_sand, Blocks.stained_glass,
			Blocks.stained_hardened_clay, Blocks.stone, Blocks.stonebrick,
			Blocks.wool);

	/**
	 * Blocks that fall down.
	 */
	public static final BlockSet FALLING = new BlockSet(Blocks.gravel,
			Blocks.sand);

	public static final BlockSet AIR = new BlockSet(Blocks.air);
	/**
	 * All stairs. It is no problem to walk on them.
	 */
	public static final BlockSet STAIRS = new BlockSet(Blocks.acacia_stairs,
			Blocks.birch_stairs, Blocks.brick_stairs, Blocks.dark_oak_stairs,
			Blocks.jungle_stairs, Blocks.nether_brick_stairs,
			Blocks.oak_stairs, Blocks.sandstone_stairs, Blocks.spruce_stairs,
			Blocks.stone_brick_stairs, Blocks.stone_stairs, Blocks.stone_slab,
			Blocks.wooden_slab, Blocks.quartz_stairs);

	/**
	 * All rail blocks.
	 */
	public static final BlockSet RAILS = new BlockSet(Blocks.golden_rail,
			Blocks.detector_rail, Blocks.rail, Blocks.activator_rail);

	/**
	 * Flowers and stuff like that
	 */
	private static final BlockSet explicitFootWalkableBlocks = new BlockSet(
			Blocks.tallgrass, Blocks.yellow_flower, Blocks.red_flower,
			Blocks.wheat, Blocks.carrots, Blocks.potatoes, Blocks.pumpkin_stem,
			Blocks.melon_stem, Blocks.carpet, Blocks.double_plant,
			Blocks.red_mushroom, Blocks.brown_mushroom, Blocks.redstone_wire,
			Blocks.sapling, Blocks.snow_layer, Blocks.nether_wart,
			Blocks.standing_sign, Blocks.wall_sign, Blocks.deadbush).unionWith(RAILS);

	/**
	 * Torches.
	 */
	public static final BlockSet TORCH = new BlockSet(Blocks.torch,
			Blocks.redstone_torch);

	/**
	 * Blocks our head can walk though. Signs could be added here, but we stay
	 * away from them for now.
	 */
	public static final BlockSet HEAD_CAN_WALK_TRHOUGH = new BlockSet(
			Blocks.air, Blocks.double_plant, Blocks.reeds).unionWith(TORCH);

	public static final BlockSet FEET_CAN_WALK_THROUGH = explicitFootWalkableBlocks
			.unionWith(HEAD_CAN_WALK_TRHOUGH);

	public static final BlockSet FENCE = new BlockSet(Blocks.oak_fence,
			Blocks.spruce_fence, Blocks.birch_fence, Blocks.jungle_fence,
			Blocks.dark_oak_fence, Blocks.acacia_fence,
			Blocks.nether_brick_fence);

	public static final BlockSet WOODEN_DOR = new BlockSet(Blocks.oak_door,
			Blocks.spruce_door, Blocks.birch_door, Blocks.jungle_door,
			Blocks.dark_oak_door, Blocks.acacia_door);

	public static final BlockSet FENCE_GATE = new BlockSet(
			Blocks.oak_fence_gate, Blocks.spruce_fence_gate,
			Blocks.birch_fence_gate, Blocks.jungle_fence_gate,
			Blocks.dark_oak_fence_gate, Blocks.acacia_fence_gate);

	private static final BlockSet explicitSafeSideBlocks = new BlockSet(
			Blocks.anvil, Blocks.cobblestone_wall, Blocks.cactus, Blocks.reeds,
			Blocks.web, Blocks.glass_pane, Blocks.bed, Blocks.enchanting_table,
			Blocks.waterlily, Blocks.brewing_stand, Blocks.vine, Blocks.chest,
			Blocks.trapped_chest, Blocks.tripwire, Blocks.tripwire_hook,
			Blocks.wooden_pressure_plate, Blocks.stone_pressure_plate,
			Blocks.wooden_button, Blocks.stone_button, Blocks.monster_egg)
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
			.unionWith(AIR).unionWith(new BlockSet(Blocks.vine, Blocks.cactus));

	/**
	 * Blocks you need to destroy but that are then safe.
	 */
	public static final BlockSet SAFE_AFTER_DESTRUCTION = new BlockSet(
			Blocks.vine, Blocks.cactus);

	/**
	 * Blocks that are considered indestructable and should be avoided.
	 */
	public static final BlockSet INDESTRUCTABLE = new BlockSet(Blocks.bedrock,
			Blocks.barrier, Blocks.obsidian);

	/**
	 * All leaves. FIXME: Only consider leaves that do not decay as safe ground.
	 */
	public static final BlockSet LEAVES = new BlockSet(Blocks.leaves,
			Blocks.leaves2);
	public static final BlockSet LOGS = new BlockSet(Blocks.log, Blocks.log2);

	public static final BlockSet LOWER_SLABS;
	static {
		BlockSet lower = BlockSets.EMPTY;
		for (int i = 0; i < 8; i++) {
			lower = lower.unionWith(new BlockMetaSet(Blocks.stone_slab, i));
			lower = lower.unionWith(new BlockMetaSet(Blocks.wooden_slab, i));
		}
		lower = lower.unionWith(new BlockMetaSet(Blocks.stone_slab2, 0));
		LOWER_SLABS = lower;
	}

	public static final BlockSet UPPER_SLABS;

	public static final BlockSet WATER = new BlockSet(Blocks.water,
			Blocks.flowing_water);

	public static final BlockSet TREE_BLOCKS = LOGS.unionWith(LEAVES);
	public static final BlockSet FURNACE = new BlockSet(Blocks.furnace, Blocks.lit_furnace);

	static {
		BlockSet upper = BlockSets.EMPTY;
		for (int i = 0; i < 8; i++) {
			upper = upper.unionWith(new BlockMetaSet(Blocks.stone_slab, i + 8));
			upper = upper
					.unionWith(new BlockMetaSet(Blocks.wooden_slab, i + 8));
		}
		upper = upper.unionWith(new BlockMetaSet(Blocks.stone_slab2, 8));
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
