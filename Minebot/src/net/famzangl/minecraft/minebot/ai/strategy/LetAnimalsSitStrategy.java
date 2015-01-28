package net.famzangl.minecraft.minebot.ai.strategy;

import java.util.ArrayList;
import java.util.List;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.ItemFilter;
import net.famzangl.minecraft.minebot.ai.animals.AnimalyType;
import net.famzangl.minecraft.minebot.ai.selectors.AndSelector;
import net.famzangl.minecraft.minebot.ai.selectors.ColorSelector;
import net.famzangl.minecraft.minebot.ai.selectors.IsSittingSelector;
import net.famzangl.minecraft.minebot.ai.selectors.NotSelector;
import net.famzangl.minecraft.minebot.ai.selectors.OneOfListSelector;
import net.famzangl.minecraft.minebot.ai.task.FaceAndInteractTask;

import com.google.common.base.Predicate;

import net.minecraft.entity.Entity;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;

/**
 * Lets all wolves that are owned by the current player either sit or stand.
 * 
 * @author michael
 * 
 */
public class LetAnimalsSitStrategy extends TaskStrategy {

	private final class NoWolfFoodFilter implements ItemFilter {
		@Override
		public boolean matches(ItemStack itemStack) {
			return !(itemStack != null && interactsWithWolf(itemStack.getItem()));
		}

		private boolean interactsWithWolf(Item item) {
			return item instanceof ItemFood
					&& ((ItemFood) item).isWolfsFavoriteMeat()
					|| item instanceof ItemDye;
		}
	}

	private static final int DISTANCE = 20;
	private final EnumDyeColor color;
	private final boolean shouldSit;
	private final List<Entity> handled = new ArrayList<Entity>();

	/**
	 * Creates a new strategy.
	 * 
	 * @param wolf
	 *            Always needs to be wolf (for now)
	 * @param shouldSit
	 *            <code>true</code> if they all should sit, <code>false</code>
	 *            otherwise
	 * @param color
	 *            A color selector or -1 for no color.
	 */
	public LetAnimalsSitStrategy(AnimalyType wolf, boolean shouldSit, EnumDyeColor color) {
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

		Predicate<Entity> selector = new AndSelector(new IsSittingSelector(
				!shouldSit, helper.getMinecraft().thePlayer), new NotSelector(
				new OneOfListSelector(handled)));
		if (color != null) {
			selector = new AndSelector(selector, new ColorSelector(color));
		}

		final Entity found = helper.getClosestEntity(DISTANCE, selector);

		if (found != null) {
			addTask(new FaceAndInteractTask(found, selector) {
				@Override
				protected void doInteractWithCurrent(AIHelper h) {
					super.doInteractWithCurrent(h);
					handled.add(found);
				}
			});
		}
	}

	@Override
	public String getDescription(AIHelper helper) {
		return shouldSit ? "Let them sit" : "Let them go";
	}
}
