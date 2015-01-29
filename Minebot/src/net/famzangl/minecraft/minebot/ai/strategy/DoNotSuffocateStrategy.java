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

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;

/**
 * Prevents a suffocation in walls because of server lags.
 * 
 * @author michael
 * 
 */
public class DoNotSuffocateStrategy extends AIStrategy {

	@Override
	public boolean takesOverAnyTime() {
		return true;
	}
	
	@Override
	public boolean checkShouldTakeOver(AIHelper helper) {
		Pos p = helper.getPlayerPosition();
		return (!safeGround(helper, p) || !safeHead(helper, p));
	}
	
	@Override
	protected TickResult onGameTick(AIHelper helper) {
		Pos p = helper.getPlayerPosition();
		if (!safeGround(helper, p)) {
			helper.faceAndDestroy(p);
			return TickResult.TICK_HANDLED;
		} else if (!safeHead(helper, p)) {
			helper.faceAndDestroy(p.add(0,1,0));
			return TickResult.TICK_HANDLED;
		}
		return TickResult.NO_MORE_WORK;
	}

	private boolean safeHead(AIHelper helper, Pos p) {
		return helper.canWalkThrough(helper.getBlock(p.getX(), p.getY() + 1, p.getZ()));
	}

	private boolean safeGround(AIHelper helper, Pos p) {
		return helper.canWalkOn(helper.getBlock(p.getX(), p.getY(), p.getZ()));
	}
	
	@Override
	public String getDescription(AIHelper helper) {
		return "Do not suffocate in walls.";
	}

}
