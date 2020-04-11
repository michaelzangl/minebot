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
import net.famzangl.minecraft.minebot.ai.selectors.NotSelector;
import net.famzangl.minecraft.minebot.ai.selectors.OrSelector;
import net.famzangl.minecraft.minebot.ai.selectors.OwnTameableSelector;
import net.famzangl.minecraft.minebot.ai.task.FaceAndInteractTask;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;

public class TintStrategy extends TaskStrategy {
	private static final int DISTANCE = 20;
	private final DyeColor color;
	private final DyeColor current;
	private final TintType type;

	public TintStrategy(DyeColor color, TintType type, DyeColor current) {
		this.color = color;
		this.type = type;
		this.current = current;
	}

	private static final class SheepSelector implements Predicate<Entity> {
		@Override
		public boolean apply(Entity var1) {
			return var1 instanceof SheepEntity;
		}
	}

	private static final class WolfSelector extends OwnTameableSelector {
		private WolfSelector(ClientPlayerEntity owner) {
			super(owner);
		}

		@Override
		public boolean apply(Entity entity) {
			return entity instanceof WolfEntity && super.apply(entity);
		}
	}

	public static enum TintType {
		WOLF, SHEEP, ANY;

	}

	public static class DyeItemFilter implements ItemFilter {

		private final DyeColor color;

		public DyeItemFilter(DyeColor color) {
			this.color = color;
		}

		@Override
		public boolean matches(ItemStack itemStack) {
			// FIXME: Color id.
			return itemStack != null
					&& itemStack.getItem() != null
					&& itemStack.getItem() instanceof DyeItem
					&& (color == null || 15 - itemStack.getDamage() == color
							.ordinal());
		}

	}

	@Override
	public void searchTasks(AIHelper helper) {
		if (!helper.selectCurrentItem(new DyeItemFilter(color))) {
			return;
		}

		final ClientPlayerEntity owner = helper.getMinecraft().player;
		// FIXME: Check.
		final DyeColor holdingColor = DyeColor.values()[15 - owner.inventory
				.getCurrentItem().getDamage()];
		final Predicate<Entity> wolfSelector = new WolfSelector(owner);
		final Predicate<Entity> sheepSelector = new SheepSelector();

		Predicate<Entity> selector;
		switch (type) {
		case WOLF:
			selector = wolfSelector;
			break;
		case SHEEP:
			selector = sheepSelector;
			break;
		default:
			selector = new OrSelector(sheepSelector, wolfSelector);
		}
		if (current != null) {
			selector = new AndSelector(selector, new ColorSelector(current));
		}
		selector = new AndSelector(selector, new NotSelector(new ColorSelector(
				holdingColor)));

		final Entity found = helper.getClosestEntity(DISTANCE, selector);

		if (found != null) {
			addTask(new FaceAndInteractTask(found, selector));
		}
	}

	@Override
	public String getDescription(AIHelper helper) {
		return "Tinting...";
	}

	@Override
	public String toString() {
		return "TintStrategy [color=" + color + ", current=" + current
				+ ", type=" + type + "]";
	}

}
