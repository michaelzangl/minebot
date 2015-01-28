package net.famzangl.minecraft.minebot.ai.strategy;

import com.google.common.base.Predicate;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.minecraft.entity.Entity;

public abstract class CloseEntityActionStrategy extends ValueActionStrategy {
	@Override
	protected double getValue(final AIHelper helper) {
		final Entity closest = helper.getClosestEntity(50,
				new Predicate<Entity>() {
					@Override
					public boolean apply(Entity player) {
						return matches(helper, player);
					}

				});
		return closest == null ? Double.MAX_VALUE : closest
				.getDistanceToEntity(helper.getMinecraft().thePlayer);
	}

	protected abstract boolean matches(AIHelper helper, Entity player);
}
