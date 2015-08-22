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

import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

/**
 * An item filter that filters for a given (buildable) block.
 * 
 * @author michael
 *
 */
public class BlockItemFilter implements HumanReadableItemFilter {

	private final BlockSet matched;

	public BlockItemFilter(Block... matched) {
		this.matched = new BlockSet(matched);
	}

	public BlockItemFilter(BlockSet matched) {
		this.matched = matched;
	}

	@Override
	public boolean matches(ItemStack itemStack) {
		return itemStack != null && itemStack.getItem() != null
				&& itemStack.getItem() instanceof ItemBlock
				&& matchesItem(itemStack, (ItemBlock) itemStack.getItem());
	}

	protected boolean matchesItem(ItemStack itemStack, ItemBlock item) {
		return matched.contains(item.block);
	}

	@Override
	public String toString() {
		return "BlockItemFilter [matched=" + matched + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((matched == null) ? 0 : matched.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BlockItemFilter other = (BlockItemFilter) obj;
		if (matched == null) {
			if (other.matched != null)
				return false;
		} else if (!matched.equals(other.matched))
			return false;
		return true;
	}

	@Override
	public String getDescription() {
		final StringBuilder str = new StringBuilder();
		matched.getBlockString(str);
		return str.toString();
	}

}
