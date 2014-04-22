package net.famzangl.minecraft.minebot.ai.task;

import net.famzangl.minecraft.minebot.ai.AIHelper;

public interface AITask {

	boolean isFinished(AIHelper h);

	void runTick(AIHelper h);

}
