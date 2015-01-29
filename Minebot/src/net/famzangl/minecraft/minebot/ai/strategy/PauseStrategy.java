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
 * Does nothing for the given amount of ticks (no matter if it was really active
 * that many ticks or other strategies higher in the stack were active)
 * 
 * @author michael
 *
 */
public class PauseStrategy extends AIStrategy {

	private int leftTicks;
	private boolean shouldTickAgain;

	public PauseStrategy(int seconds) {
		super();
		this.leftTicks = seconds * 20;
	}

	@Override
	public boolean checkShouldTakeOver(AIHelper helper) {
		return leftTicks > 0;
	}

	@Override
	protected TickResult onGameTick(AIHelper helper) {
		if (shouldTickAgain) {
			shouldTickAgain = false;
			return TickResult.TICK_AGAIN;
		} else if (leftTicks > 0) {
			leftTicks--;
			shouldTickAgain = true;
			return TickResult.TICK_HANDLED;
		}
		return TickResult.NO_MORE_WORK;
	}

	@Override
	public String getDescription(AIHelper helper) {
		return "Pausing for " + (leftTicks / 20) + "s";
	}

}
