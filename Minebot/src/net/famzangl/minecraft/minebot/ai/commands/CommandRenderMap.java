package net.famzangl.minecraft.minebot.ai.commands;

import java.io.File;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.ai.command.AICommandInvocation;
import net.famzangl.minecraft.minebot.ai.command.AICommandParameter;
import net.famzangl.minecraft.minebot.ai.command.ParameterType;
import net.famzangl.minecraft.minebot.ai.command.SafeStrategyRule;
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.RunOnceStrategy;
import net.famzangl.minecraft.minebot.map.IconDefinition;
import net.famzangl.minecraft.minebot.map.IconType;
import net.famzangl.minecraft.minebot.map.MapReader;
import net.famzangl.minecraft.minebot.settings.MinebotSettings;

@AICommand(helpText = "Store a map of this world to a png file.", name = "minebot")
public class CommandRenderMap {

	@AICommandInvocation(safeRule = SafeStrategyRule.NONE)
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "map", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "start", description = "") String start,
			@AICommandParameter(type = ParameterType.STRING, description = "The map name") final String name) {
		return new RunOnceStrategy() {
			@Override
			protected void singleRun(AIHelper helper) {
				final File dir = MinebotSettings.getDataDirFile("map");
				helper.setActiveMapReader(new MapReader(new File(dir, name
						+ ".png")));
			}
		};
	}

	@AICommandInvocation(safeRule = SafeStrategyRule.NONE)
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "map", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "stop", description = "") String start) {
		return new RunOnceStrategy() {
			@Override
			protected void singleRun(AIHelper helper) {
				helper.setActiveMapReader(null);
			}
		};
	}

	@AICommandInvocation(safeRule = SafeStrategyRule.NONE)
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "map", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "mark", description = "") String nameArg2,
			@AICommandParameter(type = ParameterType.ENUM, description = "The icon type") final IconType type) {
		return new RunOnceStrategy() {
			@Override
			protected void singleRun(AIHelper helper) {
				MapReader activeMapReader = helper.getActiveMapReader();
				activeMapReader.addIcon(new IconDefinition(helper
						.getPlayerPosition(), "",
						type == null ? IconType.DEFAULT : type));
			}
		};
	}
}
