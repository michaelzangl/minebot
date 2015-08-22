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
import net.minecraft.client.entity.EntityPlayerSP;

/**
 * Blindly walks to the given position.
 * 
 * @author michael
 *
 */
public class WalkTowardsStrategy extends AIStrategy {
	private static final int TICK_AGAIN_EVERY = 20 * 5;
	private final double x;
	private final double z;

	private int tickAgainCounter = TICK_AGAIN_EVERY;

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
		if (tickAgainCounter-- <= 0) {
			tickAgainCounter = TICK_AGAIN_EVERY;
			return TickResult.TICK_AGAIN;
		} else if (helper.walkTowards(x, z, false)) {
			return TickResult.NO_MORE_WORK;
		} else {
			return TickResult.TICK_HANDLED;
		}
	}

	@Override
	public String getDescription(AIHelper helper) {
		EntityPlayerSP player = helper.getMinecraft().thePlayer;
		double dist = player == null ? 0 : player.getDistance(x, player.posY, z);
		return "Walking to " + x + ", " + z + " (" + Math.round(dist) + "m)";
	}
}
