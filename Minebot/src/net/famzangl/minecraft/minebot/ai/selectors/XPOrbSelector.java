package net.famzangl.minecraft.minebot.ai.selectors;

import com.google.common.base.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityXPOrb;

public final class XPOrbSelector implements Predicate<Entity> {
	@Override
	public boolean apply(Entity e) {
		return e instanceof EntityXPOrb;
	}
}