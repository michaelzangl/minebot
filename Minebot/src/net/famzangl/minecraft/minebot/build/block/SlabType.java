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
import net.minecraft.init.Blocks;

/**
 * A half slab type.
 * @author michael
 *
 */
public enum SlabType {
	STONE(Blocks.stone_slab, Blocks.double_stone_slab, 0),
	SANDSTONE(Blocks.stone_slab, Blocks.double_stone_slab, 1),
	WOODEN(Blocks.stone_slab, Blocks.double_stone_slab, 2),
	COBBLESTONE(Blocks.stone_slab, Blocks.double_stone_slab, 3),
	BRICK(Blocks.stone_slab, Blocks.double_stone_slab, 4),
	STONE_BRICK(Blocks.stone_slab, Blocks.double_stone_slab, 5),
	NETHER_BRICK(Blocks.stone_slab, Blocks.double_stone_slab, 6),
	QUARTZ(Blocks.stone_slab, Blocks.double_stone_slab, 7),

	OAK(Blocks.wooden_slab, Blocks.double_wooden_slab, 0),
	SPRUCE(Blocks.wooden_slab, Blocks.double_wooden_slab, 1),
	BIRCH(Blocks.wooden_slab, Blocks.double_wooden_slab, 2),
	JUNGLE(Blocks.wooden_slab, Blocks.double_wooden_slab, 3),
	ACACIA(Blocks.wooden_slab, Blocks.double_wooden_slab, 4),
	DARK_OAK(Blocks.wooden_slab, Blocks.double_wooden_slab, 5);
	
	public static final BlockSet BLOCKS;
	static {
		BlockSet blocks = new BlockMetaSet();
		for (SlabType type : values()) {
			blocks = blocks.unionWith(type.slabBlocks);
		}
		BLOCKS = blocks ;
	}
	public final Block slabBlock;
	public final Block doubleBlock;
	public final int meta;
	
	public final BlockSet slabBlocks;

	private SlabType(Block slabBlock, Block doubleBlock, int meta) {
		this.slabBlock = slabBlock;
		this.doubleBlock = doubleBlock;
		this.meta = meta;
		slabBlocks = getBlock().toBlockSet().unionWith(getBlockForUpper().toBlockSet());
	}

	public BlockWithData getBlock() {
		return new BlockWithData(slabBlock, meta);
	}
	
	public BlockWithData getBlockForUpper() {
		return new BlockWithData(slabBlock, meta | 0x8);
	}
	
	public static SlabType getForSlabBlock(BlockWithDataOrDontcare block) {
		for (SlabType t : values()) {
			if (block.containedIn(t.slabBlocks)) {
				return t;
			}
		}
		throw new IllegalArgumentException("No known slab: " + block);
	}
}
