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