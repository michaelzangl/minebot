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

import java.util.BitSet;

import net.famzangl.minecraft.minebot.ai.path.world.Pos;
import net.minecraft.util.BlockPos;

/**
 * A simple 3-dimensional Bitset.
 * @author michael
 *
 */
public class BlockFlagTable {
	private final BitSet bits = new BitSet();
	private final BlockPos max;
	private final BlockPos min;

	public BlockFlagTable(BlockPos pos1, BlockPos pos2) {
		this.max = Pos.maxPos(pos1, pos2);
		this.min = Pos.minPos(pos1, pos2);
	}

	public void setBit(int x,int y,int z, boolean value) {
		int index = getIndex(x, y, z);
		if (index >= 0) {
			bits.set(index, value);
		}
	}
	
	/**
	 * Get a bit.
	 * @param x
	 * @param y
	 * @param z
	 * @return The stored bit or false when no bit was stored yet or the bit is outside the range.
	 */
	public boolean getBit(int x, int y, int z) {
		int index = getIndex(x, y, z);
		if (index >= 0) {
			return bits.get(index);
		} else {
			return false;
		}
	}

	private int getIndex(int x, int y, int z) {
		if (x < min.getX() || x > max.getX() || y < min.getY() || y > max.getY() || z < min.getZ() || z > max.getZ()) {
			return -1;
		}
		int v = z - min.getZ();
		v *= (max.getY() - min.getY() + 1);
		v += y - min.getY();
		v *= (max.getX() - min.getX() + 1);
		v += x - min.getX();
		return v;
	}
}
