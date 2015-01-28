package net.famzangl.minecraft.minebot.ai.task.place;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.ItemFilter;
import net.famzangl.minecraft.minebot.ai.strategy.TaskOperations;
import net.famzangl.minecraft.minebot.ai.task.BlockSide;
import net.famzangl.minecraft.minebot.ai.task.error.StringTaskError;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class JumpingPlaceAtHalfTask extends JumpingPlaceBlockAtFloorTask {

	public final static EnumFacing[] TRY_FOR_LOWER = new EnumFacing[] {
			EnumFacing.DOWN, EnumFacing.EAST, EnumFacing.NORTH,
			EnumFacing.WEST, EnumFacing.SOUTH };

	public final static EnumFacing[] TRY_FOR_UPPER = new EnumFacing[] {
			EnumFacing.EAST, EnumFacing.NORTH, EnumFacing.WEST,
			EnumFacing.SOUTH };

	protected BlockSide side;
	protected EnumFacing lookingDirection = null;

	private int attempts;

	public JumpingPlaceAtHalfTask(BlockPos pos, ItemFilter filter,
			BlockSide side) {
		super(pos, filter);
		this.side = side;
	}

	@Override
	protected void faceBlock(AIHelper h, TaskOperations o) {
		final EnumFacing[] dirs = getBuildDirs();
		for (int i = 0; i < dirs.length; i++) {
			if (faceSideBlock(h, dirs[attempts++ % dirs.length])) {
				return;
			}
		}
		o.desync(new StringTaskError("Could not face anywhere to place."));
	}

	protected EnumFacing[] getBuildDirs() {
		return side == BlockSide.UPPER_HALF ? TRY_FOR_UPPER : TRY_FOR_LOWER;
	}

	protected boolean faceSideBlock(AIHelper h, EnumFacing dir) {
		System.out.println("Facing side " + dir);
		BlockPos facingBlock = getPlaceAtPos().offset(dir);
		if (h.isAirBlock(facingBlock)) {
			return false;
		} else {
			h.faceSideOf(facingBlock, dir.getOpposite(),
					getSide(dir) == BlockSide.UPPER_HALF ? 0.5 : 0,
					getSide(dir) == BlockSide.LOWER_HALF ? 0.5 : 1,
					h.getMinecraft().thePlayer.posX - pos.getX(),
					h.getMinecraft().thePlayer.posZ - pos.getZ(), lookingDirection);
			return true;
		}
	}

	protected BlockSide getSide(EnumFacing dir) {
		return dir == EnumFacing.DOWN ? BlockSide.UPPER_HALF : BlockSide.LOWER_HALF;
	}

	@Override
	protected boolean isFacingRightBlock(AIHelper h) {
		for (final EnumFacing d : getBuildDirs()) {
			if (isFacing(h, d)) {
				return true;
			}
		}
		return false;
	}
}
