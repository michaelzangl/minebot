package net.famzangl.minecraft.minebot.ai.task.place;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.famzangl.minecraft.minebot.ai.task.TaskOperations;
import net.minecraft.util.BlockPos;

/**
 * Simply destroy a block at a given position, assuming it is in reach.
 * 
 * @author michael
 *
 */
public class DestroyBlockTask extends AITask {
	private BlockPos pos;

	public DestroyBlockTask(BlockPos pos) {
		this.pos = pos;
	}

	@Override
	public boolean isFinished(AIHelper h) {
		return h.isAirBlock(pos);
	}

	@Override
	public void runTick(AIHelper h, TaskOperations o) {
		h.faceAndDestroy(pos);
	}

}
