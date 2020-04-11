package net.famzangl.minecraft.minebot.build.reverse.factories;

import org.apache.commons.lang3.StringUtils;

import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.famzangl.minecraft.minebot.ai.path.world.WorldData;
import net.famzangl.minecraft.minebot.build.blockbuild.BuildTask;
import net.famzangl.minecraft.minebot.build.reverse.TaskDescription;
import net.famzangl.minecraft.minebot.build.reverse.UnsupportedBlockException;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;

public abstract class AbstractBuildTaskFactory implements BuildTaskFactory {

	@Override
	public TaskDescription getTaskDescription(WorldData world, BlockPos position)
			throws UnsupportedBlockException {
		if (getSupportedBlocks().isAt(world, position)) {
			IBlockState block = world.getBlockState(position);
			BuildTask task = getTaskImpl(position, block);
			try {
				Object[] args = task.getCommandArguments();
				return new TaskDescription(StringUtils.join(args, " "),
						task.getStandablePlaces());
			} catch (UnsupportedOperationException uoe) {
				throw new UnsupportedBlockException(world, position,
						"Task could not be converted: " + task);
			}
		}
		return null;
	}

	protected abstract BuildTask getTaskImpl(BlockPos position, IBlockState block);

	public abstract BlockSet getSupportedBlocks();

}
