package net.famzangl.minecraft.minebot.build.reverse.factories;

import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSets;
import net.famzangl.minecraft.minebot.ai.path.world.WorldData;
import net.famzangl.minecraft.minebot.build.reverse.TaskDescription;
import net.famzangl.minecraft.minebot.build.reverse.UnsupportedBlockException;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

public class BuildTaskFactories {

	public static final BlockSet IGNORED_ON_RECONSTRUCT = BlockSets.AIR
			.unionWith(new BlockSet(Blocks.BARRIER));

	private static final ArrayList<BuildTaskFactory> factories = new ArrayList<BuildTaskFactory>();

	static {
		register(new BlockBuildTaskFactory());
		register(new LogBuildTaskFactory());
		register(new SlabBuildTaskFactory());
		register(new StairBuildTaskFactory());
	}
	
	public static void register(BuildTaskFactory factory) {
		factories.add(factory);
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
