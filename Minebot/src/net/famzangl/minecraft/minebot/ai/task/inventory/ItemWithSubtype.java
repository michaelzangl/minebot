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

import net.famzangl.minecraft.minebot.ai.command.BlockWithData;
import net.famzangl.minecraft.minebot.ai.command.BlockWithDataOrDontcare;
import net.famzangl.minecraft.minebot.ai.command.BlockWithDontcare;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

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
	private final int itemDamage;
	private final boolean hasSubtype;

	public ItemWithSubtype(ItemStack stack) {
		this(Item.getIdFromItem(stack.getItem()), stack.getItemDamage());
	}

	public ItemWithSubtype(int itemId, int itemDamage) {
		this.itemId = itemId;
		this.itemDamage = itemDamage;
		this.hasSubtype = getItem().getHasSubtypes();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (hasSubtype ? 1231 : 1237);
		if (hasSubtype) {
			result = prime * result + itemDamage;
		}
		result = prime * result + itemId;
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
		ItemWithSubtype other = (ItemWithSubtype) obj;
		if (hasSubtype != other.hasSubtype)
			return false;
		if (itemId != other.itemId)
			return false;
		if (hasSubtype && itemDamage != other.itemDamage)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return hasSubtype ? itemId + ":" + itemDamage : itemId + "";
	}

	public ItemWithSubtype withSubtype(int subtype) {
		return new ItemWithSubtype(itemId, subtype);
	}

	public ItemWithSubtype withSubtype(String string) {
		int subtype;
		if (string.matches("\\d{1,3}")) {
			subtype = Integer.parseInt(string);
			if (subtype >= 64) {
				throw new IllegalArgumentException("Subtype " + subtype + " too big.");
			}
		} else {
			throw new IllegalArgumentException("Could not parse subtype: " + string);
		}
		return withSubtype(subtype);
	}

	public Item getItem() {
		return Item.getItemById(itemId);
	}
	
	public BlockWithDataOrDontcare getBlockType() {
		Item item = getItem();
		if (item instanceof ItemBlock) {
			ItemBlock itemBlock = (ItemBlock) item;
			if (hasSubtype) {
				int meta = getItem().getMetadata(itemDamage);
				return new BlockWithData(itemBlock.block, meta);
			} else {
				return new BlockWithDontcare(itemBlock.block);
			}
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
		stack.stackSize = size;
		if (stack.getHasSubtypes()) {
			stack.setItemDamage(itemDamage);
		}
		return stack;
	}
	
	public static ItemWithSubtype fromStack(ItemStack stack) {
		return stack == null ? null : new ItemWithSubtype(stack);
	}
	
	/**
	 * Convert a name to an item id. Always sets the subtype to 0.
	 * @param name
	 * @return
	 */
	public static ItemWithSubtype fromTypeName(String name) {
		Item item = Item.getByNameOrId(name);
		if (item == null) {
			return null;
		}
		return new ItemWithSubtype(Item.getIdFromItem(item), 0);
	}
}
