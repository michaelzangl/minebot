package net.famzangl.minecraft.minebot.build.blockbuild;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.famzangl.minecraft.minebot.ai.task.BlockSide;
import net.famzangl.minecraft.minebot.ai.task.JumpingPlaceAtHalfTask;
import net.famzangl.minecraft.minebot.ai.task.SneakAndPlaceAtHalfTask;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

public class BuildHalfslabTask extends CubeBuildTask {

	public static final Block[] BLOCKS = new Block[] { Blocks.stone_slab,
			Blocks.wooden_slab };
	private BlockSide side;

	protected BuildHalfslabTask(Pos forPosition, String slabType, String up) {
		super(forPosition, new SlabFilter(SlabType.valueOf(slabType
				.toUpperCase())));
		this.side = "up".equalsIgnoreCase(up) ? BlockSide.UPPER_HALF
				: BlockSide.LOWER_HALF;
	}

	@Override
	public AITask getPlaceBlockTask(Pos relativeFromPos) {
		if (!isStandablePlace(relativeFromPos)) {
			return null;
		} else if (relativeFromPos.equals(FROM_GROUND)) {
			return new JumpingPlaceAtHalfTask(forPosition.x, forPosition.y + 1,
					forPosition.z, blockFilter, side);
		} else {
			return new SneakAndPlaceAtHalfTask(forPosition.x,
					forPosition.y + 1, forPosition.z, blockFilter,
					relativeFromPos, getMinHeightToBuild(), side);
		}
	}

	@Override
	public String toString() {
		return "BuildHalfslabTask [side=" + side + ", blockFilter="
				+ blockFilter + ", forPosition=" + forPosition + "]";
	}
}
