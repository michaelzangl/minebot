package net.famzangl.minecraft.minebot.ai.strategy;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.ItemFilter;
import net.famzangl.minecraft.minebot.ai.selectors.AndSelector;
import net.famzangl.minecraft.minebot.ai.selectors.ColorSelector;
import net.famzangl.minecraft.minebot.ai.selectors.ItemSelector;
import net.famzangl.minecraft.minebot.ai.selectors.OrSelector;
import net.famzangl.minecraft.minebot.ai.task.FaceAndInteractTask;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;

public class ShearStrategy extends TaskStrategy {
	private static final int DISTANCE = 20;
	private final int color;

	public ShearStrategy(int color) {
		this.color = color;
	}

	private final class ShearsFilter implements ItemFilter {
		@Override
		public boolean matches(ItemStack itemStack) {
			return itemStack != null
					&& itemStack.getItem() instanceof ItemShears;
		}
	}

	private final class SheepSelector implements IEntitySelector {
		@Override
		public boolean isEntityApplicable(Entity var1) {
			return var1 instanceof EntitySheep
					&& ((EntitySheep) var1).isShearable(null, null, 0, 0, 0);
		}
	}

	@Override
	public void searchTasks(AIHelper helper) {
		if (!helper.selectCurrentItem(new ShearsFilter())) {
			return;
		}

		IEntitySelector selector = new SheepSelector();

		if (color >= 0) {
			selector = new AndSelector(selector, new ColorSelector(color));
		}

		final Entity found = helper.getClosestEntity(DISTANCE, new OrSelector(
				selector, new ItemSelector()));

		if (found != null) {
			addTask(new FaceAndInteractTask(found, selector));
		}
	}

	@Override
	public String getDescription() {
		return "Shearing...";
	}

}
