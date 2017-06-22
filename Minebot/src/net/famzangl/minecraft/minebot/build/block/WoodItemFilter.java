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
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

/**
 * This item filter filters for wood planks of a given type.
 * 
 * @author michael
 *
 */
public class WoodItemFilter extends BlockItemFilter {

	private final WoodType woodType;

	public WoodItemFilter(WoodType woodType) {
		super(Blocks.PLANKS);
		this.woodType = woodType;
	}

	@Override
	public boolean matches(ItemStack itemStack) {
		return super.matches(itemStack)
				&& itemStack.getItemDamage() == woodType.ordinal();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (woodType == null ? 0 : woodType.hashCode());
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
		final WoodItemFilter other = (WoodItemFilter) obj;
		if (woodType != other.woodType) {
			return false;
		}
		return true;
	}

	@Override
	public String getDescription() {
		return woodType.toString().toLowerCase() + " planks";
	}
}
