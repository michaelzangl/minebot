package net.famzangl.minecraft.minebot.ai.commands;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.command.AIChatController;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.ai.command.AICommandInvocation;
import net.famzangl.minecraft.minebot.ai.command.AICommandParameter;
import net.famzangl.minecraft.minebot.ai.command.ParameterType;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.RunOnceStrategy;
import net.famzangl.minecraft.minebot.settings.MinebotSettings;
import net.famzangl.minecraft.minebot.stats.StatsWindow;
import net.minecraft.block.BlockSign;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IChatComponent;

@AICommand(helpText = "Dump all signs to a text file.", name = "minebot")
public class CommandStats {
	private static final BlockSet SIGNS = new BlockSet(Blocks.standing_sign, Blocks.wall_sign);
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
