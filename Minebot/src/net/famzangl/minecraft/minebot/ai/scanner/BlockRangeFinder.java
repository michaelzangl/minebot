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
package net.famzangl.minecraft.minebot.ai.scanner;

import net.famzangl.minecraft.minebot.ai.path.WalkingPathfinder;
import net.minecraft.util.math.BlockPos;

public class BlockRangeFinder extends WalkingPathfinder {
	protected BlockRangeScanner rangeScanner;
	
	public BlockRangeFinder() {
	}

	@Override
	protected boolean runSearch(BlockPos playerPosition) {
		if (rangeScanner == null) {
			rangeScanner = constructScanner(playerPosition);
			//TODO: Pass on a synchronized world instance...
			rangeScanner.startAsync(world);
			return false;
		} else if (!rangeScanner.isScaningFinished()) {
			return false;
		} else {
			return super.runSearch(playerPosition);
		}
	}

	protected BlockRangeScanner constructScanner(BlockPos playerPosition) {
		return new BlockRangeScanner(playerPosition);
	}
}