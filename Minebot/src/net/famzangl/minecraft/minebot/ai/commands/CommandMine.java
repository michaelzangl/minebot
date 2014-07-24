package net.famzangl.minecraft.minebot.ai.commands;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.AIStrategy;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.ai.command.AICommandInvocation;
import net.famzangl.minecraft.minebot.ai.command.AICommandParameter;
import net.famzangl.minecraft.minebot.ai.command.ParameterType;
import net.famzangl.minecraft.minebot.ai.strategy.MineStrategy;

@AICommand(helpText = "Mines for ores.\n"
		+ "Uses the minebot.properties file to find ores."
		+ "If blockName is given, only the block that is given is searched for.", name = "minebot")
public class CommandMine {

	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "mine", description = "") String nameArg) {
		return new MineStrategy().produceStrategy(helper);
	}

	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "mine", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.BLOCK_NAME, description = "The block to mine.") String blockName) {
		return new MineStrategy().produceStrategy(helper, blockName);
	}

}
