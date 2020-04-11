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

public class AlongTrackPathFinder extends MovePathFinder {
	protected final int dx;
	protected final int dz;
	protected final int cx;
	protected final int cy;
	protected final int cz;
	protected final int length;

	public AlongTrackPathFinder(int dx, int dz, int cx, int cy, int cz, int length) {
		this.dx = dx;
		this.dz = dz;
		this.cx = cx;
		this.cy = cy;
		this.cz = cz;
		this.length = length;
	}

	protected boolean isOnTrack(int x, int z) {
		return (dz != 0 && x == cx && dz * (z - cz) >= 0 || dx != 0 && z == cz
				&& dx * (x - cx) >= 0) && (length < 0 || getStepNumber(x, z) <= length);
	}

	/**
	 * Only works if (x, y, z) is on track.
	 * 
	 * @param x
	 * @param z
	 * @return
	 */
	protected int getStepNumber(int x, int z) {
		return Math.abs(x - cx + z - cz);
	}

}
