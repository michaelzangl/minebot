package net.famzangl.minecraft.minebot.build.reverse;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.AIStrategy;
import net.famzangl.minecraft.minebot.ai.command.AIChatController;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;

public class CommandReverse implements AICommand {

	@Override
	public String getName() {
		return "build:reverse";
	}

	@Override
	public String getArgsUsage() {
		return "[out-file]";
	}

	@Override
	public String getHelpText() {
		return "Get a build script for the selected area.\n"
				+ "out-file is the file to write to."
				+ "It can be - to write to stdout."
				+ "If no file is given, a new one is generated.";
	}

	@Override
	public AIStrategy evaluateCommand(ICommandSender sender, String[] args,
			AIHelper h, AIChatController aiChatController) {
		final String outFile;
		if (args.length == 1) {
			File dir = new File( Minecraft.getMinecraft().mcDataDir, "minebot");
			dir.mkdirs();
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
			String date = df.format(Calendar.getInstance().getTime());

			File file = new File(dir, date + ".buildscript.txt");
			return new RunReverseBuildStrategy(file.getAbsolutePath());
		} else if (args.length == 2) {
			return new RunReverseBuildStrategy(args[1]);
		} else {
			aiChatController.usage(this);
			return null;
		}
	}
}
