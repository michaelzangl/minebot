/*******************************************************************************
 * This file is part of Minebot.
 *
 * Minebot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Minebot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Minebot.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package net.famzangl.minecraft.minebot.ai.path;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSets;
import net.famzangl.minecraft.minebot.ai.path.world.WorldWithDelta;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.famzangl.minecraft.minebot.ai.task.DestroyInRangeTask;
import net.famzangl.minecraft.minebot.ai.task.PlaceTorchSomewhereTask;
import net.famzangl.minecraft.minebot.ai.task.SkipWhenSearchingPrefetch;
import net.famzangl.minecraft.minebot.ai.task.TaskOperations;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class TunnelPathFinder extends AlongTrackPathFinder {

	private final static BlockSet tunneled = BlockSets.AIR
			.unionWith(BlockSets.TORCH);

	private BitSet finishedTunnels = new BitSet();

	/**
	 * How much more to tunnel to the side.
	 */
	private final int addToSide;
	private final int addToTop;
	private final TorchSide torches;

	private final boolean addBranches;

	@SkipWhenSearchingPrefetch
	private final class MarkAsDoneTask extends AITask {
		private final int stepNumber;
		private boolean done = false;

		private MarkAsDoneTask(int stepNumber) {
			this.stepNumber = stepNumber;
		}

		@Override
		public void runTick(AIHelper h, TaskOperations o) {
			finishedTunnels.set(stepNumber);
			done = true;
		}

		@Override
		public boolean isFinished(AIHelper h) {
			return done;
		}

		@Override
		public boolean applyToDelta(WorldWithDelta world) {
			return true;
		}
	}

	public static enum TorchSide {
		NONE(false, false, false), LEFT(true, false, false), RIGHT(false, true,
				false), BOTH(true, true, false), FLOOR(false, false, true);

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
		this.addToSide = Math.max(0, addToSide);
		this.addToTop = addToTop;
		this.torches = torches;
		this.addBranches = addToSide < 0;
	}

	@Override
	protected float rateDestination(int distance, int x, int y, int z) {
		if (isOnTrack(x, z) && y == cy && !tunneled.isAt(world, x, y, z)
				&& !finishedTunnels.get(getStepNumber(x, z))) {
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

		final int stepNumber = getStepNumber(currentPos.getX(),
				currentPos.getZ());
		final boolean isTorchStep = stepNumber % 8 == 0;
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
		final boolean isBranchStep = stepNumber % 4 == 2;
		if (addBranches && isBranchStep) {
			addBranchTask(currentPos, -dz, dx);
			addBranchTask(currentPos, dz, -dx);
		}
		addTask(new MarkAsDoneTask(stepNumber));
	}

	private void addBranchTask(BlockPos currentPos, int dx, int dz) {
		int branchMax = 0;
		for (int i = 1; i <= 4; i++) {
			if (!isSafeBranchPos(currentPos.add(dx * i, 1, dz * i)))
				break;
			branchMax = i;
		}
		if (branchMax > 0) {
			addTask(new DestroyInRangeTask(currentPos.add(dx * 1, 1, dz * 1),
					currentPos.add(dx * branchMax, 1, dz * branchMax)));
		}
	}

	private boolean isSafeBranchPos(BlockPos pos) {
		return BlockSets.safeSideAndCeilingAround(world, pos)
				&& BlockSets.SAFE_SIDE.isAt(world, pos.add(0, -1, 0));
	}

	private void addTorchesTask(BlockPos currentPos, int dirX, int dirZ) {
		final ArrayList<BlockPos> positions = new ArrayList<BlockPos>();
		positions.add(new Pos(currentPos.getX() + dirX * addToSide, currentPos
				.getY() + 1, currentPos.getZ() + dirZ * addToSide));

		for (int i = addToSide; i >= 0; i--) {
			positions.add(new Pos(currentPos.getX() + dirX * i, currentPos
					.getY(), currentPos.getZ() + dirZ * i));
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
