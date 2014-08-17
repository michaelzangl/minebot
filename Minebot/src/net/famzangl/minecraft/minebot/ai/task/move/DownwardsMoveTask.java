package net.famzangl.minecraft.minebot.ai.task.move;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.strategy.TaskOperations;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.famzangl.minecraft.minebot.ai.task.error.PositionTaskError;

public class DownwardsMoveTask extends AITask {
	private final int x;
	private final int y;
	private final int z;

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
		if (!h.isAirBlock(x, y + 1, z)) {
			// grass, ...
			h.faceAndDestroy(x, y + 1, z);
		} else if (!h.isAirBlock(x, y, z)) {
			if (!h.isStandingOn(x, y + 1, z)) {
				System.out.println("Not standing on the right block.");
				o.desync(new PositionTaskError(x, y + 1, z));
			}
			h.faceAndDestroy(x, y, z);
		}
	}

	@Override
	public String toString() {
		return "DownwardsMoveTask [x=" + x + ", y=" + y + ", z=" + z + "]";
	}
}