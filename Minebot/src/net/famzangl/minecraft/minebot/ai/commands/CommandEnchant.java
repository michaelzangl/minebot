package net.famzangl.minecraft.minebot.ai.commands;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.ai.command.AICommandInvocation;
import net.famzangl.minecraft.minebot.ai.command.AICommandParameter;
import net.famzangl.minecraft.minebot.ai.command.ParameterType;
import net.famzangl.minecraft.minebot.ai.command.SafeStrategyRule;
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.EnchantStrategy;
import net.famzangl.minecraft.minebot.ai.task.inventory.ItemWithSubtype;

@AICommand(helpText = "Kill mobs in range and enchant whatever you have in the inventory.", name = "minebot")
public class CommandEnchant {
	@AICommandInvocation(safeRule = SafeStrategyRule.DEFEND)
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "enchant", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.NUMBER, description = "Filter item id", optional = true) Integer itemId) {
		return new EnchantStrategy(itemId == null ? null : new ItemWithSubtype(itemId, 0));
	}
}
