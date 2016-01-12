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

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.minecraft.item.ItemStack;

/**
 * Put an item in a given slot of the currently open crafting table.
 * 
 * @author michael
 *
 */
public class PutOnCraftingTableTask extends MoveInInventoryTask {

	/**
	 * Slot 0..8
	 */
	private final int craftingSlot;
	private final ItemWithSubtype item;
	private final int itemCount;

	public PutOnCraftingTableTask(int craftingSlot, ItemWithSubtype item,
			int itemCount) {
		super();
		this.craftingSlot = craftingSlot;
		this.item = item;
		this.itemCount = itemCount;
	}

	@Override
	protected int getFromStack(AIHelper h) {
		ItemStack[] mainInventory = h.getMinecraft().thePlayer.inventory.mainInventory;
		int inventorySlot = -1;
		for (int i = 0; i < mainInventory.length; i++) {
			if (item.equals(ItemWithSubtype.fromStack(mainInventory[i]))) {
				inventorySlot = i;
			}
		}
		if (inventorySlot < 0) {
			return -1;
		}

		return 10 + convertPlayerInventorySlot(inventorySlot);
	}


	@Override
	protected int getToStack(AIHelper h) {
		return craftingSlot + 1;
	}

	@Override
	protected int getMissingAmount(AIHelper h, int currentCount) {
		return itemCount - currentCount;
	}
}
