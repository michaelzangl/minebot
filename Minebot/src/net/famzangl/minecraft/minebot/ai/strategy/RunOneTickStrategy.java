package net.famzangl.minecraft.minebot.ai.strategy;

import net.famzangl.minecraft.minebot.ai.AIHelper;

/**
 * Same as the run once strategy but this strategy at least takes one tick to execute.
 * @author Michael Zangl
 *
 */
public abstract class RunOneTickStrategy extends RunOnceStrategy {

	protected TickResult doSingleRun(AIHelper helper) {
		this.singleRun(helper);
		return TickResult.TICK_HANDLED;
	}
}
