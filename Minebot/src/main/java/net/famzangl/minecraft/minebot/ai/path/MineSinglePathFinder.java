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
package net.famzangl.minecraft.minebot.ai.path;

import net.famzangl.minecraft.minebot.ai.command.BlockWithData;
import net.famzangl.minecraft.minebot.ai.path.world.BlockFloatMap;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.minecraft.util.EnumFacing;

public class MineSinglePathFinder extends MinePathfinder {

	private final BlockSet blocks;

	public MineSinglePathFinder(BlockSet blocks, EnumFacing preferedDirection,
			int preferedLayer) {
		super(preferedDirection, preferedLayer);
		this.blocks = blocks;
	}

	@Override
	protected BlockFloatMap getFactorProvider() {
		BlockFloatMap map = new BlockFloatMap();
		for (BlockWithData block : blocks) {
			map.set(block, 1);
		}
		map.setDefault(0);
		return map;
	}

	@Override
	protected BlockFloatMap getPointsProvider() {
		BlockFloatMap map = new BlockFloatMap();
		map.setDefault(0);
		return map;
	}

}
