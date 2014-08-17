package net.famzangl.minecraft.minebot.ai.selectors;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityAnimal;

public final class FeedableSelector implements IEntitySelector {
	private final AIHelper helper;

	public FeedableSelector(AIHelper helper) {
		this.helper = helper;
	}

	@Override
	public boolean isEntityApplicable(Entity e) {
		if (!(e instanceof EntityAnimal)) {
			return false;
		}
		return helper.canSelectItem(new FilterFeedingItem((EntityAnimal) e));
	}
}