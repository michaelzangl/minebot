package net.famzangl.minecraft.minebot.build;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.AIStrategy;
import net.famzangl.minecraft.minebot.ai.command.AIChatController;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.famzangl.minecraft.minebot.build.blockbuild.BuildTask;
import net.minecraft.command.ICommandSender;

public class CommandStepPlace implements AICommand {
	@Override
	public String getName() {
		return "build:place";
	}

	@Override
	public String getArgsUsage() {
		return "";
	}

	@Override
	public String getHelpText() {
		return "Place the block. Use build:walk before this command.";
	}

	@Override
	public AIStrategy evaluateCommand(ICommandSender sender, String[] args,
			AIHelper h, AIChatController aiChatController) {
		if (args.length != 1) {
			aiChatController.usage(this);
			return null;
		} else {
			return new AIStrategy() {
				@Override
				public void searchTasks(AIHelper helper) {
					final BuildTask task = helper.buildManager.peekNextTask();
					if (task == null) {
						AIChatController.addChatLine("No more build tasks.");
					} else {
						addStep(helper, task);
					}
				}

				@Override
				public AITask getOverrideTask(AIHelper helper) {
					return null;
				}

				@Override
				public String getDescription() {
					return "Place the block.";
				}
			};
		}
	}
	
	public static void addStep(AIHelper helper, final BuildTask task) {
		Pos forPosition = task.getForPosition();
		Pos fromPos = getFromPos(helper, task, forPosition);
		if (fromPos == null) {
			AIChatController
					.addChatLine("Not at starting position.");
		} else if (helper.isAirBlock(task.getForPosition().x,
				task.getForPosition().y,
				task.getForPosition().z)) {
			AITask t = task.getPlaceBlockTask(fromPos);
			if (t != null) {
				helper.addTask(t);
			}
		} else {
			AIChatController
					.addChatLine("Could not place the block: There is already something!");
		}
	}

	private static Pos getFromPos(AIHelper helper, final BuildTask task,
			Pos forPosition) {
		Pos fromPos = null;
		for (Pos p : task.getStandablePlaces()) {
			if (helper.isStandingOn(p.x + forPosition.x, p.y
					+ forPosition.y, p.z + forPosition.z)) {
				fromPos = p;
			}
		}
		return fromPos;
	}


}
