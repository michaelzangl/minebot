package net.famzangl.minecraft.minebot.ai.task.move;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.strategy.TaskOperations;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.famzangl.minecraft.minebot.ai.task.error.PositionTaskError;
import net.minecraft.init.Blocks;

public class DownwardsMoveTask extends AITask {
	private final int x;
	private final int y;
	private final int z;
	private boolean obsidianMining;

	public DownwardsMoveTask(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public boolean isFinished(AIHelper h) {
		return h.isStandingOn(x, y, z);
	}

	@Override
	public void runTick(AIHelper h, TaskOperations o) {
		if (!h.isAirBlock(x, y + 1, z) && !h.isSideTorch(x, y + 1, z)) {
			// grass, ...
			h.faceAndDestroy(x, y + 1, z);
		} else if (!h.isAirBlock(x, y, z)) {
			if (!h.isStandingOn(x, y + 1, z)) {
				System.out.println("Not standing on the right block.");
				o.desync(new PositionTaskError(x, y + 1, z));
			}
			if (AIHelper.blockIsOneOf(h.getBlock(x, y, z), Blocks.obsidian)) {
				obsidianMining = true;
			}

			h.faceAndDestroy(x, y, z);
		}
	}

	@Override
	public int getGameTickTimeout() {
		return super.getGameTickTimeout()
				+ (obsidianMining ? HorizontalMoveTask.OBSIDIAN_TIME : 0);
	}

	@Override
	public String toString() {
		return "DownwardsMoveTask [x=" + x + ", y=" + y + ", z=" + z + "]";
	}
}