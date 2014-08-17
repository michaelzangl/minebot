package net.famzangl.minecraft.minebot.ai.task.error;

public class TaskError {
	private final String message;

	protected TaskError(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

}
