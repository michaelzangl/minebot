package net.famzangl.minecraft.minebot.ai.commands;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.animals.AnimalyType;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.ai.command.AICommandInvocation;
import net.famzangl.minecraft.minebot.ai.command.AICommandParameter;
import net.famzangl.minecraft.minebot.ai.command.ParameterType;
import net.famzangl.minecraft.minebot.ai.command.SafeStrategyRule;
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.LetAnimalsSitStrategy;
import net.minecraft.item.EnumDyeColor;

@AICommand(helpText = "Lets all dogs either sit or stand.", name = "minebot")
public class CommandSit {

	@AICommandInvocation(safeRule = SafeStrategyRule.DEFEND)
	public static AIStrategy runSit(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "sit", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.COLOR, description = "The color of wolfes to feed.", optional = true) EnumDyeColor color) {
		return new LetAnimalsSitStrategy(AnimalyType.WOLF, true, color);
	}

	@AICommandInvocation(safeRule = SafeStrategyRule.DEFEND)
	public static AIStrategy runUnSit(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "unsit", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.COLOR, description = "The color of wolfes to feed.", optional = true) EnumDyeColor color) {
		return new LetAnimalsSitStrategy(AnimalyType.WOLF, false, color);
	}
}
