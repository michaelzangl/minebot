package net.famzangl.minecraft.minebot.ai.strategy;

import net.famzangl.minecraft.minebot.ai.AIHelper;

/**
 * Run a whole stack of strategies.
 * 
 * @author michael
 * 
 */
public class StackStrategy extends AIStrategy {

	private final StrategyStack stack;

	public StackStrategy(StrategyStack stack) {
		super();
		this.stack = stack;
	}

	@Override
	protected TickResult onGameTick(AIHelper helper) {
		if (stack.gameTick(helper)) {
			return TickResult.TICK_HANDLED;
		} else {
			return TickResult.NO_MORE_WORK;
		}
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
	public String getDescription() {
		AIStrategy current = stack.getCurrentStrategy();
		return current != null ? current.getDescription() : "Multiple tasks.";
	}
}
