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

import net.famzangl.minecraft.minebot.ai.AIHelper;

/**
 * A strategy that only needs to be run for exactly one game tick.
 * 
 * @author michael
 *
 */
public abstract class RunOnceStrategy extends AIStrategy {

	private boolean wasRun = false;

	@Override
	protected TickResult onGameTick(AIHelper helper) {
		if (!wasRun) {
			wasRun = true;
			return this.doSingleRun(helper);
		}
		return TickResult.NO_MORE_WORK;
	}

	protected TickResult doSingleRun(AIHelper helper) {
		this.singleRun(helper);
		return TickResult.NO_MORE_WORK;
	}

	/**
	 * The code that should be run once. If you want to modify game state in
	 * this method, use {@link RunOneTickStrategy}.
	 * 
	 * @param helper
	 */
	protected abstract void singleRun(AIHelper helper);

	@Override
	public boolean checkShouldTakeOver(AIHelper helper) {
		return !wasRun;
	}
}
