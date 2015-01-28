package net.famzangl.minecraft.minebot.ai.selectors;

import com.google.common.base.Predicate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.item.EnumDyeColor;

public final class ColorSelector implements Predicate<Entity> {
	private final EnumDyeColor color;

	public ColorSelector(EnumDyeColor color) {
		super();
		this.color = color;
	}

	@Override
	public boolean apply(Entity var1) {
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