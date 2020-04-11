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
package net.famzangl.minecraft.minebot.build.block;

import net.famzangl.minecraft.minebot.ai.BlockItemFilter;
import net.minecraft.item.ItemStack;

/**
 * An item filter that filters for a given {@link WoodType}
 * 
 * @author michael
 *
 */
public class LogItemFilter extends BlockItemFilter {

	private final WoodType logType;

	public LogItemFilter(WoodType logType) {
		super(logType.block);
		this.logType = logType;
	}

	@Override
	public boolean matches(ItemStack itemStack) {
		return super.matches(itemStack)
				&& (itemStack.getItemDamage() & 3) == logType.lowerBits;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (logType == null ? 0 : logType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final LogItemFilter other = (LogItemFilter) obj;
		if (logType != other.logType) {
			return false;
		}
		return true;
	}

	@Override
	public String getDescription() {
		return logType.toString().toLowerCase() + " logs";
	}

}
