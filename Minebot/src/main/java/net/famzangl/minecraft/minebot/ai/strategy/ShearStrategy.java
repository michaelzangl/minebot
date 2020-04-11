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
import net.famzangl.minecraft.minebot.ai.selectors.ItemSelector;
import net.famzangl.minecraft.minebot.ai.selectors.OrSelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;

public class ShearStrategy extends FaceInteractStrategy {
	private final DyeColor color;

	public ShearStrategy(DyeColor color) {
		this.color = color;
	}

	private final class ShearsFilter implements ItemFilter {
		@Override
		public boolean matches(ItemStack itemStack) {
			return itemStack != null
					&& itemStack.getItem() instanceof ShearsItem;
		}
	}

	private final class SheepSelector implements Predicate<Entity> {
		@Override
		public boolean apply(Entity var1) {
			return var1 instanceof SheepEntity
					&& !((SheepEntity) var1).getSheared() && !((SheepEntity) var1).isChild();
		}
	}
	
	@Override
	protected Predicate<Entity> entitiesToInteract(AIHelper helper) {
		Predicate<Entity> selector = new SheepSelector();

		if (color != null) {
			selector = new AndSelector(selector, new ColorSelector(color));
		}
		return selector;
	}
	@Override
	protected Predicate<Entity> entitiesToFace(AIHelper helper) {
		return new OrSelector(super.entitiesToFace(helper), new ItemSelector());
	}
	
	@Override
	public boolean checkShouldTakeOver(AIHelper helper) {
		return super.checkShouldTakeOver(helper) && helper.canSelectItem(new ShearsFilter());
	}
	
	@Override
	protected TickResult onGameTick(AIHelper helper) {
		if (!helper.canSelectItem(new ShearsFilter())) {
			return TickResult.NO_MORE_WORK;
		}
		return super.onGameTick(helper);
	}
	
	@Override
	protected void doInteract(Entity entityHit, AIHelper helper) {
		helper.selectCurrentItem(new ShearsFilter());
		super.doInteract(entityHit, helper);
	}

	@Override
	public String getDescription(AIHelper helper) {
		return "Shearing...";
	}

}
