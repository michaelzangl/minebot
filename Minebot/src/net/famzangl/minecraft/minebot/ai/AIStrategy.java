package net.famzangl.minecraft.minebot.ai;

import net.famzangl.minecraft.minebot.ai.task.AITask;

public interface AIStrategy {

	void searchTasks(AIHelper helper);

	String getDescription();

	/**
	 * get an emergency override task.
	 * @param aiController
	 */
	AITask getOverrideTask(AIHelper helper);
	
}
