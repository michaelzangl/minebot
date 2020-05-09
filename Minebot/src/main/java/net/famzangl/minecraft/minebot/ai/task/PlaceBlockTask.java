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
import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.famzangl.minecraft.minebot.ai.task.error.SelectTaskError;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

/**
 * Place a block on a given side of an other block. Useful for e.g. attaching a
 * torch on a wall.
 * 
 * @author michael
 * 
 */
public class PlaceBlockTask extends AITask {
	private final BlockPos placeOn;
	private final Direction onSide;
	private final BlockSet blocks;
	private int attemptsLeft = 20;

	/**
	 * 
	 * @param placeOn
	 *            Where to place the block on (the existing block to click).
	 * @param onSide
	 *            On which side the block should be placed.
	 * @param blocks
	 *            The Block to place.
	 */
	public PlaceBlockTask(BlockPos placeOn, Direction onSide, BlockSet blocks) {
		super();
		if (placeOn == null || onSide == null || blocks == null) {
			throw new NullPointerException();
		}
		this.placeOn = placeOn;
		this.onSide = onSide;
		this.blocks = blocks;
	}

	@Override
	public boolean isFinished(AIHelper aiHelper) {
		return attemptsLeft <= 0
				|| blocks.contains(aiHelper.getBlockState(placeOn.offset(onSide)));
	}

	@Override
	public void runTick(AIHelper aiHelper, TaskOperations taskOperations) {
		final BlockItemFilter itemFilter = new BlockItemFilter(blocks);
		if (!aiHelper.selectCurrentItem(itemFilter)) {
			taskOperations.desync(new SelectTaskError(itemFilter));
		}

		aiHelper.faceSideOf(placeOn, onSide);
		if (aiHelper.isFacingBlock(placeOn, onSide)) {
			aiHelper.overrideUseItem();
		}
		attemptsLeft--;
	}

	@Override
	public String toString() {
		return "PlaceBlockTask{" +
				"placeOn=" + placeOn +
				", onSide=" + onSide +
				", blocks=" + blocks +
				'}';
	}
}
