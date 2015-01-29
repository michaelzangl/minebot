/*******************************************************************************
 * This file is part of Minebot.
 *
 * Minebot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Minebot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Minebot.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package net.famzangl.minecraft.minebot.ai.animals;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityWolf;

/**
 * A list of animal types we can filter for.
 * 
 * @author michael
 * 
 */
public enum AnimalyType {
	ANY(null),
	COW(EntityCow.class),
	CHICKEN(EntityChicken.class),
	PIG(EntityPig.class),
	SHEEP(EntitySheep.class),
	WOLF(EntityWolf.class),
	OCELOT(EntityOcelot.class),
	RABBIT(EntityRabbit.class);

	private Class<?> animalClass;

	private AnimalyType(Class<?> animalClass) {
		this.animalClass = animalClass;
	}

	public boolean hasAnimalClass(Entity e) {
		return animalClass == null ? e instanceof EntityAnimal
				: e.getClass() == animalClass;
	}

}
