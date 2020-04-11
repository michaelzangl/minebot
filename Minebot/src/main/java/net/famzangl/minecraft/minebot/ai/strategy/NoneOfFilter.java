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

import java.util.Arrays;

import net.famzangl.minecraft.minebot.ai.ItemFilter;
import net.minecraft.item.ItemStack;

/**
 * An inverted {@link ItemFilter}.
 * 
 * @author michael
 *
 */
public class NoneOfFilter implements ItemFilter {

	private final ItemFilter[] filters;

	public NoneOfFilter(ItemFilter... filters) {
		this.filters = filters;
	}

	@Override
	public boolean matches(ItemStack itemStack) {
		for (ItemFilter filter : filters) {
			if (filter.matches(itemStack)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		return "NoneOfFilter [filters=" + Arrays.toString(filters) + "]";
	}
}
