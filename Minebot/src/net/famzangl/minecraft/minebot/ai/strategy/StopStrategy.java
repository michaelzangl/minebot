package net.famzangl.minecraft.minebot.ai.strategy;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.AIStrategy;
import net.famzangl.minecraft.minebot.ai.task.AITask;

public final class StopStrategy implements AIStrategy {
	@Override
	public void searchTasks(AIHelper helper) {
	}

	@Override
	public AITask getOverrideTask(AIHelper helper) {
		return null;
	}

	@Override
	public String getDescription() {
		return "Stopping";
	}
}