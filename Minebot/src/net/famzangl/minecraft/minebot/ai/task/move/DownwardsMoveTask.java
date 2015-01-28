package net.famzangl.minecraft.minebot.ai.task.move;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.BlockWhitelist;
import net.famzangl.minecraft.minebot.ai.strategy.TaskOperations;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.famzangl.minecraft.minebot.ai.task.error.PositionTaskError;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

public class DownwardsMoveTask extends AITask {

	private static final BlockWhitelist hardBlocks = new BlockWhitelist(
			Blocks.obsidian);
	
	private boolean obsidianMining;
	private BlockPos pos;

	public DownwardsMoveTask(BlockPos pos) {
		this.pos = pos;
	}

	@Override
	public boolean isFinished(AIHelper h) {
		return h.isStandingOn(pos);
	}

	@Override
	public void runTick(AIHelper h, TaskOperations o) {
		if (!h.isAirBlock(pos.add(0,1,0)) && !h.isSideTorch(pos.add(0,1,0))) {
			// grass, ...
			h.faceAndDestroy(pos.add(0,1,0));
		} else if (!h.isAirBlock(pos)) {
			if (!h.isStandingOn(pos.add(0,1,0))) {
				System.out.println("Not standing on the right block.");
				o.desync(new PositionTaskError(pos.add(0,1,0)));
			}
			if (hardBlocks.contains(h.getBlock(pos))) {
				obsidianMining = true;
			}

			h.faceAndDestroy(pos);
		}
	}

	@Override
	public int getGameTickTimeout() {
		return super.getGameTickTimeout()
				+ (obsidianMining ? HorizontalMoveTask.OBSIDIAN_TIME : 0);
	}

	@Override
	public String toString() {
		return "DownwardsMoveTask [obsidianMining=" + obsidianMining + ", pos="
				+ pos + "]";
	}
}