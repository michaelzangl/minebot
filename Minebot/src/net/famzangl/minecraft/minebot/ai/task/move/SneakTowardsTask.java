package net.famzangl.minecraft.minebot.ai.task.move;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.strategy.TaskOperations;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.minecraft.util.EnumFacing;

/**
 * Sneak standing on (x, y - 1, z) towards the direction.
 * 
 * @author michael
 * 
 */
public class SneakTowardsTask extends AITask {
	private final int x;
	private final int y;
	private final int z;
	private final EnumFacing dir;

	public SneakTowardsTask(int x, int y, int z, EnumFacing dir) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.dir = dir;
	}

	@Override
	public boolean isFinished(AIHelper h) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void runTick(AIHelper h, TaskOperations o) {
		// TODO Auto-generated method stub

	}

}
