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
import net.minecraftforge.event.TickEvent;

import java.util.HashSet;

/**
 * Run a whole stack of strategies.
 * 
 * @see StrategyStack
 * 
 * @author michael
 * 
 */
public class StackStrategy extends AIStrategy {

	private final StrategyStack stack;
	private boolean aborted = false;

	public StackStrategy(StrategyStack stack) {
		super();
		this.stack = stack;
	}

	@Override
	protected TickResult onGameTick(AIHelper helper) {
		TickResult tickResult = stack.gameTick(helper);
		if (tickResult == TickResult.ABORT) {
			aborted = true;
			return TickResult.NO_MORE_WORK;
		}
		return tickResult;
	}

	@Override
	public boolean checkShouldTakeOver(AIHelper helper) {
		return !aborted && stack.couldTakeOver(helper);
	}

	@Override
	protected void onActivate(AIHelper helper) {
		stack.resume(helper);
		super.onActivate(helper);
	}

	@Override
	protected void onDeactivate(AIHelper helper) {
		stack.pause(helper);
		super.onDeactivate(helper);
	}

	@Override
	public String getDescription(AIHelper helper) {
		StringBuilder str = new StringBuilder("");
		HashSet<String> alreadyDisplayed = new HashSet<String>();
		AIStrategy current = stack.getCurrentStrategy();
		for (AIStrategy s : stack.getStrategies()) {
			String description = s.getDescription(helper);
			for (String line : description.split("\n")) {
				if (line.isEmpty()) {
					continue;
				}
				boolean markAsCurrent = s == current && !(s instanceof StackStrategy);
				if (!alreadyDisplayed.add(line) && !markAsCurrent) {
					continue;
				}
				if (str.length() != 0) {
					str.append("\n");
				}
				if (markAsCurrent) {
					str.append("-> ");
				}
				str.append(line);
			}
		}
		return str.toString();
	}

	@Override
	public void drawMarkers(TickEvent.RenderTickEvent event, AIHelper helper) {
		AIStrategy current = stack.getCurrentStrategy();
		if (current != null) {
			current.drawMarkers(event, helper);
		}
	}

	@Override
	public String toString() {
		return "StackStrategy [stack=" + stack + ", aborted=" + aborted + "]";
	}
}
