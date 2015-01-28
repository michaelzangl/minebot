package net.famzangl.minecraft.minebot.ai.task.place;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.BlockItemFilter;
import net.famzangl.minecraft.minebot.ai.task.BlockSide;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class SneakAndPlaceAtSideTask extends SneakAndPlaceAtHalfTask {

	public SneakAndPlaceAtSideTask(BlockPos pos, BlockItemFilter filter,
			BlockPos relativeFrom, double minBuildHeight,
			EnumFacing lookingDirection, BlockSide side) {
		super(pos, filter, relativeFrom, minBuildHeight, side);
		this.lookingDirection = lookingDirection;
	}

	@Override
	protected EnumFacing[] getBuildDirs() {
		return new EnumFacing[] { lookingDirection,
				lookingDirection.rotateY(),
				lookingDirection.rotateYCCW() };
	}

	@Override
	protected boolean isFacingRightBlock(AIHelper h) {
		if (h.getLookDirection() != lookingDirection) {
			System.out.println("Not the right dir!");
			return false;
		} else {
			return super.isFacingRightBlock(h);
		}
	}

	@Override
	public String toString() {
		return "SneakAndPlaceAtSideTask [lookingDirection=" + lookingDirection
				+ ", side=" + side + "]";
	}

}
