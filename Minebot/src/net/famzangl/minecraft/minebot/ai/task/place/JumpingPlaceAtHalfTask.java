package net.famzangl.minecraft.minebot.ai.task.place;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.ItemFilter;
import net.famzangl.minecraft.minebot.ai.task.BlockSide;
import net.minecraftforge.common.util.ForgeDirection;

public class JumpingPlaceAtHalfTask extends JumpingPlaceBlockAtFloorTask {

	public final static ForgeDirection[] TRY_FOR_LOWER = new ForgeDirection[] {
			ForgeDirection.DOWN, ForgeDirection.EAST, ForgeDirection.NORTH,
			ForgeDirection.WEST, ForgeDirection.SOUTH };

	public final static ForgeDirection[] TRY_FOR_UPPER = new ForgeDirection[] {
			ForgeDirection.EAST, ForgeDirection.NORTH, ForgeDirection.WEST,
			ForgeDirection.SOUTH };

	protected BlockSide side;
	protected ForgeDirection lookingDirection = ForgeDirection.UNKNOWN;

	private int attempts;

	public JumpingPlaceAtHalfTask(int x, int y, int z, ItemFilter filter,
			BlockSide side) {
		super(x, y, z, filter);
		this.side = side;
	}

	@Override
	protected void faceBlock(AIHelper h) {
		final ForgeDirection[] dirs = getBuildDirs();
		for (int i = 0; i < dirs.length; i++) {
			if (faceSideBlock(h, dirs[attempts++ % dirs.length])) {
				return;
			}
		}
		h.desync();
	}

	protected ForgeDirection[] getBuildDirs() {
		return side == BlockSide.UPPER_HALF ? TRY_FOR_UPPER : TRY_FOR_LOWER;
	}

	protected boolean faceSideBlock(AIHelper h, ForgeDirection dir) {
		System.out.println("Facing side " + dir);
		final int x2 = x + dir.offsetX;
		final int y2 = getPlaceAtY() + dir.offsetY;
		final int z2 = z + dir.offsetZ;
		if (h.isAirBlock(x2, y2, z2)) {
			return false;
		} else {
			h.faceSideOf(x2, y2, z2, dir.getOpposite(),
					getSide(dir) == BlockSide.UPPER_HALF ? 0.5 : 0,
					getSide(dir) == BlockSide.LOWER_HALF ? 0.5 : 1,
					h.getMinecraft().thePlayer.posX - x,
					h.getMinecraft().thePlayer.posZ - z, lookingDirection);
			return true;
		}
	}

	protected boolean isFacing(AIHelper h, ForgeDirection dir) {
		return h.isFacingBlock(x + dir.offsetX, getPlaceAtY() + dir.offsetY, z
				+ dir.offsetZ, dir.getOpposite(), getSide(dir));
	}

	private BlockSide getSide(ForgeDirection dir) {
		return dir.offsetY < 0 ? BlockSide.UPPER_HALF : BlockSide.LOWER_HALF;
	}

	@Override
	protected boolean isFacingRightBlock(AIHelper h) {
		for (final ForgeDirection d : getBuildDirs()) {
			if (isFacing(h, d)) {
				return true;
			}
		}
		return false;
	}
}
