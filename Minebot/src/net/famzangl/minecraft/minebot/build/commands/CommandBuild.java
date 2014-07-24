package net.famzangl.minecraft.minebot.build.commands;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.AIStrategy;
import net.famzangl.minecraft.minebot.ai.command.AIChatController;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.ai.command.AICommandInvocation;
import net.famzangl.minecraft.minebot.ai.command.AICommandParameter;
import net.famzangl.minecraft.minebot.ai.command.ParameterType;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.famzangl.minecraft.minebot.ai.task.GetOnHotBarTask;
import net.famzangl.minecraft.minebot.ai.task.WaitTask;
import net.famzangl.minecraft.minebot.ai.task.move.AlignToGridTask;
import net.famzangl.minecraft.minebot.build.ForBuildPathFinder;
import net.famzangl.minecraft.minebot.build.NextTaskTask;
import net.famzangl.minecraft.minebot.build.blockbuild.BuildTask;

@AICommand(helpText = "Runs all tasks that are scheduled for building.", name = "minebuild")
public class CommandBuild {

	private static final class BuildStrategy implements AIStrategy {
		private boolean alignSend;
		private ForBuildPathFinder pathFinder;

		@Override
		public void searchTasks(AIHelper helper) {
			final BuildTask task = helper.buildManager.peekNextTask();
			Pos pos;
			if (task == null) {
				AIChatController.addChatLine("No more build tasks.");
			} else if (!alignSend) {
				helper.addTask(new AlignToGridTask(helper.getPlayerPosition()));
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
				helper.addTask(new GetOnHotBarTask(task.getRequiredItem()));
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
	}

	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "build", description = "") String nameArg2) {
		return new BuildStrategy();

	}

	public static Pos isAroundSite(AIHelper helper, BuildTask task) {
		final Pos forPosition = task.getForPosition();
		for (final Pos p : task.getStandablePlaces()) {
			final int x = p.x + forPosition.x;
			final int y = p.y + forPosition.y;
			final int z = p.z + forPosition.z;
			System.out.println("Check " + forPosition + " + " + p + " -> "
					+ helper.isStandingOn(x, y, z) + ", "
					+ task.couldBuildFrom(helper, x, y, z));
			if (helper.isStandingOn(x, y, z)
					&& task.couldBuildFrom(helper, x, y, z)) {
				return p;
			}
		}
		return null;
	}

}
