package net.famzangl.minecraft.minebot.build.reverse.factories;

import net.famzangl.minecraft.minebot.ai.command.BlockWithData;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.famzangl.minecraft.minebot.build.blockbuild.BlockBuildTask;
import net.famzangl.minecraft.minebot.build.blockbuild.BuildTask;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;

public class BlockBuildTaskFactory extends AbstractBuildTaskFactory {
	
	@Override
	public BlockSet getSupportedBlocks() {
		return BlockBuildTask.BLOCKS;
	}
	
	@Override
	public BuildTask getTaskImpl(BlockPos position, IBlockState forBlock) {
		return new BlockBuildTask(position, new BlockWithData(forBlock));
	}
}
