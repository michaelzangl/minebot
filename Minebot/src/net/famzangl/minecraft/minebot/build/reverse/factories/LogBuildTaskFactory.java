package net.famzangl.minecraft.minebot.build.reverse.factories;

import net.famzangl.minecraft.minebot.ai.command.BlockWithDataOrDontcare;
import net.famzangl.minecraft.minebot.ai.path.world.BlockMetaSet;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSets;
import net.famzangl.minecraft.minebot.build.block.WoodType;
import net.famzangl.minecraft.minebot.build.block.WoodType.LogDirection;
import net.famzangl.minecraft.minebot.build.blockbuild.BuildTask;
import net.famzangl.minecraft.minebot.build.blockbuild.LogBuildTask;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockNewLog;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

public class LogBuildTaskFactory extends AbstractBuildTaskFactory {

	@Override
	protected BuildTask getTaskImpl(BlockPos position,
			BlockWithDataOrDontcare block) {
		return new LogBuildTask(position, block);
	}

	@Override
	public BlockSet getSupportedBlocks() {
		return LogBuildTask.NORMAL_LOGS;
	}

}
