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
import net.famzangl.minecraft.minebot.ai.BlockWhitelist;
import net.famzangl.minecraft.minebot.build.block.WoodItemFilter;
import net.famzangl.minecraft.minebot.build.block.WoodType;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

/**
 * Simply place wood planks.
 * 
 * @author michael
 *
 */
public class WoodBuildTask extends CubeBuildTask {

	public static final BlockWhitelist BLOCKS = new BlockWhitelist(
			Blocks.planks);

	public WoodBuildTask(BlockPos forPosition, WoodType woodType) {
		this(forPosition, new WoodItemFilter(woodType));
	}

	private WoodBuildTask(BlockPos forPosition, BlockItemFilter woodItemFilter) {
		super(forPosition, woodItemFilter);
	}

	@Override
	public BuildTask withPositionAndRotation(BlockPos add, int rotateSteps,
			MirrorDirection mirror) {
		return new WoodBuildTask(add, blockFilter);
	}

}
