package net.famzangl.minecraft.minebot.ai.enchanting;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.minecraft.util.MovingObjectPosition;

public class KillAnyMobTask implements AITask {

	int tickCount;

	@Override
	public boolean isFinished(AIHelper h) {
		MovingObjectPosition objectMouseOver = h.getObjectMouseOver();
		return objectMouseOver == null || objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY;
	}

	@Override
	public void runTick(AIHelper h) {
		tickCount++;
		if (tickCount % 10 == 5) {
			h.overrideAttack();
		}
	}

}
