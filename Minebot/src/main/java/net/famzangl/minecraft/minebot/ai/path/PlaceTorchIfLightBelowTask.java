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

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.BlockItemFilter;
import net.famzangl.minecraft.minebot.ai.task.PlaceTorchSomewhereTask;
import net.famzangl.minecraft.minebot.ai.task.SkipWhenSearchingPrefetch;
import net.famzangl.minecraft.minebot.ai.task.TaskOperations;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Arrays;

@SkipWhenSearchingPrefetch
public class PlaceTorchIfLightBelowTask extends PlaceTorchSomewhereTask {

	private static final BlockItemFilter TORCH_FILTER = new BlockItemFilter(Blocks.TORCH);
	private final BlockPos currentPos;
	private final float torchLightLevel;
	private boolean attempted;

	public PlaceTorchIfLightBelowTask(BlockPos currentPos,
			Direction doNotPlaceAt, float torchLightLevel) {
		super(Arrays.asList(currentPos, currentPos.add(0, 1, 0)),
				getDirections(doNotPlaceAt));
		this.currentPos = currentPos;
		this.torchLightLevel = torchLightLevel;
	}

	private static Direction[] getDirections(Direction except) {
		final ArrayList<Direction> allowed = new ArrayList<Direction>();
		for (final Direction d : Direction.values()) {
			if (d != except && d != Direction.UP) {
				allowed.add(d);
			}
		}
		return allowed.toArray(new Direction[allowed.size()]);
	}

	@Override
	public boolean isFinished(AIHelper aiHelper) {
		return (!attempted && (aiHelper.getLightAt(currentPos) > torchLightLevel
				|| !aiHelper.canSelectItem(TORCH_FILTER))) || super.isFinished(aiHelper);
	}
	
	@Override
	public void runTick(AIHelper aiHelper, TaskOperations taskOperations) {
		attempted  = true;
		super.runTick(aiHelper, taskOperations);
	}
	
	

}
