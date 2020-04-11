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
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.PolarBearEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.passive.horse.DonkeyEntity;
import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraft.entity.passive.horse.LlamaEntity;

import java.util.stream.Stream;

/**
 * A list of animal types we can filter for.
 * 
 * @author michael
 * 
 */
public enum AnimalyType {
	ANY(null),
	COW(CowEntity.class),
	CHICKEN(ChickenEntity.class),
	PIG(PigEntity.class),
	POLARBEAR(PolarBearEntity.class, false),
	SHEEP(SheepEntity.class),
	WOLF(WolfEntity.class, false),
	OCELOT(OcelotEntity.class),
	RABBIT(RabbitEntity.class),
	HORSE(HorseEntity.class),
	DONKEY(DonkeyEntity.class),
	PARROT(ParrotEntity.class),
	LLAMA(LlamaEntity.class, false);

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
