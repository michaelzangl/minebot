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
package net.famzangl.minecraft.minebot.build.blockbuild;

import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.famzangl.minecraft.minebot.ai.task.place.PlaceBlockAtFloorTask;
import net.minecraft.util.math.BlockPos;

/**
 * Build something that is just standing on the ground.
 * 
 * @author michael
 *
 */
public abstract class BuildFlatOnGroundTask extends BuildTask {

	protected BuildFlatOnGroundTask(BlockPos forPosition) {
		super(forPosition);
	}

	@Override
	public BlockPos[] getStandablePlaces() {
		return new BlockPos[] { forPosition };
	}

	@Override
	public AITask getPlaceBlockTask(BlockPos relativeFromPos) {
		return new PlaceBlockAtFloorTask(forPosition, this.getRequiredItem());
	}
}
