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
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;

/**
 * A type of wood used in the game.
 * 
 * @see LogItemFilter
 * @see WoodItemFilter
 * @author michael
 *
 */
public enum WoodType {
	OAK(Blocks.OAK_LOG, 0, net.minecraft.block.WoodType.OAK),
	SPRUCE(Blocks.SPRUCE_LOG, 1, net.minecraft.block.WoodType.SPRUCE),
	BIRCH(Blocks.BIRCH_LOG, 2, net.minecraft.block.WoodType.BIRCH),
	JUNGLE(Blocks.JUNGLE_LOG, 3, net.minecraft.block.WoodType.JUNGLE),
	ACACIA(Blocks.ACACIA_LOG, 0, net.minecraft.block.WoodType.ACACIA),
	DARK_OAK(Blocks.DARK_OAK_LOG, 1, net.minecraft.block.WoodType.DARK_OAK);

	public enum LogDirection {
		X(Direction.Axis.X, 1 << 2),
		Y(Direction.Axis.Y, 0),
		Z(Direction.Axis.Z, 2 << 2);

		public final int higherBits;
		public final Direction.Axis axis;
		public final BlockSet blocks;

		private LogDirection(Direction.Axis axis, int higherBits) {
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
	public final net.minecraft.block.WoodType plankType;

	private WoodType(Block block, int lowerBits, net.minecraft.block.WoodType plankType) {
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