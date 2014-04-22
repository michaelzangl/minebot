package net.famzangl.minecraft.minebot.build;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.AIStrategy;
import net.famzangl.minecraft.minebot.ai.command.AIChatController;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.famzangl.minecraft.minebot.ai.task.AlignToGridTask;
import net.famzangl.minecraft.minebot.ai.task.WaitTask;
import net.famzangl.minecraft.minebot.build.blockbuild.BuildTask;
import net.minecraft.command.ICommandSender;

/**
 * Loop over walk, place, next
 * 
 * @author michael
 * 
 */
public class CommandBuild implements AICommand {

	@Override
	public String getName() {
		return "build";
	}

	@Override
	public String getArgsUsage() {
		return "";
	}

	@Override
	public String getHelpText() {
		return "Build all scheduled stuff.";
	}

	@Override
	public AIStrategy evaluateCommand(ICommandSender sender, String[] args,
			AIHelper h, AIChatController aiChatController) {
		return new AIStrategy() {
			private boolean alignSend;
			private ForBuildPathFinder pathFinder;

			@Override
			public void searchTasks(AIHelper helper) {
				final BuildTask task = helper.buildManager.peekNextTask();
				Pos pos;
				if (task == null) {
					AIChatController.addChatLine("No more build tasks.");
				} else if (!alignSend) {
					helper.addTask(new AlignToGridTask(helper
							.getPlayerPosition()));
					alignSend = true;
				} else if ((pos = isAroundSite(helper, task)) == null) {
					if (pathFinder == null) {
						pathFinder = new ForBuildPathFinder(helper, task);
					}
					if (!pathFinder.searchSomethingAround(helper
							.getPlayerPosition())) {
						helper.addTask(WaitTask.instance);
					}
				} else {
					helper.addTask(task.getPlaceBlockTask(pos));
					helper.addTask(new NextTaskTask());
					alignSend = false;
					pathFinder = null;
				}
			}

			@Override
			public AITask getOverrideTask(AIHelper helper) {
				return null;
			}

			@Override
			public String getDescription() {
				return "Building.";
			}
		};
	}

	public static Pos isAroundSite(AIHelper helper, BuildTask task) {
		Pos forPosition = task.getForPosition();
		for (Pos p : task.getStandablePlaces()) {
			int x = p.x + forPosition.x;
			int y = p.y + forPosition.y;
			int z = p.z + forPosition.z;
			System.out.println("Check " + forPosition + " + " + p + " -> " + helper.isStandingOn(x, y, z) + ", " + task.couldBuildFrom(helper, x, y, z));
			if (helper.isStandingOn(x, y, z)
					&& task.couldBuildFrom(helper, x, y, z)) {
				return p;
			}
		}
		return null;
	}

}
