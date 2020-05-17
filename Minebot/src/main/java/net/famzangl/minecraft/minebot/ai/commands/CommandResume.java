package net.famzangl.minecraft.minebot.ai.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.ai.command.IAIControllable;

@AICommand(helpText = "Resume the last thing that was aborted.", name = "minebot")
public class CommandResume {

	public static void register(LiteralArgumentBuilder<IAIControllable> dispatcher) {
		dispatcher.then(
				Commands.literal("resume")
						.executes(context ->
								context.getSource().requestUseStrategy(context.getSource().getAiHelper().getResumeStrategy())
						));
	}
}
