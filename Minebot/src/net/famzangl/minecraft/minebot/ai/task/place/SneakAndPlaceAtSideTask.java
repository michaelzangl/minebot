package net.famzangl.minecraft.minebot.ai.task.place;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.BlockItemFilter;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.famzangl.minecraft.minebot.ai.task.BlockSide;
import net.minecraftforge.common.util.ForgeDirection;

public class SneakAndPlaceAtSideTask extends SneakAndPlaceAtHalfTask implements
		AITask {

	public SneakAndPlaceAtSideTask(int x, int y, int z, BlockItemFilter filter,
			Pos relativeFrom, double minBuildHeight,
			ForgeDirection lookingDirection, BlockSide side) {
		super(x, y, z, filter, relativeFrom, minBuildHeight, side);
		this.lookingDirection = lookingDirection;
	}

	@Override
	protected ForgeDirection[] getBuildDirs() {
		return new ForgeDirection[] {
				lookingDirection.getRotation(ForgeDirection.UP),
				lookingDirection.getRotation(ForgeDirection.DOWN),
				lookingDirection };
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
