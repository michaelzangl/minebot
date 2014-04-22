package net.famzangl.minecraft.minebot.build.blockbuild;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.BlockItemFilter;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

public class BlockBuildTask extends CubeBuildTask {

	public static final Block[] BLOCKS = new Block[] { Blocks.dirt,
			Blocks.stone, Blocks.cobblestone, Blocks.beacon, Blocks.bookshelf,
			Blocks.brick_block, Blocks.cake, Blocks.coal_block,
			Blocks.crafting_table, Blocks.diamond_block, Blocks.emerald_block,
			Blocks.end_stone, Blocks.glass, Blocks.gold_block, Blocks.grass, Blocks.gravel,
			Blocks.hay_block, Blocks.iron_block, Blocks.iron_ore,
			Blocks.lapis_block, Blocks.lapis_ore, Blocks.melon_block,
			Blocks.mossy_cobblestone, Blocks.nether_brick, Blocks.netherrack,
			Blocks.obsidian, Blocks.pumpkin, Blocks.quartz_block,
			Blocks.quartz_ore, Blocks.red_mushroom_block,
			Blocks.redstone_block, Blocks.redstone_lamp, Blocks.redstone_ore,
			Blocks.sand, Blocks.stonebrick, Blocks.tnt };

	public BlockBuildTask(Pos forPosition, Block blockToPlace) {
		super(forPosition, new BlockItemFilter(blockToPlace));
	}

	@Override
	public String toString() {
		return "BlockBuildTask [blockFilter=" + blockFilter + ", forPosition="
				+ forPosition + "]";
	}
}
