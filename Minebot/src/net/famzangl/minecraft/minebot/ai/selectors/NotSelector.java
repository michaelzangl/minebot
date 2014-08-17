package net.famzangl.minecraft.minebot.ai.selectors;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;

public class NotSelector implements IEntitySelector {

	private final IEntitySelector selector;

	public NotSelector(IEntitySelector selector) {
		this.selector = selector;

	}

	@Override
	public boolean isEntityApplicable(Entity var1) {
		return !selector.isEntityApplicable(var1);
	}

}
