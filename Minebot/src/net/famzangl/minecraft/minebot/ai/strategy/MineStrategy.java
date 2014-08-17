package net.famzangl.minecraft.minebot.ai.strategy;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.AIStrategyFactory;
import net.famzangl.minecraft.minebot.ai.path.MineBySettingsPathFinder;
import net.famzangl.minecraft.minebot.ai.path.MineSinglePathFinder;
import net.minecraft.block.Block;

public class MineStrategy implements AIStrategyFactory {

	@Override
	public AIStrategy produceStrategy(AIHelper helper) {
		return ValueActionStrategy.makeSafe(new PathFinderStrategy(
				new MineBySettingsPathFinder(helper.getLookDirection(), helper
						.getPlayerPosition().y), "Mining ores"));
	}

	public AIStrategy produceStrategy(AIHelper helper, Block blockName) {
		return ValueActionStrategy.makeSafe(new PathFinderStrategy(
				new MineSinglePathFinder(blockName, helper.getLookDirection(),
						helper.getPlayerPosition().y), "Mining "
						+ blockName.getLocalizedName()));
	}

}
