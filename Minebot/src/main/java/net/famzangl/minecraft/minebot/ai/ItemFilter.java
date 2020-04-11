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
package net.famzangl.minecraft.minebot.ai;

import net.minecraft.item.ItemStack;

/**
 * An {@link ItemStack} filter that can match item stacks.
 * 
 * @see HumanReadableItemFilter
 * @author michael
 * 
 */
public interface ItemFilter {
	ItemFilter ANY = new ItemFilter(){
		@Override
		public boolean matches(ItemStack itemStack) {
			return true;
		}
	};

	public static class OrItemFilter implements ItemFilter {
		
		private final ItemFilter[] filters;

		public OrItemFilter(ItemFilter...filters) {
			this.filters = filters;
		}

		@Override
		public boolean matches(ItemStack itemStack) {
			for (ItemFilter f : filters) {
				if (f.matches(itemStack)) {
					return true;
				}
			}
			return false;
		}
		
		public ItemFilter[] getFilters() {
			return filters;
		}
	}

	public static class AndItemFilter implements ItemFilter {
		
		private final ItemFilter[] filters;

		public AndItemFilter(ItemFilter...filters) {
			this.filters = filters;
		}

		@Override
		public boolean matches(ItemStack itemStack) {
			for (ItemFilter f : filters) {
				if (!f.matches(itemStack)) {
					return false;
				}
			}
			return true;
		}
		
		public ItemFilter[] getFilters() {
			return filters;
		}
	}

	public static class NotItemFilter implements ItemFilter {
		
		private final ItemFilter filter;

		public NotItemFilter(ItemFilter filter) {
			this.filter = filter;
		}

		@Override
		public boolean matches(ItemStack itemStack) {
			return !filter.matches(itemStack);
		}
		public ItemFilter getFilter() {
			return filter;
		}
	}
	
	/**
	 * Checks if this filter matches the item.
	 * 
	 * @param itemStack
	 *            The item stack. It might be <code>null</code> for an empty
	 *            stack
	 * @return If the stack matches this filter.
	 */
	boolean matches(ItemStack itemStack);
}