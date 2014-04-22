package net.famzangl.minecraft.minebot.ai.animals;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.AIStrategy;
import net.famzangl.minecraft.minebot.ai.AIStrategyFactory;

public class Fish implements AIStrategyFactory {

	@Override
	public AIStrategy produceStrategy(AIHelper helper) {
		return new FishStrategy();
	}

}
