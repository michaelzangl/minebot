package net.famzangl.minecraft.minebot.settings;

import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSets;
import net.minecraft.block.Blocks;

public class PathfindingSettings {
	private static final BlockSet defaultUpwardsBlocks = BlockSet.builder().add(
			Blocks.DIRT, Blocks.STONE, Blocks.COBBLESTONE, Blocks.SAND,
			Blocks.NETHERRACK).build();
	private static final BlockSet allowedForUpwards = BlockSet.builder().add(BlockSets.SAFE_GROUND)
			.add(BlockSets.FEET_CAN_WALK_THROUGH).build();
	private static final BlockSet destructableBlocks =
			BlockSet.builder().add(BlockSets.SAFE_AFTER_DESTRUCTION)
			.add(BlockSets.SAFE_CEILING).add(BlockSets.FALLING)
			.build();
			// TODO:	.intersectWith(BlockSets.INDESTRUCTABLE.invert())


	private PathfindingSetting destructive = new PathfindingSetting(
			BlockSets.SAFE_GROUND, allowedForUpwards, destructableBlocks,
			destructableBlocks, defaultUpwardsBlocks);
	private PathfindingSetting nonDestructiveLumberjack = new PathfindingSetting(
			BlockSets.SAFE_GROUND, BlockSets.SAFE_GROUND,
			BlockSet.builder().add(BlockSets.FEET_CAN_WALK_THROUGH).add(BlockSets.TREE_BLOCKS).build(),
			BlockSet.builder().add(BlockSets.HEAD_CAN_WALK_THROUGH).add(BlockSets.TREE_BLOCKS).build(),
			defaultUpwardsBlocks);
	private PathfindingSetting nonDestructive = new PathfindingSetting(
			BlockSets.SAFE_GROUND, BlockSets.SAFE_GROUND,
			BlockSets.FEET_CAN_WALK_THROUGH, BlockSets.HEAD_CAN_WALK_THROUGH,
			defaultUpwardsBlocks);
	private PathfindingSetting planting = new PathfindingSetting(
			BlockSets.SAFE_GROUND, BlockSets.SAFE_GROUND,
			BlockSets.FEET_CAN_WALK_THROUGH, BlockSets.HEAD_CAN_WALK_THROUGH,
			defaultUpwardsBlocks);
	private PathfindingSetting construction = new PathfindingSetting(
			BlockSets.SAFE_GROUND, BlockSets.SAFE_GROUND,
			BlockSets.FEET_CAN_WALK_THROUGH, BlockSets.HEAD_CAN_WALK_THROUGH,
			defaultUpwardsBlocks);
	private PathfindingSetting walking = new PathfindingSetting(
			BlockSets.SAFE_GROUND, BlockSets.SAFE_GROUND,
			BlockSets.FEET_CAN_WALK_THROUGH, BlockSets.HEAD_CAN_WALK_THROUGH,
			defaultUpwardsBlocks);

	public PathfindingSetting getDestructivePathfinder() {
		return destructive;
	}

	public PathfindingSetting getConstructionPathfinder() {
		return construction;
	}

	public PathfindingSetting getNonDestructiveLumberjack() {
		return nonDestructiveLumberjack;
	}

	public PathfindingSetting getPlanting() {
		return planting;
	}

	public PathfindingSetting getWalking() {
		return walking;
	}

	public PathfindingSetting getNonDestructive() {
		return nonDestructive;
	}
}
