package net.famzangl.minecraft.minebot.ai.path;

import java.util.ArrayList;
import java.util.Collections;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.BlockWhitelist;
import net.famzangl.minecraft.minebot.ai.task.DestroyInRangeTask;
import net.famzangl.minecraft.minebot.ai.task.PlaceTorchSomewhereTask;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class TunnelPathFinder extends AlongTrackPathFinder {

	private final static BlockWhitelist tunneled = AIHelper.air.unionWith(AIHelper.torches);
	
	private final int addToSide;
	private final int addToTop;
	private final TorchSide torches;

	public static enum TorchSide {
		NONE(false, false, false),
		LEFT(true, false, false),
		RIGHT(false, true, false),
		BOTH(true, true, false),
		FLOOR(false, false, true);

		private final boolean left;
		private final boolean right;
		private final boolean floor;

		private TorchSide(boolean left, boolean right, boolean floor) {
			this.left = left;
			this.right = right;
			this.floor = floor;
		}
	}

	public TunnelPathFinder(int dx, int dz, int cx, int cy, int cz,
			int addToSide, int addToTop, TorchSide torches, Integer length) {
		super(dx, dz, cx, cy, cz, length);
		this.addToSide = addToSide;
		this.addToTop = addToTop;
		this.torches = torches;
	}

	@Override
	protected float rateDestination(int distance, int x, int y, int z) {
		if (isOnTrack(x, z)
				&& y == cy
				&& !tunneled.contains(helper.getBlockId(x, y, z))) {
			return distance + 1;
		} else {
			return -1;
		}
	}

	@Override
	protected void addTasksForTarget(BlockPos currentPos) {
		BlockPos p1, p2;
		if (dx == 0) {
			p1 = currentPos.add(addToSide, 0, 0);
			p2 = currentPos.add(-addToSide, 1 + addToTop, 0);
		} else {
			p1 = currentPos.add(0, 0, addToSide);
			p2 = currentPos.add(0, 1 + addToTop, -addToSide);
		}
		addTask(new DestroyInRangeTask(p1, p2));

		final boolean isTorchStep = getStepNumber(currentPos.getX(), currentPos.getZ()) % 8 == 0;
		if (torches.right && isTorchStep) {
			addTorchesTask(currentPos, -dz, dx);
		}
		if (torches.left && isTorchStep) {
			addTorchesTask(currentPos, dz, -dx);
		}
		if (torches.floor && isTorchStep) {
			addTask(new PlaceTorchSomewhereTask(
					Collections.singletonList(currentPos), EnumFacing.DOWN));
		}
	}

	private void addTorchesTask(BlockPos currentPos, int dirX, int dirZ) {
		final ArrayList<BlockPos> positions = new ArrayList<BlockPos>();
		positions.add(new Pos(currentPos.getX() + dirX * addToSide,
				currentPos.getY() + 1, currentPos.getZ() + dirZ * addToSide));

		for (int i = addToSide; i >= 0; i--) {
			positions.add(new Pos(currentPos.getX() + dirX * i, currentPos.getY(),
					currentPos.getZ() + dirZ * i));
		}
		addTask(new PlaceTorchSomewhereTask(positions,
				AIHelper.getDirectionForXZ(dirX, dirZ), EnumFacing.DOWN));
	}

	@Override
	public String toString() {
		return "TunnelPathFinder [addToSide=" + addToSide + ", addToTop="
				+ addToTop + ", dx=" + dx + ", dz=" + dz + ", cx=" + cx
				+ ", cy=" + cy + ", cz=" + cz + ", torches=" + torches + "]";
	}

}
