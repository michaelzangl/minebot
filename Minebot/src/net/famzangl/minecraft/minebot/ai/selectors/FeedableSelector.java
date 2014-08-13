package net.famzangl.minecraft.minebot.ai.selectors;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.item.ItemStack;

public final class FeedableSelector implements IEntitySelector {
	private final ItemStack currentItem;

	public FeedableSelector(ItemStack currentItem) {
		this.currentItem = currentItem;
	}

	@Override
	public boolean isEntityApplicable(Entity e) {
		if (!(e instanceof EntityAnimal)) {
			return false;
		}
		final EntityAnimal animal = (EntityAnimal) e;
		return animal.isBreedingItem(currentItem) && !animal.isInLove()
				&& (animal.getGrowingAge() == 0 || isHungryWolf(animal)) && animal.getHealth() > 0;
	}

	private boolean isHungryWolf(EntityAnimal animal) {
		return animal instanceof EntityWolf && animal.getHealth() < animal.getMaxHealth();
	}
}