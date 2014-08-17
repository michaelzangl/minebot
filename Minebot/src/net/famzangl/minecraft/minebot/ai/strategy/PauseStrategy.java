package net.famzangl.minecraft.minebot.ai.strategy;

import net.famzangl.minecraft.minebot.ai.AIHelper;

public class PauseStrategy extends AIStrategy {

	private int leftTicks;

	public PauseStrategy(int seconds) {
		super();
		this.leftTicks = seconds * 20;
	}

	@Override
	public boolean checkShouldTakeOver(AIHelper helper) {
		return leftTicks > 0;
	}

	@Override
	protected TickResult onGameTick(AIHelper helper) {
		if (leftTicks > 0) {
			leftTicks--;
			return TickResult.TICK_HANDLED;
		}
		return TickResult.NO_MORE_WORK;
	}

}
