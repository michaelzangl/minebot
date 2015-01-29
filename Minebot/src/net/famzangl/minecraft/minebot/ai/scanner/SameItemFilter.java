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
package net.famzangl.minecraft.minebot.ai.scanner;

import net.famzangl.minecraft.minebot.ai.ItemFilter;
import net.minecraft.item.ItemStack;

/**
 * Filters for items that have the same type as the given item stack.
 * 
 * @author michael
 *
 */
public final class SameItemFilter implements ItemFilter {
	private final ItemStack displayed;

	public SameItemFilter(ItemStack displayed) {
		this.displayed = displayed;
	}

	@Override
	public boolean matches(ItemStack itemStack) {
		if (itemStack == null) {
			return false;
		} else if (itemStack.getItem() != displayed.getItem()) {
			return false;
		} else if (itemStack.getHasSubtypes()
				&& itemStack.getItemDamage() != displayed.getItemDamage()) {
			return false;
		} else if (!ItemStack.areItemStackTagsEqual(itemStack, displayed)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "SameItemFilter [displayed=" + displayed + "]";
	}

}