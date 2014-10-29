package net.famzangl.minecraft.minebot.ai.selectors;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityWolf;

public final class ColorSelector implements IEntitySelector {
	private final int color;

	public ColorSelector(int color) {
		super();
		this.color = color;
	}

	@Override
	public boolean isEntityApplicable(Entity var1) {
		if (var1 instanceof EntityWolf) {
			return ((EntityWolf) var1).getCollarColor() == color;
		} else if (var1 instanceof EntitySheep) {
			return ((EntitySheep) var1).getFleeceColor() == color;
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return "ColorSelector [color=" + color + "]";
	}

}