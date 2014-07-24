package net.famzangl.minecraft.minebot.ai.task.move;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Sneak standing on (x, y - 1, z) towards the direction.
 * 
 * @author michael
 * 
 */
public class SneakTowardsTask implements AITask {
	private final int x;
	private final int y;
	private final int z;
	private final ForgeDirection dir;

	public SneakTowardsTask(int x, int y, int z, ForgeDirection dir) {
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
	public void runTick(AIHelper h) {
		// TODO Auto-generated method stub

	}

}
