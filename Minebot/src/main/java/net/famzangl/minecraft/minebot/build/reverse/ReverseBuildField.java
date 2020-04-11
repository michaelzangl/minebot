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
package net.famzangl.minecraft.minebot.build.reverse;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;

/**
 * A field where all tasks from the build reverser are stored.
 * 
 * @author michael
 *
 */
public class ReverseBuildField {
	private final Block[][][] buildBlocks;
	private final TaskDescription[][][] buildNames;

	public ReverseBuildField(int lx, int ly, int lz) {
		buildBlocks = new Block[lx][ly][lz];
		buildNames = new TaskDescription[lx][ly][lz];
	}

	public void setBlockAt(BlockPos relativePos, Block block,
			TaskDescription taskString) {
		buildBlocks[relativePos.getX()][relativePos.getY()][relativePos.getZ()] = block;
		buildNames[relativePos.getX()][relativePos.getY()][relativePos.getZ()] = taskString;
	}

	public Block getBlock(int x, int y, int z) {
		return buildBlocks[x][y][z];
	}
}
