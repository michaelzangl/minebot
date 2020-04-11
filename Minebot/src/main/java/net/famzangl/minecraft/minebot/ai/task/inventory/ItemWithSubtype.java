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

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

import java.util.Objects;

/**
 * A simple (itemid, damagevalue) touple.
 * 
 * TODO: Use this more in the bot.
 * 
 * @author michael
 *
 */
public class ItemWithSubtype {

	private final int itemId;

	public ItemWithSubtype(ItemStack stack) {
		this(Item.getIdFromItem(stack.getItem()));
	}

	public ItemWithSubtype(int itemId) {
		this.itemId = itemId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ItemWithSubtype that = (ItemWithSubtype) o;
		return itemId == that.itemId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(itemId);
	}

	@Override
	public String toString() {
		return itemId + "";
	}

	public Item getItem() {
		return Item.getItemById(itemId);
	}
	
	public Block getBlockType() {
		Item item = getItem();
		if (item instanceof BlockItem) {
			BlockItem itemBlock = (BlockItem) item;
			return itemBlock.getBlock();
		} else {
			return null;
		}
	}

	public ItemStack getFakeMCStack(int size) {
		Item item = getItem();
		if (item == null) {
			throw new NullPointerException("Could not find item " + itemId);
		}
		ItemStack stack = new ItemStack(item);
		stack.setCount(size);
		return stack;
	}
	
	public static ItemWithSubtype fromStack(ItemStack stack) {
		return stack == null || stack.isEmpty() ? null : new ItemWithSubtype(stack);
	}
	
	/**
	 * Convert a name to an item id. Always sets the subtype to 0.
	 * @param name
	 * @return
	 */
	public static ItemWithSubtype fromTypeName(String name) {
		Item item = Registry.ITEM.getOrDefault(new ResourceLocation(name));
		return new ItemWithSubtype(Item.getIdFromItem(item));
	}
}
