package net.famzangl.minecraft.minebot.build.blockbuild;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.BlockItemFilter;
import net.famzangl.minecraft.minebot.ai.BlockWhitelist;
import net.famzangl.minecraft.minebot.build.WoodItemFilter;
import net.famzangl.minecraft.minebot.build.WoodType;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

public class WoodBuildTask extends CubeBuildTask {

	public static final BlockWhitelist BLOCKS = new BlockWhitelist(Blocks.planks);

	public WoodBuildTask(BlockPos forPosition, WoodType woodType) {
		this(forPosition, new WoodItemFilter(woodType));
	}

	private WoodBuildTask(BlockPos forPosition, BlockItemFilter woodItemFilter) {
		super(forPosition, woodItemFilter);
	}

	@Override
	public BuildTask withPositionAndRotation(BlockPos add, int rotateSteps,
			MirrorDirection mirror) {
		return new WoodBuildTask(add, blockFilter);
	}

}
