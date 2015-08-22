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
import net.famzangl.minecraft.minebot.ai.ColoredBlockItemFilter;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

public class ColoredCubeBuildTask extends CubeBuildTask {

	public static final BlockSet BLOCKS = new BlockSet( Blocks.wool,
			Blocks.stained_glass, Blocks.stained_hardened_clay);

	public ColoredCubeBuildTask(BlockPos forPosition, Block blockType, int extraColor) {
		this(forPosition, new ColoredBlockItemFilter(blockType, extraColor));
	}

	private ColoredCubeBuildTask(BlockPos forPosition,
			BlockItemFilter coloredBlockItemFilter) {
		super(forPosition, coloredBlockItemFilter);
	}

	@Override
	public String toString() {
		return "ColoredCubeBuildTask [blockFilter=" + blockFilter
				+ ", forPosition=" + forPosition + "]";
	}

	@Override
	public BuildTask withPositionAndRotation(BlockPos add, int rotateSteps,
			MirrorDirection mirror) {
		return new ColoredCubeBuildTask(add, blockFilter);
	}
}
