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
import net.famzangl.minecraft.minebot.ai.ClassItemFilter;
import net.famzangl.minecraft.minebot.ai.ItemFilter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemExpBottle;
import net.minecraft.item.ItemSnowball;

public class ThrowStrategy extends AIStrategy{
	
	private ThrowableThing what;

	private int timer = 0;

	private boolean failed;

	public enum ThrowableThing {
		SNOWBALL(ItemSnowball.class), XPBOTTLE(ItemExpBottle.class);
		
		private final Class<? extends Item> itemClass;

		ThrowableThing(Class<? extends Item> itemClass) {
			this.itemClass = itemClass;
		}
		
		public ItemFilter getFilter() {
			return new ClassItemFilter(itemClass);
		}
	}

	
	public ThrowStrategy(ThrowableThing what) {
		this.what = what;
	}


	@Override
	protected TickResult onGameTick(AIHelper helper) {
		if (timer == 2) {
			if (helper.selectCurrentItem(what.getFilter())) {
				helper.overrideUseItem();
			} else {
				failed = true;
			}
		}
		timer++;
		if (timer > 5) {
			return TickResult.NO_MORE_WORK;
		} else {
			return TickResult.TICK_HANDLED;
		}
	}
	@Override
	public boolean hasFailed() {
		return failed;
	}
}
