package net.famzangl.minecraft.minebot.ai.selectors;

import net.minecraft.entity.Entity;

import com.google.common.base.Predicate;

public class NotSelector implements Predicate<Entity> {

	private final Predicate<Entity> selector;

	public NotSelector(Predicate<Entity> selector) {
		this.selector = selector;

	}

	@Override
	public boolean apply(Entity var1) {
		return !selector.apply(var1);
	}

}
