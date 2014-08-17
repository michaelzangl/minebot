package net.famzangl.minecraft.minebot.ai.strategy;

import net.famzangl.minecraft.minebot.ai.AIHelper;

public final class StopStrategy extends AIStrategy {

	@Override
	public String getDescription() {
		return "Stopping";
	}

	@Override
	protected TickResult onGameTick(AIHelper helper) {
		return TickResult.NO_MORE_WORK;
	}
}