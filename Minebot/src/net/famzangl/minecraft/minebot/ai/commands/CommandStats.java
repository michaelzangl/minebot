package net.famzangl.minecraft.minebot.ai.commands;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.ai.command.AICommandInvocation;
import net.famzangl.minecraft.minebot.ai.command.AICommandParameter;
import net.famzangl.minecraft.minebot.ai.command.ParameterType;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.RunOnceStrategy;
import net.famzangl.minecraft.minebot.stats.StatsWindow;
import net.minecraft.init.Blocks;

@AICommand(helpText = "Dump all signs to a text file.", name = "minebot")
public class CommandStats {
	private static final BlockSet SIGNS = new BlockSet(Blocks.STANDING_SIGN, Blocks.WALL_SIGN);
	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "stats", description = "") String nameArg) {
		return new RunOnceStrategy() {
			@Override
			protected void singleRun(AIHelper helper) {
				new StatsWindow(helper.getStats()).setVisible(true);
			}
		};
	}

}
