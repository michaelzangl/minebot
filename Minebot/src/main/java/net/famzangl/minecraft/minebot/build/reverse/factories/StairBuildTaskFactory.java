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
package net.famzangl.minecraft.minebot.build.reverse.factories;

import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.famzangl.minecraft.minebot.build.blockbuild.BuildNormalStairsTask;
import net.famzangl.minecraft.minebot.build.blockbuild.BuildTask;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class StairBuildTaskFactory extends AbstractBuildTaskFactory {

	@Override
	public BlockSet getSupportedBlocks() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected BuildTask getTaskImpl(BlockPos position, BlockState block) {
		return new BuildNormalStairsTask(position, block.getBlock(), block.getValue(BlockStairs.FACING), block.getValue(BlockStairs.HALF));
	}

}
