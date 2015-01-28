package net.famzangl.minecraft.minebot.ai.selectors;

import com.google.common.base.Predicate;

import net.minecraft.entity.Entity;

public class AndSelector implements Predicate<Entity> {

	private final Predicate<Entity>[] selectors;

	public AndSelector(Predicate<Entity>... selectors) {
		this.selectors = selectors;
	}

	@Override
	public boolean apply(Entity var1) {
		for (final Predicate<Entity> s : selectors) {
			if (!s.apply(var1)) {
				return false;
			}
		}
		return true;
	}

}
