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
			if (mainInventory[i] != null
					&& new ItemWithSubtype(mainInventory[i]).equals(item)) {
				inventorySlot = i;
			}
		}
		if (inventorySlot < 0) {
			return -1;
		}

		// Offset: 10 blocks.
		int iSlot = 1 + inventorySlot;
		if (inventorySlot < 9) {
			iSlot += 9 * 4;
		}
		return iSlot;
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
