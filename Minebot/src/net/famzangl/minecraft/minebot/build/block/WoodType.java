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
package net.famzangl.minecraft.minebot.build.block;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

/**
 * A type of wood used in the game.
 * @see LogItemFilter
 * @see WoodItemFilter
 * @author michael
 *
 */
public enum WoodType {
	OAK(Blocks.log, 0),
	SPRUCE(Blocks.log, 1),
	BIRCH(Blocks.log, 2),
	JUNGLE(Blocks.log, 3),
	ACACIA(Blocks.log2, 0),
	DARK_OAK(Blocks.log2, 1);

	public final Block block;
	public final int lowerBits;

	private WoodType(Block block, int lowerBits) {
		this.block = block;
		this.lowerBits = lowerBits;

	}
}