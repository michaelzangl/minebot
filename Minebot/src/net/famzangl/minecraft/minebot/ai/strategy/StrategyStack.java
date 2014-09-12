package net.famzangl.minecraft.minebot.ai.strategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy.TickResult;

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
			final TickResult result = strategyTick(helper, currentStrategy == null || goodPause);
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
