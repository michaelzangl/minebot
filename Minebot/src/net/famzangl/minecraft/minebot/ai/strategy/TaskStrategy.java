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
package net.famzangl.minecraft.minebot.ai.strategy;

import java.util.LinkedList;
import java.util.List;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.command.AIChatController;
import net.famzangl.minecraft.minebot.ai.path.TaskReceiver;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.famzangl.minecraft.minebot.ai.task.CanPrefaceAndDestroy;
import net.famzangl.minecraft.minebot.ai.task.SkipWhenSearchingPrefetch;
import net.famzangl.minecraft.minebot.ai.task.TaskOperations;
import net.famzangl.minecraft.minebot.ai.task.error.StringTaskError;
import net.famzangl.minecraft.minebot.ai.task.error.TaskError;
import net.minecraft.util.BlockPos;

/**
 * This is a strategy that always queries for new tasks to do and then executes
 * them.
 * 
 * @author michael
 * 
 */
public abstract class TaskStrategy extends AIStrategy implements
		TaskOperations, TaskReceiver {
	private static final int MAX_LOOKAHEAD = 5;
	private final LinkedList<AITask> tasks = new LinkedList<AITask>();

	private boolean desync;
	private boolean searchNewTasks = true;
	private AIHelper temporaryHelper;

	private final LinkedList<TaskError> lastErrors = new LinkedList<TaskError>();
	private int taskTimeout;
	
	@Override
	protected void onDeactivate(AIHelper helper) {
		desync(new StringTaskError("An other strategy took over."));
		super.onDeactivate(helper);
	}

	@Override
	public void addTask(AITask task) {
		if (task == null) {
			throw new NullPointerException();
		}
		tasks.add(task);
	}

	@Override
	public void desync(TaskError error) {
		System.out.println("Desync. This is an error. Did the server lag?");
		System.out.println("Error: " + error);
		Thread.dumpStack();

		if (!lastErrors.contains(error)) {
			AIChatController.addChatLine("Error: " + error.getMessage());
		}
		if (lastErrors.size() > 2) {
			lastErrors.removeFirst();
		}
		lastErrors.addLast(error);
		desync = true;
	}

	@Override
	public boolean faceAndDestroyForNextTask() {
		boolean found = false;
		for (int i = 1; i < MAX_LOOKAHEAD && i < tasks.size() && !found; i++) {
			final AITask task = tasks.get(i);
//			System.out.println("Prefetching with: " + task);
			if (tasks.get(i).getClass()
					.isAnnotationPresent(SkipWhenSearchingPrefetch.class)) {
				continue;
			} else if (task instanceof CanPrefaceAndDestroy) {
				final CanPrefaceAndDestroy dTask = (CanPrefaceAndDestroy) task;
				final List<BlockPos> positions = dTask
						.getPredestroyPositions(temporaryHelper);
				for (final BlockPos pos : positions) {
					if (!temporaryHelper.isAirBlock(pos)) {
						temporaryHelper.faceAndDestroy(pos);
						found = true;
						break;
					}
				}
//				System.out.println("Prefacing: " + found + " for " + positions);
			} else {
//				System.out.println("Prefetching showstopper: " + task);
				break;
			}
		}
//		if (!found) {
//			System.out.println("Could not prefetch anything. " + tasks.size());
//		}
		return found;
	}
	
	@Override
	public boolean checkShouldTakeOver(AIHelper helper) {
		if (tasks.isEmpty()) {
			searchAndPrintTasks(helper);
			searchNewTasks = false;
		}
		return !tasks.isEmpty();
	}

	@Override
	protected TickResult onGameTick(AIHelper helper) {
		if (desync) {
			tasks.clear();
			desync = false;
			// pause for a tick, to reset all buttons, jump, ...
			return TickResult.TICK_HANDLED;
		}

		if (searchNewTasks) {
			searchAndPrintTasks(helper);
			if (tasks.isEmpty()) {
				System.out.println("No more tasks found.");
				return TickResult.NO_MORE_WORK;
			}
			taskTimeout = 0;
			searchNewTasks = false;
		} else if (tasks.isEmpty()) {
			searchNewTasks = true;
			return TickResult.TICK_AGAIN;
		}

		final AITask task = tasks.peekFirst();
		if (task.isFinished(helper)) {
			System.out.println("Task done: " + task);
			tasks.removeFirst();
			System.out.println("Next will be: " + tasks.peekFirst());
			taskTimeout = 0;
			return TickResult.TICK_AGAIN;
		} else if (taskTimeout > task.getGameTickTimeout()) {
			desync(new StringTaskError("Task timed out."));
			return TickResult.TICK_AGAIN;
		} else {
			temporaryHelper = helper;
			task.runTick(helper, this);
			temporaryHelper = null;
			taskTimeout++;
			return TickResult.TICK_HANDLED;
		}
	}

	private void searchAndPrintTasks(AIHelper helper) {
		searchTasks(helper);
		if (!tasks.isEmpty()) {
			System.out.println("Found " + tasks.size() + " tasks, first task: " + tasks.peekFirst());
		}
	}
	
	protected boolean hasMoreTasks() {
		return !tasks.isEmpty();
	}

	/**
	 * Searches for tasks to do. Always called in a game tick. The tasks should
	 * be added with {@link AIHelper#addTask(AITask)}.
	 * <p>
	 * You cannot make any assumptions on the state of the world. Tasks may even
	 * have been interrupted in between. You can be sure that there are no tasks
	 * currently added when this method is called.
	 * 
	 * @param helper
	 *            The helper that can be used.
	 */
	protected abstract void searchTasks(AIHelper helper);
}
