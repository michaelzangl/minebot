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
package net.famzangl.minecraft.minebot.ai.task.error;

import net.famzangl.minecraft.minebot.ai.HumanReadableItemFilter;
import net.famzangl.minecraft.minebot.ai.ItemFilter;

/**
 * Tells the user that the bot could not select a given item.
 * @author michael
 *
 */
public final class SelectTaskError extends TaskError {
	private final ItemFilter filter;

	public SelectTaskError(ItemFilter filter) {
		super("Cannot select: " + getMessage(filter));
		this.filter = filter;
	}

	private static String getMessage(ItemFilter filter) {
		if (filter instanceof HumanReadableItemFilter) {
			return ((HumanReadableItemFilter) filter).getDescription();
		} else {
			return filter.toString();
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (filter == null ? 0 : filter.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final SelectTaskError other = (SelectTaskError) obj;
		if (filter == null) {
			if (other.filter != null) {
				return false;
			}
		} else if (!filter.equals(other.filter)) {
			return false;
		}
		return true;
	}

}