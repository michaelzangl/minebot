package net.famzangl.minecraft.minebot.build.commands;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.AIStrategy;
import net.famzangl.minecraft.minebot.ai.command.AIChatController;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.ai.command.AICommandInvocation;
import net.famzangl.minecraft.minebot.ai.command.AICommandParameter;
import net.famzangl.minecraft.minebot.ai.command.ParameterType;
import net.famzangl.minecraft.minebot.build.blockbuild.BuildTask;

import com.google.common.base.Function;

@AICommand(helpText = "List all scheduled commands.", name = "minebuild")
public class CommandListBuilds {

	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "list", description = "") String nameArg2) {
		return run(helper, nameArg2, 1);
	}

	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "list", description = "") String nameArg2,
			@AICommandParameter(type = ParameterType.NUMBER, description = "page") int page) {
		AIChatController.addToChatPaged("scheduled builds", page,
				helper.buildManager.getScheduled(),
				new Function<BuildTask, String>() {
					@Override
					public String apply(BuildTask task) {
						return task + "";
					}
				});
		return null;
	}

}
