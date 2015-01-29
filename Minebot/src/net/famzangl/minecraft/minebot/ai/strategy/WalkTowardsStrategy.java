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
 * Blindly walks to the given position.
 * 
 * @author michael
 *
 */
public class WalkTowardsStrategy extends AIStrategy {
	private final double x;
	private final double z;

	public WalkTowardsStrategy(double x, double z) {
		this.x = x;
		this.z = z;
	}

	@Override
	public boolean checkShouldTakeOver(AIHelper helper) {
		return !helper.arrivedAt(x, z);
	}

	@Override
	protected TickResult onGameTick(AIHelper helper) {
		return helper.walkTowards(x, z, false) ? TickResult.NO_MORE_WORK
				: TickResult.TICK_HANDLED;
	}
}
