package net.famzangl.minecraft.minebot.ai.commands;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

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
	private static final Marker MARKER_EDIT = MarkerManager
			.getMarker("edit");
	private static final Logger LOGGER = LogManager.getLogger(CommandSettings.class);

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
					LOGGER.catching(e);
				} catch (UnsupportedOperationException e) {
					AIChatController
							.addChatLine("Could not open an editor. Do it yourself: "
									+ MinebotSettings.getSettingsFile()
											.getAbsolutePath());
				}
			}

			private void edit(File settingsFile) throws IOException {
				if (openInJavaDefaultEditor(settingsFile)) {
					return;
				}

				ArrayList<String> editors = new ArrayList<String>();

				// TODO: Only linux
				editors.add("edit_json");
				editors.add("gedit");
				editors.add("kedit");
				editors.add("gvim");
				editors.add("notepad++.exe");
				for (String e : editors) {
					if (openWithProgram(e, settingsFile.getAbsolutePath())) {
						return;
					}
				}
				LOGGER.info("No editor found. Attempted: " + editors);
				throw new UnsupportedOperationException("No editor found.");
			}

			private boolean openInJavaDefaultEditor(File settingsFile)
					throws IOException {
				try {
					Desktop.getDesktop().edit(settingsFile);
					return true;
				} catch (UnsupportedOperationException e) {
					return false;
				}
			}

			private boolean openWithProgram(String e, String file)
					throws IOException {
				String envPath = System.getenv("PATH");
				LOGGER.trace("Open with " + e + " in " + envPath);
				for (String dir : envPath.split("\\" + File.pathSeparator)) {
					File testPorgram = new File(new File(dir), e);
					LOGGER.trace("Attempt to open the file with " + testPorgram.getAbsolutePath());
					if (testPorgram.canExecute()) {
						Process evaluated = Runtime.getRuntime().exec(
								new String[] { testPorgram.getAbsolutePath(), file });
						LOGGER.trace("Success.");
						return true;
					}
				}

				return false;
			}
		};
	}
}
