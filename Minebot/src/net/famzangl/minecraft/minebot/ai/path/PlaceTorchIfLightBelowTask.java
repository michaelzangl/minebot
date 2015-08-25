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
import java.util.Arrays;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.BlockItemFilter;
import net.famzangl.minecraft.minebot.ai.task.PlaceTorchSomewhereTask;
import net.famzangl.minecraft.minebot.ai.task.SkipWhenSearchingPrefetch;
import net.famzangl.minecraft.minebot.ai.task.TaskOperations;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

@SkipWhenSearchingPrefetch
public class PlaceTorchIfLightBelowTask extends PlaceTorchSomewhereTask {

	private static final BlockItemFilter TORCH_FILTER = new BlockItemFilter(
			Blocks.torch);
	private final BlockPos currentPos;
	private final float torchLightLevel;
	private boolean attempted;

	public PlaceTorchIfLightBelowTask(BlockPos currentPos,
			EnumFacing doNotPlaceAt, float torchLightLevel) {
		super(Arrays.asList(currentPos, currentPos.add(0, 1, 0)),
				getDirections(doNotPlaceAt));
		this.currentPos = currentPos;
		this.torchLightLevel = torchLightLevel;
	}

	private static EnumFacing[] getDirections(EnumFacing except) {
		final ArrayList<EnumFacing> allowed = new ArrayList<EnumFacing>();
		for (final EnumFacing d : EnumFacing.values()) {
			if (d != except && d != EnumFacing.UP) {
				allowed.add(d);
			}
		}
		return allowed.toArray(new EnumFacing[allowed.size()]);
	}

	@Override
	public boolean isFinished(AIHelper h) {
		return (!attempted && (h.getLightAt(currentPos) > torchLightLevel
				|| !h.canSelectItem(TORCH_FILTER))) || super.isFinished(h);
	}
	
	@Override
	public void runTick(AIHelper h, TaskOperations o) {
		attempted  = true;
		super.runTick(h, o);
	}
	
	

}
