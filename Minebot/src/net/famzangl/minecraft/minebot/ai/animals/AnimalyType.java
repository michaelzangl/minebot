package net.famzangl.minecraft.minebot.ai.animals;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;

public enum AnimalyType {
	ANY(null), COW(EntityCow.class), CHICKEN(EntityChicken.class), PIG(
			EntityPig.class), SHEEP(EntitySheep.class);

	private Class<?> animalClass;

	private AnimalyType(Class<?> animalClass) {
		this.animalClass = animalClass;
	}

	boolean hasAnimalClass(Entity e) {
		return animalClass == null ? e instanceof EntityAnimal
				: e.getClass() == animalClass;
	}

}
