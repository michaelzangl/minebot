package net.famzangl.minecraft.minebot.ai.task;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.BlockItemFilter;
import net.minecraftforge.common.util.ForgeDirection;

public class SneakAndPlaceAtHalfTask extends SneakAndPlaceTask {

	protected final BlockSide side;

	protected ForgeDirection lookingDirection = ForgeDirection.UNKNOWN;

	protected final ForgeDirection[] DIRS = new ForgeDirection[] {
			ForgeDirection.EAST, ForgeDirection.NORTH, ForgeDirection.WEST,
			ForgeDirection.SOUTH };

	private int attempts;

	protected ForgeDirection[] getBuildDirs() {
		return DIRS;
	}

	public SneakAndPlaceAtHalfTask(int x, int y, int z, BlockItemFilter filter,
			Pos relativeFrom, double minBuildHeight, BlockSide side) {
		super(x, y, z, filter, relativeFrom, minBuildHeight);
		this.side = side;
	}

	@Override
	protected void faceBlock(AIHelper h) {
		ForgeDirection[] dirs = getBuildDirs();
		for (int i = 0; i < dirs.length; i++) {
			ForgeDirection useSide = dirs[attempts % dirs.length];
			if (!h.isAirBlock(x + useSide.offsetX, y - 1, z + useSide.offsetZ)) {
				faceSideBlock(h, useSide);
				return;
			}
			attempts++;
		}
		h.desync();
	}

	private void faceSideBlock(AIHelper h, ForgeDirection useSide) {
		h.faceSideOf(x + useSide.offsetX, y - 1, z + useSide.offsetZ,
				useSide.getOpposite(), side == BlockSide.UPPER_HALF ? 0.5 : 0,
				side == BlockSide.LOWER_HALF ? 0.5 : 1,
				h.getMinecraft().thePlayer.posX - x,
				h.getMinecraft().thePlayer.posZ - z, lookingDirection);
	}

	private boolean isFacing(AIHelper h, ForgeDirection dir) {
		return h.isFacingBlock(x + dir.offsetX, y - 1, z + dir.offsetZ,
				dir.getOpposite(), side);
	}

	@Override
	protected boolean isFacingRightBlock(AIHelper h) {
		for (ForgeDirection d : getBuildDirs()) {
			if (isFacing(h, d)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "SneakAndPlaceAtHalfTask [side=" + side + ", x=" + x + ", y="
				+ y + ", z=" + z + ", filter=" + filter + ", relativeFrom="
				+ relativeFrom + "]";
	}

}
