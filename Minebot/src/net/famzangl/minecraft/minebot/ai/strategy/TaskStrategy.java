package net.famzangl.minecraft.minebot.ai.strategy;

import java.util.LinkedList;
import java.util.List;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.command.AIChatController;
import net.famzangl.minecraft.minebot.ai.path.TaskReceiver;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.famzangl.minecraft.minebot.ai.task.CanPrefaceAndDestroy;
import net.famzangl.minecraft.minebot.ai.task.SkipWhenSearchingPrefetch;
import net.famzangl.minecraft.minebot.ai.task.error.StringTaskError;
import net.famzangl.minecraft.minebot.ai.task.error.TaskError;

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
			System.out.println("Prefetching with: " + task);
			if (tasks.get(i).getClass()
					.isAnnotationPresent(SkipWhenSearchingPrefetch.class)) {
				continue;
			} else if (task instanceof CanPrefaceAndDestroy) {
				final CanPrefaceAndDestroy dTask = (CanPrefaceAndDestroy) task;
				final List<Pos> positions = dTask
						.getPredestroyPositions(temporaryHelper);
				for (final Pos pos : positions) {
					if (!temporaryHelper.isAirBlock(pos.x, pos.y, pos.z)) {
						temporaryHelper.faceAndDestroy(pos.x, pos.y, pos.z);
						found = true;
						break;
					}
				}
				System.out.println("Prefacing: " + found + " for " + positions);
			} else {
				System.out.println("Prefetching showstopper: " + task);
				break;
			}
		}
		if (!found) {
			System.out.println("Could not prefetch anything. " + tasks.size());
		}
		return found;
	}

	@Override
	protected TickResult onGameTick(AIHelper helper) {
		if (desync) {
			tasks.clear();
			desync = false;
			return TickResult.TICK_AGAIN;
		}

		if (searchNewTasks) {
			searchTasks(helper);
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
			System.out.println("Task done.");
			tasks.removeFirst();
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
