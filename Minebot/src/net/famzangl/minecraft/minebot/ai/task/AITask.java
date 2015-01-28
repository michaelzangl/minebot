package net.famzangl.minecraft.minebot.ai.task;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.strategy.TaskStrategy;

/**
 * This is a specific task that the {@link TaskStrategy} should work on.
 * 
 * @author michael
 *
 */
public abstract class AITask {

	/**
	 * Returns <code>true</code> as soon as the task is finished. This gets
	 * called every time before a new tick is run.
	 * 
	 * @param h
	 * @return <code>true</code> If the task has no more work on this tick.
	 */
	public abstract boolean isFinished(AIHelper h);

	/**
	 * Lets the task control the bot for one tick. Only modify the bot state in
	 * this method.
	 * 
	 * @param h
	 * @param o
	 */
	public abstract void runTick(AIHelper h, TaskOperations o);

	/**
	 * How many game ticks this task should take. After this time, the task is
	 * considered to have failed and a search for new tasks is started. This is
	 * useful if e.g. the server laged. TODO: Automatically compute time needed
	 * to mine, ...
	 * 
	 * @return
	 */
	public int getGameTickTimeout() {
		return 20 * 5;
	}

}
