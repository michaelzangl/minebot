package net.famzangl.minecraft.minebot.ai.commands;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.ai.command.AICommandInvocation;
import net.famzangl.minecraft.minebot.ai.command.AICommandParameter;
import net.famzangl.minecraft.minebot.ai.command.ParameterType;
import net.famzangl.minecraft.minebot.ai.command.SafeStrategyRule;
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.InventoryDefinition;
import net.famzangl.minecraft.minebot.ai.strategy.UnstoreStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.UnstoreStrategy.Wishlist;

@AICommand(name = "minebot", helpText = "Get a list of items from a chest.")
public class CommandGet {
	@AICommandInvocation(safeRule = SafeStrategyRule.DEFEND)
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "get", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.STRING, fixedName = "", description = "") String data) {
		return new UnstoreStrategy(new Wishlist(new InventoryDefinition(data)));
	}
}