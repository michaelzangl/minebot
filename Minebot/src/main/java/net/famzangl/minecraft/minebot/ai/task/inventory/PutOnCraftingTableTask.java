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
import net.minecraft.item.Item;
import net.minecraft.item.Items;

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
	private final Item item;
	private final int itemCount;
	private int x;
	private int y;

	public PutOnCraftingTableTask(int x, int y, Item item,
			int itemCount) {
		super();
		this.x = x;
		this.y = y;
		if (item == Items.AIR) {
			throw new IllegalArgumentException("Cannot place air");
		}
		if (itemCount <= 0) {
			throw new IllegalArgumentException("Count out of range: " + itemCount);
		}
		this.craftingSlot = y * 3 + x;
		this.item = item;
		this.itemCount = itemCount;
	}

	@Override
	protected int getFromStack(AIHelper aiHelper) {
		int inventorySlot = findItemInInventory(aiHelper, item);
		if (inventorySlot < 0) {
			return -1;
		}

		return 10 + convertPlayerInventorySlot(inventorySlot);
	}


	@Override
	protected int getToStack(AIHelper aiHelper) {
		return craftingSlot + 1;
	}

	@Override
	protected int getMissingAmount(AIHelper aiHelper, int currentCount) {
		return itemCount - currentCount;
	}

	@Override
	public String toString() {
		return "PutOnCraftingTableTask{" +
				"item=" + item +
				", itemCount=" + itemCount +
				", x=" + x +
				", y=" + y +
				'}';
	}
}
