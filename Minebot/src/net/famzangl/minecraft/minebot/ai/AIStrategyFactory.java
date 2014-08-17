package net.famzangl.minecraft.minebot.ai;

import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy;

public interface AIStrategyFactory {
	public AIStrategy produceStrategy(AIHelper helper);
}
