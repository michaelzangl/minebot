package net.famzangl.minecraft.minebot.ai.strategy;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;

public abstract class CloseEntityActionStrategy extends ValueActionStrategy {
	@Override
	protected double getValue(final AIHelper helper) {
		final Entity closest = helper.getClosestEntity(50,
				new IEntitySelector() {
					@Override
					public boolean isEntityApplicable(Entity player) {
						return matches(helper, player);
					}

				});
		return closest == null ? Double.MAX_VALUE : closest
				.getDistanceToEntity(helper.getMinecraft().thePlayer);
	}

	protected abstract boolean matches(AIHelper helper, Entity player);
}
