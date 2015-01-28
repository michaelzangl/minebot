package net.famzangl.minecraft.minebot.ai.selectors;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import com.google.common.base.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityAnimal;

public final class FeedableSelector implements Predicate<Entity> {
	private final AIHelper helper;

	public FeedableSelector(AIHelper helper) {
		this.helper = helper;
	}

	@Override
	public boolean apply(Entity e) {
		if (!(e instanceof EntityAnimal)) {
			return false;
		}
		return helper.canSelectItem(new FilterFeedingItem((EntityAnimal) e));
	}
}