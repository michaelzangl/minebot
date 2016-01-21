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
import java.util.Hashtable;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSets;
import net.famzangl.minecraft.minebot.ai.path.world.Pos;
import net.famzangl.minecraft.minebot.ai.path.world.WorldWithDelta;
import net.famzangl.minecraft.minebot.ai.task.DestroyInRangeTask;
import net.famzangl.minecraft.minebot.ai.task.PlaceTorchSomewhereTask;
import net.famzangl.minecraft.minebot.ai.task.RunOnceTask;
import net.famzangl.minecraft.minebot.ai.task.SkipWhenSearchingPrefetch;
import net.famzangl.minecraft.minebot.ai.task.TaskOperations;
import net.famzangl.minecraft.minebot.ai.utils.BlockCuboid;
import net.famzangl.minecraft.minebot.ai.utils.BlockFilteredArea;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class TunnelPathFinder extends AlongTrackPathFinder {
	/**
	 * Marks a section of the tunnel as done.
	 * 
	 * @author Michael Zangl
	 */
	@SkipWhenSearchingPrefetch
	private final class MarkAsDoneTask extends RunOnceTask {
		private final int stepNumber;

		private MarkAsDoneTask(int stepNumber) {
			this.stepNumber = stepNumber;
		}

		@Override
		protected void runOnce(AIHelper h, TaskOperations o) {
			finishedTunnels.set(stepNumber);
			inQueueTunnels.clear(stepNumber);
			o.faceAndDestroyForNextTask();
		}
		
		@Override
		public void onCanceled() {
			inQueueTunnels.clear(stepNumber);
			super.onCanceled();
		}

		@Override
		public boolean applyToDelta(WorldWithDelta world) {
			return true;
		}

		@Override
		public String toString() {
			return "MarkAsDoneTask [stepNumber=" + stepNumber + "]";
		}
	}

	/**
	 * Marks that we have reached a given section of the tunnel.
	 * 
	 * @author Michael Zangl
	 */
	@SkipWhenSearchingPrefetch
	private final class MarkAsReachedTask extends RunOnceTask {
		private final int stepNumber;

		private MarkAsReachedTask(int stepNumber) {
			this.stepNumber = stepNumber;
		}

		@Override
		protected void runOnce(AIHelper h, TaskOperations o) {
			tunnelPositionStartCount.put(stepNumber,
					getStartCount(stepNumber) + 1);

			synchronized (currentStepNumberMutex) {
				currentStepNumber = stepNumber;
			}
			o.faceAndDestroyForNextTask();
		}

		@Override
		public boolean applyToDelta(WorldWithDelta world) {
			return true;
		}

		@Override
		public String toString() {
			return "MarkAsReachedTask [stepNumber=" + stepNumber + "]";
		}
	}

	/**
	 * The side of the tunnel at which the torch is placed.
	 * 
	 * @author Michael Zangl
	 *
	 */
	public enum TorchSide {
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

	private final static BlockSet FREE_TUNNEL_BLOCKS = BlockSets.AIR
			.unionWith(BlockSets.TORCH);

	private BitSet finishedTunnels = new BitSet();
	private BitSet inQueueTunnels = new BitSet();
	/**
	 * How often did we attempt to tunnel at a given position?
	 */
	private Hashtable<Integer, Integer> tunnelPositionStartCount = new Hashtable<Integer, Integer>();

	/**
	 * How much more to tunnel to the side.
	 */
	private final int addToSide;
	private final int addToTop;
	private final TorchSide torches;

	private final boolean addBranches;

	private int currentStepNumber = 0;
	private final Object currentStepNumberMutex = new Object();

	public TunnelPathFinder(int dx, int dz, int cx, int cy, int cz,
			int addToSide, int addToTop, TorchSide torches, int length) {
		super(dx, dz, cx, cy, cz, length);
		this.addToSide = Math.max(0, addToSide);
		this.addToTop = addToTop;
		this.torches = torches;
		this.addBranches = addToSide < 0;
	}

	@Override
	protected float rateDestination(int distance, int x, int y, int z) {
		if (shouldTunnel(x, y, z)) {
			return distance + 1;
		} else {
			return -1;
		}
	}

	/**
	 * Test if we should dig a tunnel to that position.
	 * 
	 * @param x
	 *            The x pos.
	 * @param y
	 *            The y pos
	 * @param z
	 *            The z pos
	 * @return
	 */
	private boolean shouldTunnel(int x, int y, int z) {
		if (y != cy || !isOnTrack(x, z)) {
			// not on track
			return false;
		}
		int stepNumber = getStepNumber(x, z);

		if (finishedTunnels.get(stepNumber) || inQueueTunnels.get(stepNumber)) {
			// we already handled that position.
			return false;
		}

		boolean isFree = FREE_TUNNEL_BLOCKS.isAt(world, x, y, z)
				&& FREE_TUNNEL_BLOCKS.isAt(world, x, y + 1, z);

		int startCount = getStartCount(stepNumber);

		if (startCount > 0 && startCount < 5) {
			// we started something but got stopped by e.g. eat/,,,
			return true;
		} else {
			return !isFree;
		}
	}

	private int getStartCount(int stepNumber) {
		Integer startCount = tunnelPositionStartCount.get(stepNumber);
		if (startCount == null) {
			return 0;
		} else {
			return startCount;
		}
	}

	@Override
	protected void addTasksForTarget(BlockPos currentPos) {
		final int stepNumber = getStepNumber(currentPos.getX(),
				currentPos.getZ());
		// if (getStartCount(stepNumber) < 1) {
		// tunnelPositionStartCount.put(stepNumber, 1);
		// }
		addTask(new MarkAsReachedTask(stepNumber));
		BlockPos p1, p2;
		if (dx == 0) {
			p1 = currentPos.add(addToSide, 0, 0);
			p2 = currentPos.add(-addToSide, 1 + addToTop, 0);
		} else {
			p1 = currentPos.add(0, 0, addToSide);
			p2 = currentPos.add(0, 1 + addToTop, -addToSide);
		}
		BlockCuboid tunnelArea = new BlockCuboid(p1, p2);
		BlockFilteredArea area = new BlockFilteredArea(tunnelArea,
				FREE_TUNNEL_BLOCKS.invert());
		addTask(new DestroyInRangeTask(area));

		final boolean isTorchStep = stepNumber % 8 == 0;
		// TODO: Only check for right torch.
		if (torches.right && isTorchStep && !containsTorches(tunnelArea)) {
			addTorchesTask(currentPos, -dz, dx);
		}
		if (torches.left && isTorchStep && !containsTorches(tunnelArea)) {
			addTorchesTask(currentPos, dz, -dx);
		}
		if (torches.floor && isTorchStep && !containsTorches(tunnelArea)) {
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

	private boolean containsTorches(BlockCuboid tunnelArea) {
		BlockFilteredArea torchArea = new BlockFilteredArea(tunnelArea,
				BlockSets.TORCH);
		return torchArea.getVolume(world) > 0;
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
		positions.add(new BlockPos(currentPos.getX() + dirX * addToSide, currentPos
				.getY() + 1, currentPos.getZ() + dirZ * addToSide));

		for (int i = addToSide; i >= 0; i--) {
			positions.add(new BlockPos(currentPos.getX() + dirX * i, currentPos
					.getY(), currentPos.getZ() + dirZ * i));
		}
		addTask(new PlaceTorchSomewhereTask(positions,
				AIHelper.getDirectionForXZ(dirX, dirZ), EnumFacing.DOWN));
	}

	public String getProgress() {
		synchronized (currentStepNumberMutex) {
			String str = currentStepNumber + "";
			if (length >= 0) {
				str += "/" + length;
			}
			return str + "m";
		}
	}

	@Override
	public String toString() {
		return "TunnelPathFinder [addToSide=" + addToSide + ", addToTop="
				+ addToTop + ", dx=" + dx + ", dz=" + dz + ", cx=" + cx
				+ ", cy=" + cy + ", cz=" + cz + ", torches=" + torches
				+ ", length=" + length + "]";
	}

}
