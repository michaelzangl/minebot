package net.famzangl.minecraft.minebot.settings;

import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSets;
import net.minecraft.init.Blocks;

public class PathfindingSettings {
	private static final BlockSet defaultUpwardsBlocks = new BlockSet(
			Blocks.dirt, Blocks.stone, Blocks.cobblestone, Blocks.sand,
			Blocks.netherrack);
	private static final BlockSet allowedForUpwards = BlockSets.SAFE_GROUND
			.unionWith(BlockSets.FEET_CAN_WALK_THROUGH);
	private static final BlockSet destructableBlocks = BlockSets.SAFE_AFTER_DESTRUCTION
			.unionWith(BlockSets.SAFE_CEILING).unionWith(BlockSets.FALLING);

	private PathfindingSetting destructive = new PathfindingSetting(
			BlockSets.SAFE_GROUND, allowedForUpwards, destructableBlocks,
			destructableBlocks, defaultUpwardsBlocks);
	private PathfindingSetting nonDestructiveLumberjack = new PathfindingSetting(
			BlockSets.SAFE_GROUND, BlockSets.SAFE_GROUND,
			BlockSets.FEET_CAN_WALK_THROUGH, BlockSets.HEAD_CAN_WALK_TRHOUGH,
			defaultUpwardsBlocks);
	private PathfindingSetting planting = new PathfindingSetting(
			BlockSets.SAFE_GROUND, BlockSets.SAFE_GROUND,
			BlockSets.FEET_CAN_WALK_THROUGH, BlockSets.HEAD_CAN_WALK_TRHOUGH,
			defaultUpwardsBlocks);
	private PathfindingSetting construction = new PathfindingSetting(
			BlockSets.SAFE_GROUND, BlockSets.SAFE_GROUND,
			BlockSets.FEET_CAN_WALK_THROUGH, BlockSets.HEAD_CAN_WALK_TRHOUGH,
			defaultUpwardsBlocks);
	private PathfindingSetting walking = new PathfindingSetting(
			BlockSets.SAFE_GROUND, BlockSets.SAFE_GROUND,
			BlockSets.FEET_CAN_WALK_THROUGH, BlockSets.HEAD_CAN_WALK_TRHOUGH,
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
}
