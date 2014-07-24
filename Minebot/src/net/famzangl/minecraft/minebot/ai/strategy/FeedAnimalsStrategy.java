package net.famzangl.minecraft.minebot.ai.strategy;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.AIStrategy;
import net.famzangl.minecraft.minebot.ai.selectors.AndSelector;
import net.famzangl.minecraft.minebot.ai.selectors.OrSelector;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.famzangl.minecraft.minebot.ai.task.FaceAndInteractTask;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.init.Items;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;

public class FeedAnimalsStrategy implements AIStrategy {

	private final class XPOrbSelector implements IEntitySelector {
		@Override
		public boolean isEntityApplicable(Entity e) {
			return e instanceof EntityXPOrb;
		}
	}

	private final class FeedableSelector implements IEntitySelector {
		private final ItemStack currentItem;

		private FeedableSelector(ItemStack currentItem) {
			this.currentItem = currentItem;
		}

		@Override
		public boolean isEntityApplicable(Entity e) {
			if (!(e instanceof EntityAnimal)) {
				return false;
			}
			final EntityAnimal animal = (EntityAnimal) e;
			return animal.isBreedingItem(currentItem) && !animal.isInLove()
					&& animal.getGrowingAge() == 0 && animal.getHealth() > 0;
		}
	}

	private final class ColorSelector implements IEntitySelector {
		private final int color;

		public ColorSelector(int color) {
			super();
			this.color = color;
		}

		@Override
		public boolean isEntityApplicable(Entity var1) {
			return var1 instanceof EntityWolf
					&& ((EntityWolf) var1).getCollarColor() == color;
		}
	}

	private static final int DISTANCE = 20;
	private final int color;

	public FeedAnimalsStrategy() {
		this (-1);
	}
	
	public FeedAnimalsStrategy(int color) {
		this.color = color;
	}

	@Override
	public void searchTasks(AIHelper helper) {
		final ItemStack currentItem = helper.getMinecraft().thePlayer.inventory
				.getCurrentItem();
		if (currentItem != null
				&& (currentItem.getItem() instanceof ItemFood
						|| currentItem.getItem() == Items.wheat || currentItem
							.getItem() instanceof ItemSeeds)) {
			feedWithFood(helper, currentItem);
		}
	}

	private void feedWithFood(AIHelper helper, final ItemStack currentItem) {
		IEntitySelector selector = new FeedableSelector(currentItem);
		if (color >= 0) {
			selector = new AndSelector(selector, new ColorSelector(color));
		}

		final IEntitySelector collect = new XPOrbSelector();

		final Entity found = helper.getClosestEntity(DISTANCE, new OrSelector(
				selector, collect));

		if (found != null) {
			helper.addTask(new FaceAndInteractTask(found, selector));
		}
	}

	@Override
	public String getDescription() {
		return "Feeding";
	}

	@Override
	public AITask getOverrideTask(AIHelper helper) {
		return null;
	}

}