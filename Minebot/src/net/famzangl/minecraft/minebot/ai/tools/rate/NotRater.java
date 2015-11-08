package net.famzangl.minecraft.minebot.ai.tools.rate;

import net.famzangl.minecraft.minebot.ai.path.world.BlockFloatMap;
import net.minecraft.item.ItemStack;

public class NotRater extends Rater {
	private Rater rater;

	public NotRater(BlockFloatMap values, Rater rater) {
		super("!" + rater.getName(), values);
		this.rater = rater;
	}
	@Override
	protected double getPow(ItemStack item, int forBlockAndMeta) {
		return rater.getPow(item, forBlockAndMeta);
	}


	@Override
	protected boolean isAppleciable(ItemStack item, int forBlockAndMeta) {
		return !rater.isAppleciable(item, forBlockAndMeta);
	}
}