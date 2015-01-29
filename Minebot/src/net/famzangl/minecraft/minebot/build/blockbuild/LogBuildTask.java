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

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.BlockItemFilter;
import net.famzangl.minecraft.minebot.ai.BlockWhitelist;
import net.famzangl.minecraft.minebot.build.block.LogItemFilter;
import net.famzangl.minecraft.minebot.build.block.WoodType;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

/**
 * Build a log. Logs can be placed in 3 directions.
 * 
 * @author michael
 *
 */
public class LogBuildTask extends CubeBuildTask {

	public static final BlockWhitelist BLOCKS = new BlockWhitelist(Blocks.log,
			Blocks.log2);
	public static final BlockPos[] UP_DOWN_POS = new BlockPos[] { Pos.ZERO };
	public static final BlockPos[] NORTH_SOUTH_POS = new BlockPos[] {
			new BlockPos(0, 1, 1), new BlockPos(0, 1, -1) };
	public static final BlockPos[] EAST_WEST_POS = new BlockPos[] {
			new BlockPos(1, 1, 0), new BlockPos(-1, 1, 0) };
	private final EnumFacing dir;

	public LogBuildTask(BlockPos forPosition, WoodType logType,
			EnumFacing direction) {
		this(forPosition, new LogItemFilter(logType), direction);
	}

	private LogBuildTask(BlockPos forPosition, BlockItemFilter logItemFilter,
			EnumFacing direction) {
		super(forPosition, logItemFilter);
		dir = direction;
	}

	@Override
	public BlockPos[] getStandablePlaces() {
		switch (dir) {
		case EAST:
		case WEST:
			return EAST_WEST_POS;
		case SOUTH:
		case NORTH:
			return NORTH_SOUTH_POS;
		default:
			return UP_DOWN_POS;
		}
	}

	@Override
	public BuildTask withPositionAndRotation(BlockPos add, int rotateSteps,
			MirrorDirection mirror) {
		EnumFacing newDir = dir;
		for (int i = 0; i < rotateSteps; i++) {
			newDir = newDir.rotateY();
		}

		return new LogBuildTask(add, blockFilter, newDir);
	}
}
