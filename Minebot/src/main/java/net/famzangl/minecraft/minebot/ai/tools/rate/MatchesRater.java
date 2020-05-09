package net.famzangl.minecraft.minebot.ai.tools.rate;

import net.famzangl.minecraft.minebot.ai.path.world.BlockFloatMap;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class MatchesRater extends Rater {
	public MatchesRater(String name, BlockFloatMap values) {
		super(name, values);
	}

	@Override
	protected boolean isAppleciable(ItemStack item, int forBlockAndMeta) {
		return item != null
				&& forBlockAndMeta >= 0
				&& item.getItem() != null
				&& item.getItem().canHarvestBlock(item,
					BlockSet.getStateById(forBlockAndMeta));
	}

	@Override
	public String toString() {
		return "MatchesRater [name=" + name + "]";
	}
}