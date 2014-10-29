package net.famzangl.minecraft.minebot.build.commands;

import java.util.List;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.command.AIChatController;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.ai.command.AICommandInvocation;
import net.famzangl.minecraft.minebot.ai.command.AICommandParameter;
import net.famzangl.minecraft.minebot.ai.command.ParameterType;
import net.famzangl.minecraft.minebot.ai.command.SafeStrategyRule;
import net.famzangl.minecraft.minebot.ai.render.MarkingStrategy;
import net.famzangl.minecraft.minebot.ai.render.PosMarkerRenderer;
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.TaskStrategy;
import net.famzangl.minecraft.minebot.ai.task.WaitTask;
import net.famzangl.minecraft.minebot.ai.task.inventory.GetOnHotBarTask;
import net.famzangl.minecraft.minebot.ai.task.move.AlignToGridTask;
import net.famzangl.minecraft.minebot.build.ForBuildPathFinder;
import net.famzangl.minecraft.minebot.build.NextTaskTask;
import net.famzangl.minecraft.minebot.build.blockbuild.BuildTask;
import net.minecraftforge.client.event.RenderWorldLastEvent;

@AICommand(helpText = "Runs all tasks that are scheduled for building.", name = "minebuild")
public class CommandBuild {

	private static final class BuildStrategy extends TaskStrategy implements
			MarkingStrategy {
		private boolean alignSend;
		private ForBuildPathFinder pathFinder;

		private final PosMarkerRenderer renderer = new PosMarkerRenderer(1, 1,
				0);
		private final Pos[] positions = new Pos[5];

		@Override
		public void searchTasks(AIHelper helper) {
			final BuildTask task = helper.buildManager.peekNextTask();
			Pos pos;
			if (task == null) {
				AIChatController.addChatLine("No more build tasks.");
			} else if (!alignSend) {
				addTask(new AlignToGridTask(helper.getPlayerPosition()));
				alignSend = true;
			} else if ((pos = isAroundSite(helper, task)) == null) {
				if (pathFinder == null) {
					pathFinder = new ForBuildPathFinder(task);
				}
				if (!pathFinder.searchSomethingAround(
						helper.getPlayerPosition(), helper, this)) {
					addTask(new WaitTask());
				} else if (pathFinder.isNoPathFound()) {
					AIChatController
							.addChatLine("Cannot navigate to next build task.");
				}
			} else {
				addTask(new GetOnHotBarTask(task.getRequiredItem()));
				addTask(task.getPlaceBlockTask(pos));
				addTask(new NextTaskTask());
				alignSend = false;
				pathFinder = null;
			}
			reloadPositions(helper.buildManager.getScheduled());
		}

		private void reloadPositions(List<BuildTask> list) {
			for (int i = 0; i < positions.length; i++) {
				positions[i] = i < list.size() ? list.get(i).getForPosition()
						: null;
			}
		}

		@Override
		public String getDescription(AIHelper helper) {
			return "Building.";
		}

		@Override
		public void drawMarkers(RenderWorldLastEvent event, AIHelper helper) {
			renderer.render(event, helper, positions);
		}
	}

	@AICommandInvocation(safeRule = SafeStrategyRule.DEFEND)
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
