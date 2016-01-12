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
import net.famzangl.minecraft.minebot.ai.command.BlockWithDataOrDontcare;
import net.famzangl.minecraft.minebot.ai.path.world.BlockMetaSet;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.famzangl.minecraft.minebot.build.block.LogItemFilter;
import net.famzangl.minecraft.minebot.build.block.WoodType;
import net.famzangl.minecraft.minebot.build.block.WoodType.LogDirection;
import net.minecraft.util.BlockPos;

/**
 * Build a log. Logs can be placed in 3 directions.
 * 
 * @author michael
 *
 */
public class LogBuildTask extends BlockBuildTask {
	public static final BlockSet NORMAL_LOGS;
	static {
		BlockMetaSet logs = new BlockMetaSet();
		for (LogDirection d : LogDirection.values()) {
			logs = logs.unionWith(d.blocks);
		}
		NORMAL_LOGS = logs;
	}
	public static final BlockPos[] UP_DOWN_POS = new BlockPos[] { Pos.ZERO };
	public static final BlockPos[] NORTH_SOUTH_POS = new BlockPos[] {
			new BlockPos(0, 1, 1), new BlockPos(0, 1, -1) };
	public static final BlockPos[] EAST_WEST_POS = new BlockPos[] {
			new BlockPos(1, 1, 0), new BlockPos(-1, 1, 0) };

	public LogBuildTask(BlockPos forPosition,
			BlockWithDataOrDontcare blockWithMeta) {
		super(forPosition, blockWithMeta);
	}

	@Override
	protected BlockItemFilter getItemToPlaceFilter() {
		return new LogItemFilter(WoodType.getFor(blockToPlace));
	}

	@Override
	public BlockPos[] getStandablePlaces() {
		switch (LogDirection.forData(blockToPlace)) {
		case X:
			return EAST_WEST_POS;
		case Z:
			return NORTH_SOUTH_POS;
		default:
			return UP_DOWN_POS;
		}
	}

	@Override
	public BuildTask withPositionAndRotation(BlockPos add, int rotateSteps,
			MirrorDirection mirror) {
		LogDirection oldDir = LogDirection.forData(blockToPlace);
		LogDirection newDir = rotateSteps % 2 == 0 ? oldDir : oldDir.rotateY();
		WoodType wt = WoodType.getFor(blockToPlace);
		return new LogBuildTask(add, wt.getBlockWithMeta(newDir));
	}
}
