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
package net.famzangl.minecraft.minebot.ai.task.inventory;

import java.util.Hashtable;
import java.util.Map.Entry;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

/**
 * A simple list of item counts (how often do we have item x)
 * 
 * @author michael
 *
 */
public class ItemCountList {
	private final Hashtable<ItemWithSubtype, Integer> counts = new Hashtable<ItemWithSubtype, Integer>();

	public ItemCountList() {
	}

	public ItemCountList(ItemCountList from) {
		counts.putAll(from.counts);
	}

	public ItemCountList(InventoryPlayer from) {
		for (ItemStack i : from.mainInventory) {
			if (i != null && i.getItem() != null) {
				this.add(new ItemWithSubtype(i), i.stackSize);
			}
		}
	}

	public int getCount(ItemWithSubtype item) {
		Integer c = counts.get(item);
		return c == null ? 0 : c;
	}

	public void add(ItemWithSubtype item, int count) {
		setCount(item, getCount(item) + count);
	}

	private void setCount(ItemWithSubtype item, int i) {
		counts.put(item, i);
	}

	public boolean hasEnough(ItemWithSubtype item, int count) {
		return getCount(item) >= count;
	}

	// TODO:
	// public boolean hasEnough(ItemCountList minimum) {
	// for (Integer item : minimum.counts.keySet()) {
	// if (!hasEnough(item, minimum.getCount(item))) {
	// return false;
	// }
	// }
	// return true;
	// }

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("ItemCountList [");
		boolean first = true;
		for (Entry<ItemWithSubtype, Integer> e : counts.entrySet()) {
			if (first) {
				first = false;
			} else {
				stringBuilder.append(", ");
			}
			stringBuilder.append(e.getKey());
			stringBuilder.append(": ");
			stringBuilder.append(e.getValue());
		}
		stringBuilder.append("]");
		return stringBuilder.toString();
	}

}
