package net.famzangl.minecraft.minebot.ai.strategy;

import net.famzangl.minecraft.minebot.ai.AIHelper;

public class StopInStrategy extends TimeStrategy {

	private final int time;
	private final boolean force;

	public StopInStrategy(int seconds, boolean force) {
		super();
		this.force = force;
		this.time = seconds * 20;
	}

	@Override
	public boolean takesOverAnyTime() {
		return force;
	}

	@Override
	public boolean checkShouldTakeOver(AIHelper helper) {
		return getTimeElapsed(helper) > time;
	}

	@Override
	protected TickResult onGameTick(AIHelper helper) {
		if (getTimeElapsed(helper) > time) {
			return TickResult.ABORT;
		} else {
			return TickResult.NO_MORE_WORK;
		}
	}

	@Override
	public String getDescription(AIHelper helper) {
		int time = (int) ((this.time - getTimeElapsed(helper)) / 20);
		return "Stop in " + time + "s";
	}
}
