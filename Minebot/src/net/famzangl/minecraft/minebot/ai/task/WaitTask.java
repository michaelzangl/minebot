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
import net.famzangl.minecraft.minebot.ai.path.world.WorldWithDelta;

/**
 * Waits a given number of game ticks.
 * 
 * @author michael
 *
 */
@SkipWhenSearchingPrefetch
public class WaitTask extends AITask {

	private final int ticks;
	private int ticked;

	public WaitTask() {
		this(1);
	}

	public WaitTask(int ticks) {
		this.ticks = ticks;
	}

	@Override
	public boolean isFinished(AIHelper h) {
		return ticks <= ticked;
	}

	@Override
	public void runTick(AIHelper h, TaskOperations o) {
		ticked++;
	}

	@Override
	public String toString() {
		return "WaitTask [ticks=" + ticks + "]";
	}
	
	@Override
	public boolean applyToDelta(WorldWithDelta world) {
		return true;
	}
	
	@Override
	public int getGameTickTimeout(AIHelper helper) {
		return ticks + 5;
	}

}
