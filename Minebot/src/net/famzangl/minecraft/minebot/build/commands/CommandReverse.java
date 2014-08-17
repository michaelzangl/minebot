package net.famzangl.minecraft.minebot.build.commands;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.ai.command.AICommandInvocation;
import net.famzangl.minecraft.minebot.ai.command.AICommandParameter;
import net.famzangl.minecraft.minebot.ai.command.ParameterType;
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy;
import net.famzangl.minecraft.minebot.build.reverse.RunReverseBuildStrategy;
import net.minecraft.client.Minecraft;

@AICommand(helpText = "Get a build script for the selected area.\n"
		+ "out-file is the file to write to."
		+ "It can be - to write to stdout."
		+ "If no file is given, a new one is generated.", name = "minebuild")
public class CommandReverse {

	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "reverse", description = "") String nameArg) {
		final File dir = new File(Minecraft.getMinecraft().mcDataDir, "minebot");
		dir.mkdirs();
		final DateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		final String date = df.format(Calendar.getInstance().getTime());

		final File file = new File(dir, date + ".buildscript.txt");
		return run(helper, nameArg, file.getAbsolutePath());
	}

	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "reverse", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.FILE, description = "Output file") String file) {
		return new RunReverseBuildStrategy(file);
	}

}
