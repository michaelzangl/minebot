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
package net.famzangl.minecraft.minebot.ai.strategy;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.ItemFilter;
import net.famzangl.minecraft.minebot.ai.animals.AnimalyType;
import net.famzangl.minecraft.minebot.ai.selectors.ColorSelector;
import net.famzangl.minecraft.minebot.ai.selectors.IsSittingSelector;
import net.famzangl.minecraft.minebot.ai.selectors.OneOfListSelector;
import net.famzangl.minecraft.minebot.ai.task.FaceAndInteractTask;
import net.minecraft.entity.Entity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

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
			return item.getFood() != null
					&& item.getFood().isMeat()
					|| item instanceof DyeItem;
		}
	}

	private static final int DISTANCE = 20;
	private final DyeColor color;
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
	public LetAnimalsSitStrategy(AnimalyType wolf, boolean shouldSit, DyeColor color) {
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

		Predicate<Entity> selector = new IsSittingSelector(
				!shouldSit, helper.getMinecraft().player).and(
				new OneOfListSelector(handled).negate());
		if (color != null) {
			selector = selector.and(new ColorSelector(color));
		}

		final Entity found = helper.getClosestEntity(DISTANCE, selector);

		if (found != null) {
			addTask(new FaceAndInteractTask(found, selector) {
				@Override
				protected void doInteractWithCurrent(AIHelper aiHelper) {
					super.doInteractWithCurrent(aiHelper);
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
