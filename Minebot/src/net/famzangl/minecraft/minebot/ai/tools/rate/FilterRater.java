package net.famzangl.minecraft.minebot.ai.tools.rate;

import net.famzangl.minecraft.minebot.ai.ItemFilter;
import net.famzangl.minecraft.minebot.ai.path.world.BlockFloatMap;
import net.minecraft.item.ItemStack;

public class FilterRater extends Rater {
	protected final ItemFilter filter;

	public FilterRater(ItemFilter filter, String name, BlockFloatMap values) {
		super(name, values);
		this.filter = filter;
	}

	@Override
	protected boolean isAppleciable(ItemStack item, int forBlockAndMeta) {
		return filter.matches(item);
	}
}