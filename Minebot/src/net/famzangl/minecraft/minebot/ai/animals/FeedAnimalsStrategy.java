package net.famzangl.minecraft.minebot.ai.animals;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.AIStrategy;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.init.Items;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;

public class FeedAnimalsStrategy implements AIStrategy {

	private static final int DISTANCE = 20;

	@Override
	public void searchTasks(AIHelper helper) {
		ItemStack currentItem = helper.getMinecraft().thePlayer.inventory
				.getCurrentItem();
		if (currentItem != null
				&& (currentItem.getItem() instanceof ItemFood
						|| currentItem.getItem() == Items.wheat || currentItem
							.getItem() instanceof ItemSeeds)) {
			feedWithFood(helper, currentItem);
		}
	}

	private void feedWithFood(AIHelper helper, final ItemStack currentItem) {
		final IEntitySelector selector = new IEntitySelector() {
			@Override
			public boolean isEntityApplicable(Entity e) {
				if (!(e instanceof EntityAnimal)) {
					return false;
				}
				EntityAnimal animal = (EntityAnimal) e;
				return animal.isBreedingItem(currentItem) && !animal.isInLove()
						&& animal.getGrowingAge() == 0
						&& animal.getHealth() > 0;
			}
		};

		IEntitySelector collect = new IEntitySelector() {
			@Override
			public boolean isEntityApplicable(Entity e) {
				return e instanceof EntityXPOrb
						|| selector.isEntityApplicable(e);
			}
		};
		Entity found = helper.getClosestEntity(DISTANCE, collect);

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