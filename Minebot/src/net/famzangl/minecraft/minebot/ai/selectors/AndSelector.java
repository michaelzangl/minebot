package net.famzangl.minecraft.minebot.ai.selectors;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;

public class AndSelector implements IEntitySelector {

	private final IEntitySelector[] selectors;

	public AndSelector(IEntitySelector... selectors) {
		this.selectors = selectors;
	}

	@Override
	public boolean isEntityApplicable(Entity var1) {
		for (IEntitySelector s : selectors) {
			if (!s.isEntityApplicable(var1)) {
				return false;
			}
		}
		return true;
	}

}
