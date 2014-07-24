package net.famzangl.minecraft.minebot.build.blockbuild;

import net.famzangl.minecraft.minebot.Pos;

public class TaskDescription {
	private final String commandArgs;
	private final Pos[] buildableFrom;

	public TaskDescription(String commandArgs, Pos[] buildableFrom) {
		this.commandArgs = commandArgs;
		this.buildableFrom = buildableFrom;
	}

	public String getCommandArgs() {
		return commandArgs;
	}
}
