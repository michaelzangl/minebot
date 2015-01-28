package net.famzangl.minecraft.minebot.ai.task.error;

import net.famzangl.minecraft.minebot.ai.strategy.TaskStrategy;
import net.famzangl.minecraft.minebot.ai.task.TaskOperations;

/**
 * This is an error that occurred while doing a task of a {@link TaskStrategy}.
 * You can add as many errors as you like with the
 * {@link TaskOperations#desync(TaskError)} method. Multiple errors of the same
 * type are automatically filtered.
 * 
 * @author michael
 *
 */
public class TaskError {
	private final String message;

	protected TaskError(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

}
