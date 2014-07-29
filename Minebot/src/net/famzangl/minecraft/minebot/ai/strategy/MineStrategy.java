package net.famzangl.minecraft.minebot.ai.strategy;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.AIStrategy;
import net.famzangl.minecraft.minebot.ai.AIStrategyFactory;
import net.famzangl.minecraft.minebot.ai.path.MineBySettingsPathFinder;
import net.famzangl.minecraft.minebot.ai.path.MineSinglePathFinder;
import net.minecraft.block.Block;

public class MineStrategy implements AIStrategyFactory {

	@Override
	public AIStrategy produceStrategy(AIHelper helper) {
		return new PathFinderStrategy(new MineBySettingsPathFinder(helper),
				"Mining ores");
	}

	public AIStrategy produceStrategy(AIHelper helper, Block blockName) {
		return new PathFinderStrategy(new MineSinglePathFinder(helper,
				blockName), "Mining " + blockName.getLocalizedName());
	}

}
