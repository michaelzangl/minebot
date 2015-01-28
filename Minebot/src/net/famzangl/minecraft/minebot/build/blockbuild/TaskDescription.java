package net.famzangl.minecraft.minebot.build.blockbuild;

import net.famzangl.minecraft.minebot.Pos;
import net.minecraft.util.BlockPos;

public class TaskDescription {
	private final String commandArgs;
	private final BlockPos[] buildableFrom;

	public TaskDescription(String commandArgs, BlockPos[] buildableFrom) {
		this.commandArgs = commandArgs;
		this.buildableFrom = buildableFrom;
	}

	public String getCommandArgs() {
		return commandArgs;
	}
}
