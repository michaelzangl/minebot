package net.famzangl.minecraft.minebot.ai.strategy;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.AIStrategy;
import net.famzangl.minecraft.minebot.ai.ItemFilter;
import net.famzangl.minecraft.minebot.ai.animals.AnimalyType;
import net.famzangl.minecraft.minebot.ai.selectors.AndSelector;
import net.famzangl.minecraft.minebot.ai.selectors.ColorSelector;
import net.famzangl.minecraft.minebot.ai.selectors.IsSittingSelector;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.famzangl.minecraft.minebot.ai.task.FaceAndInteractTask;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;

/**
 * Lets all wolves that are owned by the current player either sit or stand.
 * 
 * @author michael
 * 
 */
public class LetAnimalsSitStrategy implements AIStrategy {

	private final class NoWolfFoodFilter implements ItemFilter {
		@Override
		public boolean matches(ItemStack itemStack) {
			return !(itemStack != null
					&& ((itemStack.getItem() instanceof ItemFood) && ((ItemFood) itemStack
							.getItem()).isWolfsFavoriteMeat()) || itemStack
						.getItem() instanceof ItemDye);
		}
	}

	private static final int DISTANCE = 20;
	private final int color;
	private final boolean shouldSit;

	/**
	 * Creates a new strategy.
	 * @param wolf Always needs to be wolf (for now)
	 * @param shouldSit <code>true</code> if they all should sit, <code>false</code> otherwise
	 * @param color A color selector or -1 for no color.
	 */
	public LetAnimalsSitStrategy(AnimalyType wolf, boolean shouldSit, int color) {
		if (wolf != AnimalyType.WOLF) {
			throw new IllegalArgumentException();
		}
		this.shouldSit = shouldSit;
		this.color = color;
	}

	@Override
	public void searchTasks(AIHelper helper) {
		if (!helper.selectCurrentItem(new NoWolfFoodFilter())) {
			return;
		}

		IEntitySelector selector = new IsSittingSelector(!shouldSit,
				helper.getMinecraft().thePlayer);
		if (color >= 0) {
			selector = new AndSelector(selector, new ColorSelector(color));
		}

		final Entity found = helper.getClosestEntity(DISTANCE, selector);

		if (found != null) {
			helper.addTask(new FaceAndInteractTask(found, selector));
		}
	}

	@Override
	public String getDescription() {
		return shouldSit ? "Let them sit" : "Let them go";
	}

	@Override
	public AITask getOverrideTask(AIHelper helper) {
		return null;
	}

}
