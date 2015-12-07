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
 * Stops after a given time.
 * 
 * @author michael
 *
 */
public class StopInStrategy extends TimeStrategy {

	private final int time;
	private final boolean force;

	public StopInStrategy(int seconds, boolean force) {
		super();
		this.force = force;
		this.time = seconds * 20;
	}

	@Override
	public boolean takesOverAnyTime() {
		return force;
	}

	@Override
	public boolean checkShouldTakeOver(AIHelper helper) {
		return getTimeElapsed(helper) > time;
	}

	@Override
	protected TickResult onGameTick(AIHelper helper) {
		if (getTimeElapsed(helper) > time) {
			return TickResult.ABORT;
		} else {
			return TickResult.NO_MORE_WORK;
		}
	}

	@Override
	public String getDescription(AIHelper helper) {
		int time = (int) ((this.time - getTimeElapsed(helper)) / 20);
		String m = time >= 60 ? time / 60 + "m " : "";
		return "Stop in " + m + (time % 60) + "s";
	}

	@Override
	public String toString() {
		return "StopInStrategy [time=" + time + ", force=" + force + "]";
	}

}
