package net.famzangl.minecraft.minebot.ai.path;

import net.famzangl.minecraft.minebot.ai.task.AITask;

public interface TaskReceiver {

	/**
	 * Adds a task that should be executed.
	 * 
	 * @param task
	 *            The new task
	 */
	public abstract void addTask(AITask task);
}
