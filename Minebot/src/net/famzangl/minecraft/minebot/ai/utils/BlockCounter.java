package net.famzangl.minecraft.minebot.ai.utils;

import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.famzangl.minecraft.minebot.ai.path.world.WorldData;
import net.famzangl.minecraft.minebot.ai.utils.BlockArea.AreaVisitor;

public class BlockCounter implements AreaVisitor {

	private BlockSet[] blockSets;
	private int[] count;

	public BlockCounter(BlockSet... blockSets) {
		this.blockSets = blockSets;
		this.count = new int[blockSets.length];
	}

	public static int[] countBlocks(WorldData world, BlockArea area, BlockSet... blockSets) {
		BlockCounter blockCounter = new BlockCounter(blockSets);
		area.accept(blockCounter, world);
		return blockCounter.getCount();
	}

	@Override
	public void visit(WorldData world, int x, int y, int z) {
		for (int i = 0; i < blockSets.length; i++) {
			if (blockSets[i].isAt(world, x, y, z)) {
				count[i]++;
			}
		}
	}
	
	public int[] getCount() {
		return count;
	}
}
