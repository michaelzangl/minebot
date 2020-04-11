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

import com.google.common.base.Predicate;
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
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;

public class FeedAnimalsStrategy extends TaskStrategy {

	private static final int DISTANCE = 20;
	private final DyeColor color;

	public FeedAnimalsStrategy() {
		this(null);
	}

	public FeedAnimalsStrategy(DyeColor color) {
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
				public void runTick(AIHelper aiHelper, TaskOperations taskOperations) {
					aiHelper.selectCurrentItem(new ItemFilter() {
						@Override
						public boolean matches(ItemStack itemStack) {
							return itemStack == null
									|| itemStack.isEmpty()
									|| !(itemStack.getItem() == Items.WHEAT
											|| itemStack.getItem() == Items.CARROT
									|| itemStack.getItem() == Items.BEETROOT_SEEDS
									|| itemStack.getItem() == Items.MELON_SEEDS
									|| itemStack.getItem() == Items.PUMPKIN_SEEDS
									|| itemStack.getItem() == Items.WHEAT_SEEDS);
						}
					});
					super.runTick(aiHelper, taskOperations);
				}

				@Override
				protected void doInteractWithCurrent(AIHelper aiHelper) {
					RayTraceResult rayTrace = aiHelper.getObjectMouseOver();
					if (rayTrace instanceof EntityRayTraceResult) {
						Entity over = ((EntityRayTraceResult) rayTrace).getEntity();
						if (over instanceof AnimalEntity && aiHelper.selectCurrentItem(new FilterFeedingItem((AnimalEntity) over))) {
							super.doInteractWithCurrent(aiHelper);
						} else if (found == over) {
							interacted = true;
						}
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