package net.famzangl.minecraft.minebot.build.blockbuild;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.BlockItemFilter;
import net.famzangl.minecraft.minebot.ai.BlockWhitelist;
import net.famzangl.minecraft.minebot.ai.ColoredBlockItemFilter;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

public class ColoredCubeBuildTask extends CubeBuildTask {

	public static final BlockWhitelist BLOCKS = new BlockWhitelist( Blocks.wool,
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
