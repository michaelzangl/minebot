package net.famzangl.minecraft.minebot.build.reverse.factories;

import net.famzangl.minecraft.minebot.ai.command.BlockWithDataOrDontcare;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.famzangl.minecraft.minebot.build.blockbuild.BuildTask;
import net.famzangl.minecraft.minebot.build.blockbuild.SlabBuildTask;
import net.minecraft.util.BlockPos;

public class SlabBuildTaskFactory extends AbstractBuildTaskFactory {

	@Override
	protected BuildTask getTaskImpl(BlockPos position,
			BlockWithDataOrDontcare block) {
		return new SlabBuildTask(position, block);
	}

	@Override
	public BlockSet getSupportedBlocks() {
		return SlabBuildTask.BLOCKS;
	}
}
