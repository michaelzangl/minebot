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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.command.AIChatController;
import net.famzangl.minecraft.minebot.ai.path.TaskReceiver;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSets;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.famzangl.minecraft.minebot.ai.task.CanPrefaceAndDestroy;
import net.famzangl.minecraft.minebot.ai.task.SkipWhenSearchingPrefetch;
import net.famzangl.minecraft.minebot.ai.task.TaskOperations;
import net.famzangl.minecraft.minebot.ai.task.error.StringTaskError;
import net.famzangl.minecraft.minebot.ai.task.error.TaskError;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;

/**
 * This is a strategy that always queries for new tasks to do and then executes
 * them.
 * 
 * @author michael
 * 
 */
public abstract class TaskStrategy extends AIStrategy implements
		TaskOperations, TaskReceiver {
	private static final Marker MARKER_PREFACING = MarkerManager
			.getMarker("preface");
	private static final Marker MARKER_TASK = MarkerManager
			.getMarker("task");
	private static final Logger LOGGER = LogManager.getLogger(AIStrategy.class);
	private static final int MAX_LOOKAHEAD = 9;
	private static final int DESYNC_TIME = 5;
	// Maximum distance for aiming at blocks to destroy.
	private static final int MAX_PREDESTROY_DISTANCE = 4;
	private static final boolean LESS_ERRORS = false;
	protected final LinkedList<AITask> tasks = new LinkedList<AITask>();

	private int desyncTimer = 0;
	private boolean searchNewTasks = true;
	private AIHelper temporaryHelper;

	private final LinkedList<TaskError> lastErrors = new LinkedList<TaskError>();
	private int taskTimeout;
	private volatile AITask activeTask;

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
		LOGGER.error(MARKER_TASK, "Task sent desync. This is an error. Did the server lag?");
		LOGGER.error(MARKER_TASK, "Error: " + error);
		Thread.dumpStack();

		if (!LESS_ERRORS || !lastErrors.contains(error)) {
			AIChatController.addChatLine("Error: " + error.getMessage());
		}
		if (lastErrors.size() > 2) {
			lastErrors.removeFirst();
		}
		lastErrors.addLast(error);
		desyncTimer = DESYNC_TIME;
	}

	@Override
	public boolean faceAndDestroyForNextTask() {
		boolean found = false;
		for (int i = 1; i < MAX_LOOKAHEAD && i < tasks.size() && !found; i++) {
			final AITask task = tasks.get(i);
			LOGGER.trace(MARKER_PREFACING, "Prefetching with: " + task);
			if (tasks.get(i).getClass()
					.isAnnotationPresent(SkipWhenSearchingPrefetch.class)) {
				continue;
			} else if (task instanceof CanPrefaceAndDestroy) {
				final CanPrefaceAndDestroy dTask = (CanPrefaceAndDestroy) task;
				final List<BlockPos> positions = dTask
						.getPredestroyPositions(temporaryHelper);
				for (final BlockPos pos : positions) {
					if (!BlockSets.AIR.isAt(temporaryHelper.getWorld(), pos)
							&& pos.distanceSq(temporaryHelper
									.getPlayerPosition()) < MAX_PREDESTROY_DISTANCE
									* MAX_PREDESTROY_DISTANCE) {
						temporaryHelper.faceAndDestroy(pos);
						found = true;
						break;
					}
				}
			} else {
				LOGGER.trace(MARKER_PREFACING, "Prefetching showstopper: " + task);
				break;
			}
		}
		// if (!found) {
		// System.out.println("Could not prefetch anything. " + tasks.size());
		// }
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
		if (desyncTimer > 0) {
			activeTask = null;
			// clear the tasks
			for (AITask t : tasks) {
				t.onCanceled();
			}
			tasks.clear();
			LOGGER.debug(MARKER_TASK, "Waiting because of desync... " + desyncTimer);
			desyncTimer--;
			// pause for a tick, to reset all buttons, jump, ...
			return TickResult.TICK_HANDLED;
		}

		if (searchNewTasks) {
			activeTask = null;
			searchAndPrintTasks(helper);
			if (tasks.isEmpty()) {
				LOGGER.debug(MARKER_TASK, "No more tasks found.");
				return TickResult.NO_MORE_WORK;
			}
			taskTimeout = 0;
			searchNewTasks = false;
		} else if (tasks.isEmpty()) {
			searchNewTasks = true;
			activeTask = null;
			return TickResult.TICK_AGAIN;
		}

		final AITask task = tasks.peekFirst();
		if (task.isFinished(helper)) {
			LOGGER.trace(MARKER_TASK,"Task done: " + task);
			tasks.removeFirst();
			LOGGER.debug(MARKER_TASK,"Next task will be: " + tasks.peekFirst());
			taskTimeout = 0;
			activeTask = null;
			return TickResult.TICK_AGAIN;
		} else {
			int tickTimeout = task.getGameTickTimeout(helper);
			if (taskTimeout > tickTimeout) {
				LOGGER.error(MARKER_TASK, "Task timeout for: " + task);
				desync(new StringTaskError(
						"Task timed out. It should have been completed in "
								+ (tickTimeout / 20f) + "s"));
				activeTask = null;
				return TickResult.TICK_HANDLED;
			} else {
				temporaryHelper = helper;
				task.runTick(helper, this);
				temporaryHelper = null;
				taskTimeout++;
				activeTask = task;
				return TickResult.TICK_HANDLED;
			}
		}
	}

	private void searchAndPrintTasks(AIHelper helper) {
		searchTasks(helper);
		if (!tasks.isEmpty()) {
			LOGGER.trace(MARKER_TASK, "Found " + tasks.size() + " tasks, first task: "
					+ tasks.peekFirst());
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

	/**
	 * @return the desync flag
	 */
	public boolean isDesync() {
		return desyncTimer > 0;
	}
	
	@Override
	public void drawMarkers(RenderTickEvent event, AIHelper helper) {
		AITask activeTask2 = activeTask;
		if (activeTask2 != null) {
			activeTask2.drawMarkers(event, helper);
		}
		super.drawMarkers(event, helper);
	}

}
