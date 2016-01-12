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
package net.famzangl.minecraft.minebot.ai.task;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.path.world.RecordingWorld;
import net.famzangl.minecraft.minebot.ai.path.world.WorldWithDelta;
import net.famzangl.minecraft.minebot.ai.strategy.TaskStrategy;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This is a specific task that the {@link TaskStrategy} should work on.
 * 
 * @author michael
 *
 */
public abstract class AITask {
	/**
	 * A logger that can be used by all tasks. Use your own marker to mark your messages.
	 */
	protected static final Logger LOGGER = LogManager
			.getLogger(AITask.class);

	private int gameTickTimeoutByBlockDestruction = -1;

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
	 * useful if e.g. the server laged.
	 * <p>
	 * The timeout returned by this method may be changed while the task is
	 * running.
	 * <p>
	 * The default timeout for tasks is 5 seconds. The timeout for tasks that
	 * specify a list of blocks to destroy is less.
	 * 
	 * @param helper
	 *            The AI helper to compute the time.
	 * 
	 * @return The timeout for this task in game ticks.
	 */
	public int getGameTickTimeout(AIHelper helper) {
		if (gameTickTimeoutByBlockDestruction < 0) {
			gameTickTimeoutByBlockDestruction = Math.max(
					computeGameTickTimeout(helper), 5);
		}

		return gameTickTimeoutByBlockDestruction;
	}

	/**
	 * This computes the game tick timeout once.
	 * <p>
	 * The default value is the time to apply the world delta.
	 * <p>
	 * If that list is empty, the timeout is 5 seconds.
	 * 
	 * @return The expected game tick timeout.
	 */
	protected int computeGameTickTimeout(AIHelper helper) {
		// List<BlockPos> blocks = getBlocksToDestory(helper.getWorld());
		// if (blocks.isEmpty()) {
		// return 20 * 5;
		// } else {
		// int time = 5;
		// for (BlockPos b : blocks) {
		// time += getTimeToMine(helper.getWorld(), b);
		// }
		// }
		RecordingWorld world = new RecordingWorld(helper.getWorld(),
				helper.getMinecraft().thePlayer);
		if (applyToDelta(world)) {
			return (int) (world.getTimeInTicks() * 1.3f);
		} else {
			return 5 * 20;
		}
	}

	// /**
	// * Computes the list of blocks that this task destroys to compute the time
	// needed for it.
	// * @param world
	// * @return
	// */
	// protected List<BlockPos> getBlocksToDestory(WorldData world) {
	// return Collections.emptyList();
	// }

	/**
	 * Attempts to apply this task to the world. The task needs to fail if that
	 * delta was not reached.
	 * 
	 * @param world
	 *            The world.
	 * @return true if the delta was successfully applied.
	 */
	public boolean applyToDelta(WorldWithDelta world) {
		// default is unsupported.
		return false;
	}

	/**
	 * Called whenever this task was canceled.
	 */
	public void onCanceled() {
	}

	public void drawMarkers(RenderTickEvent event, AIHelper helper) {
	}

}
