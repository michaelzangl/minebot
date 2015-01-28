package net.famzangl.minecraft.minebot.ai.task.move;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.strategy.TaskOperations;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.famzangl.minecraft.minebot.ai.task.error.PositionTaskError;

/**
 * Simply walks towards (x, z) assuming there is nothing in the way.
 * @author michael
 *
 */
public class WalkTowardsTask extends AITask {

	private final int x;
	private final int z;
	private Pos fromPos;

	public WalkTowardsTask(int x, int z, Pos fromPos) {
		this.x = x;
		this.z = z;
		this.fromPos = fromPos;
	}

	@Override
	public boolean isFinished(AIHelper h) {
		return h.arrivedAt(x + 0.5, z + 0.5);
	}

	@Override
	public void runTick(AIHelper h, TaskOperations o) {
		if (fromPos != null) {
			if (!h.isStandingOn(fromPos.getX(), fromPos.getY(), fromPos.getZ())) {
				o.desync(new PositionTaskError(fromPos.getX(), fromPos.getY(), fromPos.getZ()));
			}
			fromPos = null;
		}
		final boolean nextIsFacing = o.faceAndDestroyForNextTask();
		h.walkTowards(x + 0.5, z + 0.5, false, !nextIsFacing);
	}

	@Override
	public String toString() {
		return "WalkTowardsTask [x=" + x + ", z=" + z + ", fromPos=" + fromPos
				+ "]";
	}

}
