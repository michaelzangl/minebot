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
package net.famzangl.minecraft.minebot.ai.task;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.BlockItemFilter;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSets;
import net.famzangl.minecraft.minebot.ai.task.error.SelectTaskError;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

/**
 * Place a torch on one of the given positions. Attempts to place it somewhere
 * 
 * <p>
 * TODO: Do a better search for blocks we cannot attach a torch on.
 * 
 * @author michael
 * 
 */
@SkipWhenSearchingPrefetch
public class PlaceTorchSomewhereTask extends AITask {
	private LinkedList<PosAndDir> attemptOnPositions;
	private final List<BlockPos> places;
	private final EnumFacing[] preferedDirection;
	private BlockPos lastAttempt;

	private int delayAfter;

	private static class PosAndDir {
		public final BlockPos place;
		public final EnumFacing dir;
		public int attemptsLeft = 10;

		public PosAndDir(BlockPos place, EnumFacing dir) {
			super();
			this.place = place;
			this.dir = dir;
		}

		public BlockPos getPlaceOn() {
			return place.offset(dir);
		}

		@Override
		public String toString() {
			return "PosAndDir [place=" + place + ", dir=" + dir
					+ ", attemptsLeft=" + attemptsLeft + "]";
		}
	}

	/**
	 * Create a new task-
	 * 
	 * @param positions
	 *            The positions on which the torch should be placed.
	 * @param preferedDirectionThe
	 *            direction in which the stick of the torch should be mounted.
	 */
	public PlaceTorchSomewhereTask(List<BlockPos> positions,
			EnumFacing... preferedDirection) {
		super();
		this.places = positions;
		this.preferedDirection = preferedDirection;
	}

	@Override
	public boolean isFinished(AIHelper h) {
		return isImpossible(h) || (placeWasSuccessful(h) && delayAfter == 0);
	}

	private boolean isImpossible(AIHelper h) {
		for (BlockPos p : places) {
			if (BlockSets.AIR.isAt(h.getWorld(), p)) {
				return false;
			}
		}
		return true;
	}

	private boolean placeWasSuccessful(AIHelper h) {
		final PosAndDir place = getNextPlace(h);
		return place == null || lastAttempt != null
				&& BlockSets.TORCH.isAt(h.getWorld(), lastAttempt);
	}

	private PosAndDir getNextPlace(AIHelper h) {
		if (attemptOnPositions == null) {
			attemptOnPositions = new LinkedList<PlaceTorchSomewhereTask.PosAndDir>();
			for (final BlockPos p : places) {
				for (final EnumFacing d : preferedDirection) {
					final PosAndDir current = new PosAndDir(p, d);
					final BlockPos placeOn = current.getPlaceOn();
					if (!BlockSets.AIR.isAt(h.getWorld(), placeOn)) {
						attemptOnPositions.add(current);
					}
				}
			}
		}

		while (!attemptOnPositions.isEmpty()
				&& (attemptOnPositions.peekFirst().attemptsLeft <= 0 || !BlockSets.AIR
						.isAt(h.getWorld(),
								attemptOnPositions.peekFirst().place))) {
			attemptOnPositions.removeFirst();
		}

		return attemptOnPositions.peekFirst();
	}

	@Override
	public void runTick(AIHelper h, TaskOperations o) {
		if (delayAfter > 0) {
			delayAfter--;
			return;
		}

		final BlockItemFilter f = new BlockItemFilter(Blocks.torch);
		if (!h.selectCurrentItem(f)) {
			o.desync(new SelectTaskError(f));
		}

		final PosAndDir next = getNextPlace(h);
		final BlockPos placeOn = next.getPlaceOn();
		h.faceSideOf(placeOn, next.dir.getOpposite());
		if (h.isFacingBlock(placeOn, next.dir.getOpposite())) {
			h.overrideUseItem();
			delayAfter = 10;
		}
		next.attemptsLeft--;
		lastAttempt = next.place;
	}

	@Override
	public int getGameTickTimeout(AIHelper helper) {
		return super.getGameTickTimeout(helper) * 3;
	}

	@Override
	public String toString() {
		return "PlaceTorchSomewhereTask [places=" + places
				+ ", preferedDirection=" + Arrays.toString(preferedDirection)
				+ "]";
	}

}
