package net.famzangl.minecraft.minebot.ai.strategy;

import net.famzangl.minecraft.minebot.ai.task.error.TaskError;

public interface TaskOperations {

	/**
	 * This should be called whenever the current task could not achieve it's
	 * goal. All following tasks are unscheduled.
	 * 
	 * @param taskError
	 */
	public abstract void desync(TaskError taskError);

	/**
	 * This can be called by the current task to do a look ahead and already let
	 * the next task to it's face and destroy. Use with care.
	 * 
	 * @return
	 */
	public boolean faceAndDestroyForNextTask();
}
