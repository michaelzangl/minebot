package net.famzangl.minecraft.minebot.ai;

import net.famzangl.minecraft.minebot.ai.task.AITask;

/**
 * An {@link AIStrategy} tells the bot what to do next. It should recover from
 * any state that might happen while the bot executes.
 * 
 * @author michael
 * 
 */
public interface AIStrategy {

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
	void searchTasks(AIHelper helper);

	/**
	 * 
	 * @return A String to display in the top right hand corner of the screen.
	 */
	String getDescription();

	/**
	 * get an emergency override task.
	 * 
	 * @param aiController
	 * @return A not-<code>null</code>-Value to cancel all pending tasks and
	 *         execute a new one instead.
	 */
	AITask getOverrideTask(AIHelper helper);

}
