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
import net.famzangl.minecraft.minebot.ai.selectors.AndSelector;
import net.famzangl.minecraft.minebot.ai.selectors.ColorSelector;
import net.famzangl.minecraft.minebot.ai.selectors.FeedableSelector;
import net.famzangl.minecraft.minebot.ai.selectors.FilterFeedingItem;
import net.famzangl.minecraft.minebot.ai.selectors.OrSelector;
import net.famzangl.minecraft.minebot.ai.selectors.XPOrbSelector;
import net.famzangl.minecraft.minebot.ai.task.FaceAndInteractTask;
import net.famzangl.minecraft.minebot.ai.task.TaskOperations;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;

import com.google.common.base.Predicate;

public class FeedAnimalsStrategy extends TaskStrategy {

	private static final int DISTANCE = 20;
	private final EnumDyeColor color;

	public FeedAnimalsStrategy() {
		this(null);
	}

	public FeedAnimalsStrategy(EnumDyeColor color) {
		this.color = color;
	}

	@Override
	public void searchTasks(AIHelper helper) {
		feedWithFood(helper);
	}

	private void feedWithFood(AIHelper helper) {
		Predicate<Entity> selector = new FeedableSelector(helper);
		if (color != null) {
			selector = new AndSelector(selector, new ColorSelector(color));
		}

		final Predicate<Entity> collect = new XPOrbSelector();

		final Entity found = helper.getClosestEntity(DISTANCE, new OrSelector(
				selector, collect));

		if (found != null) {
			addTask(new FaceAndInteractTask(found, selector) {
				@Override
				public void runTick(AIHelper h, TaskOperations o) {
					h.selectCurrentItem(new ItemFilter() {
						@Override
						public boolean matches(ItemStack itemStack) {
							return itemStack == null
									|| itemStack.getItem() == null
									|| !(itemStack.getItem() == Items.wheat
											|| itemStack.getItem() == Items.carrot || itemStack
											.getItem() instanceof ItemSeeds);
						}
					});
					super.runTick(h, o);
				}

				@Override
				protected void doInteractWithCurrent(AIHelper h) {
					final Entity over = h.getObjectMouseOver().entityHit;
					if (over instanceof EntityAnimal
							&& h.selectCurrentItem(new FilterFeedingItem(
									(EntityAnimal) over))) {
						super.doInteractWithCurrent(h);
					} else if (found == over) {
						interacted = true;
					}
				}
			});
		}
	}

	@Override
	public String getDescription(AIHelper helper) {
		return "Feeding";
	}
}