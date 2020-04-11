package net.famzangl.minecraft.minebot.build.reverse.factories;

import net.famzangl.minecraft.minebot.ai.command.BlockWithData;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.famzangl.minecraft.minebot.build.blockbuild.BuildTask;
import net.famzangl.minecraft.minebot.build.blockbuild.SlabBuildTask;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class SlabBuildTaskFactory extends AbstractBuildTaskFactory {

	@Override
	protected BuildTask getTaskImpl(BlockPos position,
			BlockState block) {
		return new SlabBuildTask(position, new BlockWithData(block));
	}

	@Override
	public BlockSet getSupportedBlocks() {
		return SlabBuildTask.BLOCKS;
	}
}
