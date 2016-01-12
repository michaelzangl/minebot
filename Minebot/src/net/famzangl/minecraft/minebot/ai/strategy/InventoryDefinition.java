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

import java.util.ArrayList;
import java.util.Arrays;

import net.famzangl.minecraft.minebot.ai.task.inventory.ItemWithSubtype;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

/**
 * This is a definition of what the (current) inventory of the player contains.
 * 
 * @author michael
 *
 */
public class InventoryDefinition {
	private final ArrayList<InventorySlot> slots = new ArrayList<InventorySlot>();

	public static class InventorySlot {
		public final int slotIndex;
		public final int amount;
		public final int itemId;
		public final int damageValue;

		public InventorySlot(int slotIndex, int amount, int itemId,
				int damageValue) {
			super();
			if (itemId != 0 && Item.getItemById(itemId) == null) {
				throw new IllegalArgumentException("Could not find item " + itemId);
			}
			if (amount <= 0 && itemId != 0) {
				throw new IllegalArgumentException("Illegal item amount: " + amount);
			}
			this.slotIndex = slotIndex;
			this.amount = amount;
			this.itemId = itemId;
			this.damageValue = damageValue;
		}
		public ItemWithSubtype getItem() {
			if (itemId == 0) {
				return null;
			}
			return new ItemWithSubtype(itemId, damageValue);
		}

		public ItemStack getFakeMcStack() {
			ItemWithSubtype item = getItem();
			return item == null ? null : item.getFakeMCStack(this.amount);
		}

		public boolean isEmpty() {
			return itemId == 0 || amount == 0;
		}

	}

	public InventoryDefinition(InventoryPlayer player) {
		for (int i = 0; i < 36; i++) {
			ItemStack stack = player.mainInventory[i];
			if (stack == null || stack.getItem() == null) {
				continue;
			}

			stack.getItem();
			slots.add(new InventorySlot(i, stack.stackSize, Item
					.getIdFromItem(stack.getItem()),
					stack.getHasSubtypes() ? stack.getItemDamage() : -1));
		}
	}

	public InventoryDefinition(String serialized) {
		Gson gson = new Gson();
		InventorySlot[] slots = gson
				.fromJson(serialized, InventorySlot[].class);
		this.slots.addAll(Arrays.asList(slots));
	}

	public InventorySlot getSlot(int i) {
		for (InventorySlot s : slots) {
			if (s.slotIndex == i) {
				return s;
			}
		}
		return new InventorySlot(i, 0, 0, -1);
	}

	public int searchFreeSlot() {
		for (int i = 0; i < 36; i++) {
			if (getSlot(i).isEmpty()) {
				return i;
			}
		}
		return -1;
	}
	
	public String getJSON() {
		Gson gson = new Gson();
		
		JsonArray array = new JsonArray();
		for (int i = 0; i < 36; i++) {
			InventorySlot slot = slots.get(i);
			if (slot.amount > 0) {
				array.add(gson.toJsonTree(slot));
			}
		}
		return gson.toJson(array);
	}
}
