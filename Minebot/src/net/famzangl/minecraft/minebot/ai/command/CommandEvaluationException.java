package net.famzangl.minecraft.minebot.ai.command;

public class CommandEvaluationException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6305591561818630563L;

	public CommandEvaluationException(String message, Throwable cause) {
		super(message, cause);
	}

	public CommandEvaluationException(String message) {
		super(message);
	}

}
