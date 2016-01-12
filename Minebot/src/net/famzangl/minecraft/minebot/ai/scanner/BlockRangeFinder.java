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

import net.famzangl.minecraft.minebot.ai.path.MovePathFinder;
import net.famzangl.minecraft.minebot.settings.MinebotSettingsRoot;
import net.famzangl.minecraft.minebot.settings.PathfindingSetting;
import net.minecraft.util.BlockPos;

public class BlockRangeFinder extends MovePathFinder {
	protected BlockRangeScanner rangeScanner;
	
	public BlockRangeFinder() {
		allowedGroundForUpwardsBlocks = allowedGroundBlocks;
//		footAllowedBlocks = BlockSets.FEET_CAN_WALK_THROUGH;
//		headAllowedBlocks = BlockSets.HEAD_CAN_WALK_TRHOUGH;
//		footAllowedBlocks = footAllowedBlocks.intersectWith(forbiddenBlocks.invert());
//		headAllowedBlocks = headAllowedBlocks.intersectWith(forbiddenBlocks.invert());
	}
	
	@Override
	protected PathfindingSetting loadSettings(MinebotSettingsRoot settingsRoot) {
		return settingsRoot.getPathfinding().getWalking();
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