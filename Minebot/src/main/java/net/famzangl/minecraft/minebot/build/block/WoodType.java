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

import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LogBlock;
import net.minecraft.util.Direction;

import java.util.stream.Stream;

/**
 * A type of wood used in the game.
 * 
 * @see LogItemFilter
 * @see WoodItemFilter
 * @author michael
 *
 */
public enum WoodType {
	OAK(Blocks.OAK_LOG, 0, net.minecraft.block.WoodType.OAK, Blocks.OAK_SAPLING),
	SPRUCE(Blocks.SPRUCE_LOG, 1, net.minecraft.block.WoodType.SPRUCE, Blocks.SPRUCE_SAPLING),
	BIRCH(Blocks.BIRCH_LOG, 2, net.minecraft.block.WoodType.BIRCH, Blocks.BIRCH_SAPLING),
	JUNGLE(Blocks.JUNGLE_LOG, 3, net.minecraft.block.WoodType.JUNGLE, Blocks.JUNGLE_SAPLING),
	ACACIA(Blocks.ACACIA_LOG, 0, net.minecraft.block.WoodType.ACACIA, Blocks.ACACIA_SAPLING),
	DARK_OAK(Blocks.DARK_OAK_LOG, 1, net.minecraft.block.WoodType.DARK_OAK, Blocks.DARK_OAK_SAPLING);


	// TODO: Replace with block.get(LogBlock.AXIS) / Direction.Axis
	public enum LogDirection {
		X(Direction.Axis.X),
		Y(Direction.Axis.Y),
		Z(Direction.Axis.Z);

		public final Direction.Axis axis;

		private LogDirection(Direction.Axis axis) {
			this.axis = axis;
		}

		public static LogDirection forData(BlockState block) {
			Direction.Axis axis = block.get(LogBlock.AXIS);
			if (axis == null) {
				throw new IllegalArgumentException("Not a log: " + block);
			}
			return forDirection(axis);
		}

		private static LogDirection forDirection(Direction.Axis axis) {
			return Stream.of(values()).filter(it -> it.axis == axis).findAny().get();
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
	private final Block sapling;
	private final BlockSet logBlocks;


	private WoodType(Block block, int lowerBits, net.minecraft.block.WoodType plankType, Block sapling) {
		this.block = block;
		this.lowerBits = lowerBits;
		this.plankType = plankType;
		this.sapling = sapling;

		logBlocks = BlockSet.builder().add(block).build();
	}

	private boolean matches(BlockState block2) {
		return getLogBlocks().contains(block2);
	}

	/**
	 * Returns a block set with all blocks of this type.
	 * 
	 * @return
	 */
	public BlockSet getLogBlocks() {
		return logBlocks;
	}

	public static WoodType getFor(BlockState block2) {
		for (WoodType t : values()) {
			if (t.matches(block2)) {
				return t;
			}
		}

		throw new IllegalArgumentException("Cannot convert to log: " + block2);
	}

	public BlockState getBlockState(LogDirection newDir) {
		return block.getDefaultState().with(LogBlock.AXIS, newDir.axis);
	}

	public Block getSapling() {
		return sapling;
	}
}