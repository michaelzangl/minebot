/*******************************************************************************
 * This file is part of Minebot.
 *
 * Minebot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Minebot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Minebot.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
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
import net.famzangl.minecraft.minebot.settings.MinebotSettings;

@AICommand(helpText = "Get a build script for the selected area.\n"
		+ "out-file is the file to write to."
		+ "It can be - to write to stdout."
		+ "If no file is given, a new one is generated.", name = "minebuild")
public class CommandReverse {

	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "reverse", description = "") String nameArg) {
		final File dir = MinebotSettings.getDataDirFile("build");
		dir.mkdirs();
		final DateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		final String date = df.format(Calendar.getInstance().getTime());

		final File file = new File(dir, date + ".buildscript.txt");
		return run(helper, nameArg, file);
	}

	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "reverse", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.FILE, relativeToSettingsFile="build", description = "Output file") File file) {
		return new RunReverseBuildStrategy(file);
	}

}
