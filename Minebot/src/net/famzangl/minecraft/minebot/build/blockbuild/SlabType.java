package net.famzangl.minecraft.minebot.build.blockbuild;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

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
	public final Block slabBlock;
	public final Block doubleBlock;
	public final int meta;

	private SlabType(Block slabBlock, Block doubleBlock, int meta) {
		this.slabBlock = slabBlock;
		this.doubleBlock = doubleBlock;
		this.meta = meta;
	}
}
