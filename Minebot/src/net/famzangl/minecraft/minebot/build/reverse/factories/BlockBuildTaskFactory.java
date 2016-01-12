package net.famzangl.minecraft.minebot.build.reverse.factories;

import net.famzangl.minecraft.minebot.ai.command.BlockWithDataOrDontcare;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.famzangl.minecraft.minebot.build.blockbuild.BlockBuildTask;
import net.famzangl.minecraft.minebot.build.blockbuild.BuildTask;
import net.minecraft.util.BlockPos;

public class BlockBuildTaskFactory extends AbstractBuildTaskFactory {
	
	@Override
	public BlockSet getSupportedBlocks() {
		return BlockBuildTask.BLOCKS;
	}
	
	@Override
	public BuildTask getTaskImpl(BlockPos position, BlockWithDataOrDontcare forBlock) {
		return new BlockBuildTask(position, forBlock);
	}
}
