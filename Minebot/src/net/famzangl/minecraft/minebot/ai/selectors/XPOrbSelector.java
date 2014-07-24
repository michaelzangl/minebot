package net.famzangl.minecraft.minebot.ai.selectors;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityXPOrb;

public final class XPOrbSelector implements IEntitySelector {
	@Override
	public boolean isEntityApplicable(Entity e) {
		return e instanceof EntityXPOrb;
	}
}