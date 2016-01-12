package net.famzangl.minecraft.minebot.build.reverse.factories;

import java.util.ArrayList;

import net.famzangl.minecraft.minebot.ai.command.BlockWithDataOrDontcare;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSets;
import net.famzangl.minecraft.minebot.ai.path.world.WorldData;
import net.famzangl.minecraft.minebot.build.blockbuild.BuildTask;
import net.famzangl.minecraft.minebot.build.reverse.TaskDescription;
import net.famzangl.minecraft.minebot.build.reverse.UnsupportedBlockException;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

public class BuildTaskFactories {

	public static final BlockSet IGNORED_ON_RECONSTRUCT = BlockSets.AIR
			.unionWith(new BlockSet(Blocks.barrier));

	private static final ArrayList<BuildTaskFactory> factories = new ArrayList<BuildTaskFactory>();

	static {
		register(new BlockBuildTaskFactory());
		register(new LogBuildTaskFactory());
		register(new SlabBuildTaskFactory());
	}
	
	public static void register(BuildTaskFactory factory) {
		factories.add(factory);
	}
	
	public static BuildTask getTask(BlockPos position, BlockWithDataOrDontcare block) {
		for (BuildTaskFactory f : factories) {
			BuildTask res = f.getTask(position, block);
			if (res != null) {
				return res;
			}
		}

		throw new IllegalArgumentException("Cannot handle: " + block);
	}

	public static TaskDescription getTaskFor(WorldData world, BlockPos position)
			throws UnsupportedBlockException {
		if (IGNORED_ON_RECONSTRUCT.isAt(world, position)) {
			return null;
		}

		for (BuildTaskFactory f : factories) {
			TaskDescription res = f.getTaskDescription(world, position);
			if (res != null) {
				return res;
			}
		}

		throw new UnsupportedBlockException(world, position,
				"No handler found for that block.");
	}

}
