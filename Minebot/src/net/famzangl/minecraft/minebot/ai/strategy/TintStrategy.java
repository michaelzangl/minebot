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
import net.famzangl.minecraft.minebot.ai.selectors.NotSelector;
import net.famzangl.minecraft.minebot.ai.selectors.OrSelector;
import net.famzangl.minecraft.minebot.ai.selectors.OwnTameableSelector;
import net.famzangl.minecraft.minebot.ai.task.FaceAndInteractTask;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;

import com.google.common.base.Predicate;

public class TintStrategy extends TaskStrategy {
	private static final int DISTANCE = 20;
	private final EnumDyeColor color;
	private final EnumDyeColor current;
	private final TintType type;

	public TintStrategy(EnumDyeColor color, TintType type, EnumDyeColor current) {
		this.color = color;
		this.type = type;
		this.current = current;
	}

	private final class SheepSelector implements Predicate<Entity> {
		@Override
		public boolean apply(Entity var1) {
			return var1 instanceof EntitySheep;
		}
	}

	private final class WolfSelector extends OwnTameableSelector {
		private WolfSelector(EntityPlayerSP owner) {
			super(owner);
		}

		@Override
		public boolean apply(Entity var1) {
			return var1 instanceof EntityWolf && super.apply(var1);
		}
	}

	public static enum TintType {
		WOLF, SHEEP, ANY;

	}

	public static class DyeItemFilter implements ItemFilter {

		private final EnumDyeColor color;

		public DyeItemFilter(EnumDyeColor color) {
			this.color = color;
		}

		@Override
		public boolean matches(ItemStack itemStack) {
			// FIXME: Color id.
			return itemStack != null
					&& itemStack.getItem() != null
					&& itemStack.getItem() instanceof ItemDye
					&& (color == null || 15 - itemStack.getItemDamage() == color
							.ordinal());
		}

	}

	@Override
	public void searchTasks(AIHelper helper) {
		if (!helper.selectCurrentItem(new DyeItemFilter(color))) {
			return;
		}

		final EntityPlayerSP owner = helper.getMinecraft().thePlayer;
		// FIXME: Check.
		final EnumDyeColor holdingColor = EnumDyeColor.values()[15 - owner.inventory
				.getCurrentItem().getItemDamage()];
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
