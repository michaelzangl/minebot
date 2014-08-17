package net.famzangl.minecraft.minebot.build.blockbuild;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.BlockItemFilter;
import net.famzangl.minecraft.minebot.ai.ColoredBlockItemFilter;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

public class ColoredCubeBuildTask extends CubeBuildTask {

	public static final Block[] BLOCKS = new Block[] { Blocks.wool,
			Blocks.stained_glass, Blocks.stained_hardened_clay };

	public ColoredCubeBuildTask(Pos forPosition, Block blockType, int extraColor) {
		this(forPosition, new ColoredBlockItemFilter(blockType, extraColor));
	}

	private ColoredCubeBuildTask(Pos forPosition,
			BlockItemFilter coloredBlockItemFilter) {
		super(forPosition, coloredBlockItemFilter);
	}

	@Override
	public String toString() {
		return "ColoredCubeBuildTask [blockFilter=" + blockFilter
				+ ", forPosition=" + forPosition + "]";
	}

	@Override
	public BuildTask withPositionAndRotation(Pos add, int rotateSteps,
			MirrorDirection mirror) {
		return new ColoredCubeBuildTask(add, blockFilter);
	}
}
