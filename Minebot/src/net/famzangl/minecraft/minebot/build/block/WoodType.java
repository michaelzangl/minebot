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

import net.famzangl.minecraft.minebot.ai.command.BlockWithData;
import net.famzangl.minecraft.minebot.ai.command.BlockWithDataOrDontcare;
import net.famzangl.minecraft.minebot.ai.path.world.BlockMetaSet;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockLog.EnumAxis;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.init.Blocks;

/**
 * A type of wood used in the game.
 * 
 * @see LogItemFilter
 * @see WoodItemFilter
 * @author michael
 *
 */
public enum WoodType {
	OAK(Blocks.log, 0, EnumType.OAK), SPRUCE(Blocks.log, 1, EnumType.SPRUCE), BIRCH(
			Blocks.log, 2, EnumType.BIRCH), JUNGLE(Blocks.log, 3,
			EnumType.JUNGLE), ACACIA(Blocks.log2, 0, EnumType.ACACIA), DARK_OAK(
			Blocks.log2, 1, EnumType.DARK_OAK);

	public enum LogDirection {
		X(BlockLog.EnumAxis.X, 1 << 2), Y(BlockLog.EnumAxis.Y, 0), Z(
				BlockLog.EnumAxis.Z, 2 << 2);

		public final int higherBits;
		public final EnumAxis axis;
		public final BlockSet blocks;

		private LogDirection(EnumAxis axis, int higherBits) {
			this.axis = axis;
			this.higherBits = higherBits;
			BlockMetaSet set = new BlockMetaSet();
			for (WoodType w : WoodType.values()) {
				set = set.unionWith(w.block, w.lowerBits + higherBits);
			}
			blocks = set;
		}

		public static LogDirection forData(BlockWithDataOrDontcare block) {
			for (LogDirection d : LogDirection.values()) {
				if (block.containedIn(d.blocks)) {
					return d;
				}
			}
			throw new IllegalArgumentException("Illegal Log Block: " + block);
		}

		public LogDirection rotateY() {
			switch (this) {
			case X:
				return Z;
			case Z:
				return X;
			default:
				return this;
			}
		}
	}

	public final Block block;
	public final int lowerBits;
	public final EnumType plankType;

	private WoodType(Block block, int lowerBits, EnumType plankType) {
		this.block = block;
		this.lowerBits = lowerBits;
		this.plankType = plankType;
	}

	private boolean matches(BlockWithDataOrDontcare block2) {
		return block2.containedIn(getLogBlocks());
	}

	/**
	 * Returns a block set with all blocks of this type.
	 * 
	 * @return
	 */
	public BlockSet getLogBlocks() {
		BlockMetaSet set = new BlockMetaSet();
		for (LogDirection a : LogDirection.values()) {
			set = set.unionWith(block, a.higherBits + lowerBits);
		}
		return set;
	}

	public static WoodType getFor(BlockWithDataOrDontcare block2) {
		for (WoodType t : values()) {
			if (t.matches(block2)) {
				return t;
			}
		}

		throw new IllegalArgumentException("Cannot convert to log: " + block2);
	}

	public BlockWithData getBlockWithMeta(LogDirection direction) {
		return new BlockWithData(block, lowerBits | direction.higherBits);
	}
}