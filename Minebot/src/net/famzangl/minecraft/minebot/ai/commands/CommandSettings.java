package net.famzangl.minecraft.minebot.ai.commands;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.command.AIChatController;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.ai.command.AICommandInvocation;
import net.famzangl.minecraft.minebot.ai.command.AICommandParameter;
import net.famzangl.minecraft.minebot.ai.command.ParameterType;
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.RunOnceStrategy;
import net.famzangl.minecraft.minebot.settings.MinebotSettings;

@AICommand(helpText = "Edit settings.", name = "minebot")
public class CommandSettings {

	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "settings", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "store", description = "") String nameArg2) {
		return new RunOnceStrategy() {
			@Override
			protected void singleRun(AIHelper helper) {
				MinebotSettings.writeSettings();
				AIChatController.addChatLine("Written to "
						+ MinebotSettings.getSettingsFile().getAbsolutePath());
			}
		};
	}

	@AICommandInvocation()
	public static AIStrategy run2(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "settings", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "edit", description = "") String nameArg2) {
		return new RunOnceStrategy() {
			@Override
			protected void singleRun(AIHelper helper) {
				MinebotSettings.writeSettings();
				try {
					edit(MinebotSettings.getSettingsFile());
				} catch (IOException e) {
					AIChatController.addChatLine("Could not open the file.");
				} catch (UnsupportedOperationException e) {
					AIChatController
							.addChatLine("Could not open an editor. Do it yourself: "
									+ MinebotSettings.getSettingsFile()
											.getAbsolutePath());
				}
			}

			private void edit(File settingsFile) throws IOException {
				try {
					Desktop.getDesktop().edit(settingsFile);
					return;
				} catch (UnsupportedOperationException e) {
				}

				ArrayList<String> editors = new ArrayList<String>();

				// TODO: Only linux
				editors.add("edit");
				for (String e : editors) {
					Runtime.getRuntime().exec(
							new String[] { e, settingsFile.getAbsolutePath() });
				}
			}
		};
	}
}
