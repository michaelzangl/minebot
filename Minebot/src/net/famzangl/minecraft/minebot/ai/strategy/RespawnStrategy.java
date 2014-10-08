package net.famzangl.minecraft.minebot.ai.strategy;

import net.famzangl.minecraft.minebot.ai.AIHelper;

public class RespawnStrategy extends AIStrategy{
	@Override
	protected TickResult onGameTick(AIHelper helper) {
		if (!helper.isAlive()) {
			helper.respawn();
			return TickResult.TICK_HANDLED;
		} else {
			return TickResult.NO_MORE_WORK;
		}
	}
}
