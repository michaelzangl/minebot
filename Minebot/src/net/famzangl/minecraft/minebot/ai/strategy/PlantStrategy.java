package net.famzangl.minecraft.minebot.ai.strategy;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.AIStrategyFactory;
import net.famzangl.minecraft.minebot.ai.path.PlantPathFinder;
import net.famzangl.minecraft.minebot.ai.path.PlantPathFinder.PlantType;

public class PlantStrategy implements AIStrategyFactory {

	@Override
	public AIStrategy produceStrategy(AIHelper helper) {
		return ValueActionStrategy.makeSafe(new PathFinderStrategy(
				new PlantPathFinder(PlantType.ANY), "Planting"));
	}

}
