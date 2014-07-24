package net.famzangl.minecraft.minebot.ai.selectors;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityWolf;

public final class ColorSelector implements IEntitySelector {
	private final int color;

	public ColorSelector(int color) {
		super();
		this.color = color;
	}

	@Override
	public boolean isEntityApplicable(Entity var1) {
		return var1 instanceof EntityWolf
				&& ((EntityWolf) var1).getCollarColor() == color;
	}
}