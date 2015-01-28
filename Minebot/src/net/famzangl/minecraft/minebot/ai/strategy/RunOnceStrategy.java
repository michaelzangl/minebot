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
			this.singleRun(helper);
		}
		return TickResult.NO_MORE_WORK;
	}

	protected abstract void singleRun(AIHelper helper);

	@Override
	public boolean checkShouldTakeOver(AIHelper helper) {
		return !wasRun;
	}
}
