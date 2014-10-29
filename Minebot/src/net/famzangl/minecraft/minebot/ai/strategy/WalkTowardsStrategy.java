package net.famzangl.minecraft.minebot.ai.strategy;

import net.famzangl.minecraft.minebot.ai.AIHelper;

public class WalkTowardsStrategy extends AIStrategy{
	private final double x;
	private final double z;

	public WalkTowardsStrategy(double x, double z) {
		this.x = x;
		this.z = z;
	}
	
	@Override
	public boolean checkShouldTakeOver(AIHelper helper) {
		return !helper.arrivedAt(x, z);
	}
	
	@Override
	protected TickResult onGameTick(AIHelper helper) {
		return helper.walkTowards(x, z, false) ? TickResult.NO_MORE_WORK : TickResult.TICK_HANDLED;
	}
}
