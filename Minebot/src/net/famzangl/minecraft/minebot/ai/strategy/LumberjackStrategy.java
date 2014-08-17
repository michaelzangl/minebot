package net.famzangl.minecraft.minebot.ai.strategy;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.AIStrategyFactory;
import net.famzangl.minecraft.minebot.ai.path.TreePathFinder;

public class LumberjackStrategy implements AIStrategyFactory {

	@Override
	public AIStrategy produceStrategy(AIHelper helper) {
		return ValueActionStrategy.makeSafe(new PathFinderStrategy(
				new TreePathFinder(null), "Getting some wood"));
	}
}
