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

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.command.AIChatController;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.ai.command.AICommandInvocation;
import net.famzangl.minecraft.minebot.ai.command.AICommandParameter;
import net.famzangl.minecraft.minebot.ai.command.ParameterType;
import net.famzangl.minecraft.minebot.ai.command.SafeStrategyRule;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSets;
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.TaskStrategy;
import net.famzangl.minecraft.minebot.ai.task.WaitTask;
import net.famzangl.minecraft.minebot.ai.task.inventory.GetOnHotBarTask;
import net.famzangl.minecraft.minebot.ai.task.move.AlignToGridTask;
import net.famzangl.minecraft.minebot.build.ForBuildPathFinder;
import net.famzangl.minecraft.minebot.build.NextTaskTask;
import net.famzangl.minecraft.minebot.build.blockbuild.BuildTask;
import net.minecraft.util.math.BlockPos;

import java.util.List;

@AICommand(helpText = "Runs all tasks that are scheduled for building.", name = "minebuild")
public class CommandBuild {

	private static final class BuildStrategy extends TaskStrategy {
		private boolean alignSend;
		private boolean terrainChecked = false;
		private ForBuildPathFinder pathFinder;

		private final BlockPos[] positions = new BlockPos[5];

		@Override
		protected TickResult onGameTick(AIHelper helper) {
			if (!terrainChecked) {
				BlockPos pos = getBlockInTheWay(helper);
				if (pos != null) {
					AIChatController
							.addChatLine("The area is not cleared. Block in the way: "
									+ pos);
					return TickResult.ABORT;
				}
				terrainChecked = true;
			}
			return super.onGameTick(helper);
		}

		private BlockPos getBlockInTheWay(AIHelper helper) {
			for (BuildTask task : helper.buildManager.getScheduled()) {
				BlockPos pos = task.getForPosition();
				if (!BlockSets.AIR.isAt(helper.getWorld(), pos)) {
					return pos;
				}
			}
			return null;
		}

		@Override
		public void searchTasks(AIHelper helper) {
			final BuildTask task = helper.buildManager.peekNextTask();
			BlockPos pos;
			if (task == null) {
				AIChatController.addChatLine("No more build tasks.");
			} else if (!alignSend) {
				addTask(new AlignToGridTask(helper.getPlayerPosition()));
				alignSend = true;
			} else if (!task.isReadyForBuild(helper)) {
				AIChatController
				.addChatLine("There already is something at " + task.getForPosition() + ", skipping it.");
				addTask(new NextTaskTask());
				alignSend = false;
				pathFinder = null;
			} else if ((pos = isAroundSite(helper, task)) == null) {
				if (pathFinder == null) {
					pathFinder = new ForBuildPathFinder(task);
				}
				if (!pathFinder.searchSomethingAround(
						helper.getPlayerPosition(), helper, helper.getWorld(), this)) {
					addTask(new WaitTask());
				} else if (pathFinder.isNoPathFound()) {
					AIChatController
							.addChatLine("Cannot find a path to build task at "
									+ task.getForPosition()
									+ ". Trying to skip it.");
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
	}

	@AICommandInvocation(safeRule = SafeStrategyRule.DEFEND)
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "build", description = "") String nameArg2) {
		return new BuildStrategy();
	}

	public static BlockPos isAroundSite(AIHelper helper, BuildTask task) {
		final BlockPos forPosition = task.getForPosition();
		for (final BlockPos pos : task.getStandablePlaces()) {
			final int x = pos.getX() + forPosition.getX();
			final int y = pos.getY() + forPosition.getY();
			final int z = pos.getZ() + forPosition.getZ();
			System.out.println("Check " + forPosition + " + " + pos + " -> "
					+ helper.isStandingOn(x, y, z) + ", "
					+ task.couldBuildFrom(helper, x, y, z));
			if (helper.isStandingOn(x, y, z)
					&& task.couldBuildFrom(helper, x, y, z)) {
				return pos;
			}
		}
		return null;
	}

}
