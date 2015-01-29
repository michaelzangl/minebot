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

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.BlockItemFilter;
import net.famzangl.minecraft.minebot.ai.BlockWhitelist;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

public class FenceBuildTask extends CubeBuildTask {

	public static final BlockPos[] STANDABLE = new BlockPos[] { new BlockPos(-1, 1, 0),
			new BlockPos(0, 1, -1), new BlockPos(1, 1, 0), new BlockPos(0, 1, 1), };

	public static final BlockWhitelist BLOCKS = new BlockWhitelist(
			Blocks.cobblestone_wall).unionWith(AIHelper.fences);

	public FenceBuildTask(BlockPos forPosition, Block blockToPlace) {
		this(forPosition, new BlockItemFilter(blockToPlace));
	}

	public FenceBuildTask(BlockPos forPosition, BlockItemFilter blockItemFilter) {
		super(forPosition, blockItemFilter);
	}

	@Override
	public BlockPos[] getStandablePlaces() {
		return STANDABLE;
	}

	@Override
	protected double getBlockHeight() {
		return 1.5;
	}

	@Override
	protected double getMinHeightToBuild() {
		return super.getMinHeightToBuild();
	}

	@Override
	public BuildTask withPositionAndRotation(BlockPos add, int rotateSteps,
			MirrorDirection mirror) {
		return new FenceBuildTask(add, blockFilter);
	}
}
