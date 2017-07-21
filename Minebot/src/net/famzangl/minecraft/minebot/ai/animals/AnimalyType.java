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

import java.util.stream.Stream;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityPolarBear;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityDonkey;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityLlama;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityParrot;
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
	POLARBEAR(EntityPolarBear.class, false),
	SHEEP(EntitySheep.class),
	WOLF(EntityWolf.class, false),
	OCELOT(EntityOcelot.class),
	RABBIT(EntityRabbit.class),
	HORSE(EntityHorse.class),
	DONKEY(EntityDonkey.class),
	PARROT(EntityParrot.class),
	LLAMA(EntityLlama.class, false);

	private Class<?> animalClass;
	private boolean inDefaultList;

	private AnimalyType(Class<?> animalClass) {
		this(animalClass, true);
	}
	
	private AnimalyType(Class<?> animalClass, boolean inDefaultList) {
		this.animalClass = animalClass;
		this.inDefaultList = inDefaultList;
	}

	public boolean hasAnimalClass(Entity e) {
		if (animalClass == null) {
			return Stream.of(values()).filter(t -> t.inDefaultList).anyMatch(t -> t.animalClass == e.getClass());
		} else {
			return e.getClass() == animalClass;
		}
	}

}
