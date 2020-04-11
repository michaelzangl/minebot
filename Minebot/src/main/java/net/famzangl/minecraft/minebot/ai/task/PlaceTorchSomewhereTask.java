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

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.BlockItemFilter;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSets;
import net.famzangl.minecraft.minebot.ai.task.error.SelectTaskError;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

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
	private final Direction[] preferedDirection;
	private BlockPos lastAttempt;

	private int delayAfter;

	private static class PosAndDir {
		public final BlockPos place;
		public final Direction dir;
		public int attemptsLeft = 10;

		public PosAndDir(BlockPos place, Direction dir) {
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
	 * @param preferedDirection The
	 *            direction in which the stick of the torch should be mounted.
	 */
	public PlaceTorchSomewhereTask(List<BlockPos> positions,
			Direction... preferedDirection) {
		super();
		this.places = positions;
		this.preferedDirection = preferedDirection;
	}

	@Override
	public boolean isFinished(AIHelper aiHelper) {
		return isImpossible(aiHelper) || (placeWasSuccessful(aiHelper) && delayAfter == 0);
	}

	private boolean isImpossible(AIHelper aiHelper) {
		for (BlockPos p : places) {
			if (BlockSets.AIR.isAt(aiHelper.getWorld(), p)) {
				return false;
			}
		}
		return true;
	}

	private boolean placeWasSuccessful(AIHelper aiHelper) {
		final PosAndDir place = getNextPlace(aiHelper);
		return place == null || lastAttempt != null
				&& BlockSets.TORCH.isAt(aiHelper.getWorld(), lastAttempt);
	}

	private PosAndDir getNextPlace(AIHelper aiHelper) {
		if (attemptOnPositions == null) {
			attemptOnPositions = new LinkedList<PlaceTorchSomewhereTask.PosAndDir>();
			for (final BlockPos place : places) {
				for (final Direction direction : preferedDirection) {
					final PosAndDir current = new PosAndDir(place, direction);
					final BlockPos placeOn = current.getPlaceOn();
					if (!BlockSets.AIR.isAt(aiHelper.getWorld(), placeOn)) {
						attemptOnPositions.add(current);
					}
				}
			}
		}

		while (!attemptOnPositions.isEmpty()
				&& (attemptOnPositions.peekFirst().attemptsLeft <= 0 || !BlockSets.AIR
						.isAt(aiHelper.getWorld(),
								attemptOnPositions.peekFirst().place))) {
			attemptOnPositions.removeFirst();
		}

		return attemptOnPositions.peekFirst();
	}

	@Override
	public void runTick(AIHelper aiHelper, TaskOperations taskOperations) {
		if (delayAfter > 0) {
			delayAfter--;
			return;
		}

		final BlockItemFilter itemFilter = new BlockItemFilter(Blocks.TORCH);
		if (!aiHelper.selectCurrentItem(itemFilter)) {
			taskOperations.desync(new SelectTaskError(itemFilter));
		}

		final PosAndDir next = getNextPlace(aiHelper);
		final BlockPos placeOn = next.getPlaceOn();
		aiHelper.faceSideOf(placeOn, next.dir.getOpposite());
		if (aiHelper.isFacingBlock(placeOn, next.dir.getOpposite())) {
			aiHelper.overrideUseItem();
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
