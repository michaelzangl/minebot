package net.famzangl.minecraft.minebot.ai.strategy;

import net.famzangl.minecraft.minebot.ai.AIHelper;

public class StopOnConditionStrategy extends AIStrategy {
	public interface StopCondition {
		boolean shouldStop(AIHelper helper);
	}

	private final StopCondition condition;
	private final boolean force;
	private final String name;

	public StopOnConditionStrategy(StopCondition condition, boolean force, String name) {
		super();
		this.condition = condition;
		this.force = force;
		this.name = name;
	}

	@Override
	public boolean takesOverAnyTime() {
		return force;
	}

	@Override
	public boolean checkShouldTakeOver(AIHelper helper) {
		return condition.shouldStop(helper);
	}

	@Override
	protected TickResult onGameTick(AIHelper helper) {
		if (condition.shouldStop(helper)) {
			return TickResult.ABORT;
		} else {
			return TickResult.NO_MORE_WORK;
		}
	}

	@Override
	public String getDescription(AIHelper helper) {
		return "Stop on " + name;
	}

	@Override
	public String toString() {
		return "StopOnConditionStrategy [condition=" + condition + ", force="
				+ force + "]";
	}

}
