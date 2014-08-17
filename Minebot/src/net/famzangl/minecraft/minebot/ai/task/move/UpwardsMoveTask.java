package net.famzangl.minecraft.minebot.ai.task.move;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.ItemFilter;
import net.famzangl.minecraft.minebot.ai.strategy.TaskOperations;
import net.famzangl.minecraft.minebot.ai.task.error.PositionTaskError;
import net.famzangl.minecraft.minebot.ai.task.place.JumpingPlaceBlockAtFloorTask;

public class UpwardsMoveTask extends JumpingPlaceBlockAtFloorTask {
	public UpwardsMoveTask(int x, int y, int z, ItemFilter filter) {
		super(x, y, z, filter);
	}

	@Override
	public void runTick(AIHelper h, TaskOperations o) {
		if (!h.isAirBlock(x, y + 1, z)) {
			if (!h.isStandingOn(x, y - 1, z)) {
				o.desync(new PositionTaskError(x, y - 1, z));
			}
			h.faceAndDestroy(x, y + 1, z);
		} else {
			super.runTick(h, o);
		}
	}

	@Override
	public String toString() {
		return "UpwardsMoveTask [x=" + x + ", y=" + y + ", z=" + z + "]";
	}
}
