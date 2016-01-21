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
package net.famzangl.minecraft.minebot.ai.path.world;

import net.famzangl.minecraft.minebot.ai.scripting.EntityPos;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

/**
 * This class holds some useful methods to handle the {@link BlockPos}
 * 
 * @author Michael Zangl
 * 
 */
public class Pos {
	
	public static BlockPos ZERO = new BlockPos(0,0,0);

	private Pos() {
	}

	public static BlockPos fromDir(EnumFacing dir) {
		return ZERO.offset(dir);
	}

	public static BlockPos[] fromDir(EnumFacing[] standable) {
		final BlockPos[] res = new BlockPos[standable.length];
		for (int i = 0; i < res.length; i++) {
			res[i] = fromDir(standable[i]);
		}
		return res;
	}

	public static BlockPos minPos(BlockPos p1, BlockPos p2) {
		return new BlockPos(Math.min(p1.getX(), p2.getX()), Math.min(p1.getY(), p2.getY()), Math.min(
				p1.getZ(), p2.getZ()));
	}

	public static BlockPos maxPos(BlockPos p1, BlockPos p2) {
		return new BlockPos(Math.max(p1.getX(), p2.getX()), Math.max(p1.getY(), p2.getY()), Math.max(
				p1.getZ(), p2.getZ()));
	}

	public static double length(double dx, double dy, double dz) {
		return Math.sqrt(dx * dx + dy * dy + dz * dz);
	}
	
	public static String niceString(BlockPos pos) {
		return pos.getX() + ", " + pos.getY() + ", " + pos.getZ();
	}
}