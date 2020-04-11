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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy.TickResult;

/**
 * This is a stack of strategies, ordered by priority. The strategy with the
 * highest priority that wants to take over gets control. Strategies with higher
 * priority are asked if they want to take over if either
 * {@link AIStrategy#takesOverAnyTime()} evaluates to <code>true</code> or the
 * current strategy signaled that it is in a good state for other strategies to
 * take over.
 * <p>
 * Strategies can give control to other strategies in the stack by returning
 * different values in their {@link AIStrategy#gameTick(AIHelper)}:
 * <dl>
 * <dt> {@link TickResult#NO_MORE_WORK}</dt>
 * <dd>The current strategy has no more work to do and releases control.
 * {@link AIStrategy#checkShouldTakeOver(AIHelper)} is called afterwards to
 * query if the strategy has more work..</dd>
 * <dt> {@link TickResult#TICK_HANDLED}</dt>
 * <dd>The bot was controled for this tick.</dd>
 * <dt> {@link TickResult#TICK_AGAIN}</dt>
 * <dd>The strategy has more work to do. It did not do any actions that
 * influenced the bot state. If no higher priority strategies have work (e.g.
 * eating), this strategy is just called again..</dd>
 * <dt> {@link TickResult#ABORT}</dt>
 * <dd>The whole stack should exit.</dd>
 * </dl>
 * 
 * @author michael
 *
 */
public class StrategyStack {
	/**
	 * Strategies, ordered from most to least important.
	 */
	private final ArrayList<AIStrategy> strategies = new ArrayList<AIStrategy>();
	private AIStrategy currentStrategy = null;
	private boolean paused;
	private boolean goodPause;

	/**
	 * Does a game tick. Selects the next best strategy to activate.
	 * 
	 * @param helper
	 * @return <code>true</code> on success.
	 */
	public TickResult gameTick(AIHelper helper) {
		final TickResult result = strategyTick(helper, currentStrategy == null
				|| goodPause);
		goodPause = false;
		if (result == null) {
			return TickResult.NO_MORE_WORK;
		} else if (result == TickResult.NO_MORE_WORK) {
			setCurrentStrategy(helper, null);
			return TickResult.TICK_AGAIN;
		} else if (result == TickResult.TICK_AGAIN) {
			goodPause = true;
		}
		return result;
	}

	private TickResult strategyTick(AIHelper helper, boolean goodTimeToPause) {
		for (final AIStrategy s : strategies) {
			if (s == currentStrategy) {
				return s.gameTick(helper);
			} else if ((goodTimeToPause || s.takesOverAnyTime())
					&& s.checkShouldTakeOver(helper)) {
				setCurrentStrategy(helper, s);
				return s.gameTick(helper);
			}
		}
		return null;
	}

	private void setCurrentStrategy(AIHelper helper, AIStrategy s) {
		if (currentStrategy != null) {
			currentStrategy.setActive(false, helper);
		}
		currentStrategy = s;
		if (currentStrategy != null) {
			currentStrategy.setActive(true, helper);
		}
	}

	public void addStrategy(AIStrategy strategy) {
		if (strategy.isActive()) {
			throw new IllegalArgumentException(
					"Attempting to add an active strategy to this stack.");
		} else if (strategies.contains(strategy)) {
			throw new IllegalArgumentException("Added a strategy twice.");
		}
		strategies.add(strategy);
	}

	public void pause(AIHelper helper) {
		setCurrentStrategy(helper, null);
		this.paused = true;
	}

	public void resume(AIHelper helper) {
		this.paused = false;
	}

	public void shutdown() {
	}

	public AIStrategy getCurrentStrategy() {
		return currentStrategy;
	}

	public boolean couldTakeOver(AIHelper helper) {
		for (AIStrategy s : strategies) {
			if (s.checkShouldTakeOver(helper)) {
				return true;
			}
		}
		return false;
	}

	public List<AIStrategy> getStrategies() {
		return Collections.unmodifiableList(strategies);
	}
}
