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

import net.minecraft.util.BlockPos;

public class GoToPathfinder extends WalkingPathfinder {
	private final BlockPos position;

	public GoToPathfinder(BlockPos position) {
		this.position = position;
	}
	
	@Override
	protected boolean runSearch(BlockPos playerPosition) {
		if (playerPosition.equals(position)) {
			return true;
		}
		return super.runSearch(playerPosition);
	}

	@Override
	protected float rateDestination(int distance, int x, int y, int z) {
		return position.getX() == x && position.getY() == y && position.getZ() == z ? 1 : -1;
	}
}
