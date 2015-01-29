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
package net.famzangl.minecraft.minebot.ai.selectors;

import net.famzangl.minecraft.minebot.ai.ItemFilter;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.item.ItemStack;

public final class FilterFeedingItem implements ItemFilter {
	private final EntityAnimal animal;

	public FilterFeedingItem(EntityAnimal animal) {
		this.animal = animal;
	}

	@Override
	public boolean matches(ItemStack itemStack) {
		return isFeedingItem(animal, itemStack);
	}

	private boolean isFeedingItem(final EntityAnimal animal,
			ItemStack currentItem) {
		return currentItem != null && currentItem.getItem() != null
				&& animal.isBreedingItem(currentItem) && !animal.isInLove()
				&& (animal.getGrowingAge() == 0 || isHungryWolf(animal))
				&& animal.getHealth() > 0;
	}

	private boolean isHungryWolf(EntityAnimal animal) {
		return animal instanceof EntityWolf
				&& animal.getHealth() < animal.getMaxHealth();
	}
}