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
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.item.ItemStack;

public final class FilterFeedingItem implements ItemFilter {
	private final AnimalEntity animal;

	public FilterFeedingItem(AnimalEntity animal) {
		this.animal = animal;
	}

	@Override
	public boolean matches(ItemStack itemStack) {
		return isFeedingItem(animal, itemStack);
	}

	private boolean isFeedingItem(final AnimalEntity animal,
			ItemStack currentItem) {
		return currentItem != null && currentItem.getItem() != null
				&& animal.isBreedingItem(currentItem) && !animal.isInLove()
				&& (animal.getGrowingAge() == 0 || isHungryWolf(animal))
				&& animal.getHealth() > 0;
	}

	private boolean isHungryWolf(AnimalEntity animal) {
		return animal instanceof WolfEntity
				&& animal.getHealth() < animal.getMaxHealth();
	}
}