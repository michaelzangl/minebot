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
package net.famzangl.minecraft.minebot.ai.task.error;

import net.minecraft.util.math.BlockPos;

public class PositionTaskError extends TaskError {

	private final BlockPos expectedPosition;

	public PositionTaskError(BlockPos expectedPosition) {
		super("Not standing on " + expectedPosition.getX() + ", " + expectedPosition.getY() + ", " + expectedPosition.getZ() + ".");
		this.expectedPosition = expectedPosition;
	}

	public PositionTaskError(int x, int y, int z) {
		this(new BlockPos(x, y, z));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ (expectedPosition == null ? 0 : expectedPosition.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final PositionTaskError other = (PositionTaskError) obj;
		if (expectedPosition == null) {
			if (other.expectedPosition != null) {
				return false;
			}
		} else if (!expectedPosition.equals(other.expectedPosition)) {
			return false;
		}
		return true;
	}

}
