package net.famzangl.minecraft.minebot.ai.task.place;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.ItemFilter;
import net.famzangl.minecraft.minebot.ai.task.BlockSide;
import net.minecraftforge.common.util.ForgeDirection;

public class JumpingPlaceBlockAtSideTask extends JumpingPlaceAtHalfTask {

	private final int attempts = 0;

	public JumpingPlaceBlockAtSideTask(int x, int y, int z, ItemFilter filter,
			ForgeDirection lookingDirection, BlockSide side) {
		super(x, y, z, filter, side);
		this.lookingDirection = lookingDirection;
	}

	// @Override
	// protected void faceBlock(AIHelper h) {
	// if (side != BlockSide.UPPER_HALF && attempts % 4 == 3) {
	// faceBottomBlock(h);
	// } else if (attempts % 4 == 1) {
	// faceSideBlock(h, lookingDirection.getRotation(ForgeDirection.UP));
	// } else if (attempts % 4 == 2) {
	// faceSideBlock(h, lookingDirection.getRotation(ForgeDirection.DOWN));
	// } else {
	// faceSideBlock(h, lookingDirection);
	// }
	// attempts++;
	// }

	@Override
	protected ForgeDirection[] getBuildDirs() {
		return side != BlockSide.UPPER_HALF ? new ForgeDirection[] {
				ForgeDirection.DOWN,
				lookingDirection.getRotation(ForgeDirection.UP),
				lookingDirection.getRotation(ForgeDirection.DOWN),
				lookingDirection } : new ForgeDirection[] {
				lookingDirection.getRotation(ForgeDirection.UP),
				lookingDirection.getRotation(ForgeDirection.DOWN),
				lookingDirection };
	}

	@Override
	protected boolean isFacingRightBlock(AIHelper h) {
		if (h.getLookDirection() != lookingDirection) {
			return false;
		} else {
			return isFacing(h, lookingDirection.getRotation(ForgeDirection.UP))
					|| isFacing(h,
							lookingDirection.getRotation(ForgeDirection.DOWN))
					|| isFacing(h, lookingDirection)
					|| side != BlockSide.UPPER_HALF
					&& h.isFacingBlock(x, getPlaceAtY() - 1, z,
							ForgeDirection.UP);
		}
	}
}
