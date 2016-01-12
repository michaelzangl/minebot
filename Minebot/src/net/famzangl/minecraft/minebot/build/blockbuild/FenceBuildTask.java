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

import net.famzangl.minecraft.minebot.ai.BlockItemFilter;
import net.famzangl.minecraft.minebot.ai.command.BlockWithDataOrDontcare;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSets;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

public class FenceBuildTask extends AbstractBuildTask {

	public static final BlockPos[] STANDABLE = new BlockPos[] { new BlockPos(-1, 1, 0),
			new BlockPos(0, 1, -1), new BlockPos(1, 1, 0), new BlockPos(0, 1, 1), };

	public static final BlockSet BLOCKS = new BlockSet(
			Blocks.cobblestone_wall).unionWith(BlockSets.FENCE);

	private final BlockWithDataOrDontcare fenceBlock;

	public FenceBuildTask(BlockPos forPosition, BlockWithDataOrDontcare fenceBlock) {
		super(forPosition);
		if (!BLOCKS.contains(fenceBlock)) {
			throw new IllegalArgumentException();
		}
		this.fenceBlock = fenceBlock;
	}
	@Override
	protected BlockItemFilter getItemToPlaceFilter() {
		return new BlockItemFilter(fenceBlock.toBlockSet());
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
	public BuildTask withPositionAndRotation(BlockPos add, int rotateSteps,
			MirrorDirection mirror) {
		return new FenceBuildTask(add, fenceBlock);
	}
}
