package net.famzangl.minecraft.minebot.ai.strategy;

import java.util.ArrayList;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.google.gson.Gson;

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
			this.slotIndex = slotIndex;
			this.amount = amount;
			this.itemId = itemId;
			this.damageValue = damageValue;
		}

		public ItemStack getFakeMcStack() {
			ItemStack stack = new ItemStack(Item.getItemById(itemId));
			stack.stackSize = this.amount;
			if (stack.getHasSubtypes()) {
				stack.setItemDamage(damageValue);
			}
			return stack;
		}

	}

	public InventoryDefinition(InventoryPlayer player) {
		for (int i = 0; i < 36; i++) {
			ItemStack stack = player.mainInventory[i];
			if (stack == null) {
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
		for (InventorySlot s : slots) {
			this.slots.add(s);
		}
	}

	public InventorySlot getSlot(int i) {
		for (InventorySlot s : slots) {
			if (s.slotIndex == i) {
				return s;
			}
		}
		return null;
	}
}
