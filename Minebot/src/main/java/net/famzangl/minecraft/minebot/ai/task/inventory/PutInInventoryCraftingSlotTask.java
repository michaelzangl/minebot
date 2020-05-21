package net.famzangl.minecraft.minebot.ai.task.inventory;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.minecraft.item.Item;

/**
 * Put an item in the placer inventory crafting slots.
 *
 * Screen is the player inventory screen using PlayerContainer
 * Slot 0: Crafting result
 * Slot 1-4: Crafting input
 * Slot 5-8: Armor
 * Then Inventory
 */
public class PutInInventoryCraftingSlotTask extends MoveInInventoryTask {

    private final int craftingX;
    private final int craftingY;
    private final Item item;
    private final int itemCount;

    public PutInInventoryCraftingSlotTask(int craftingX, int craftingY, Item item,
                                          int itemCount) {
        this.craftingX = craftingX;
        this.craftingY = craftingY;
        this.item = item;
        this.itemCount = itemCount;
    }


	@Override
	protected int getFromStack(AIHelper aiHelper) {
		int inventorySlot = findItemInInventory(aiHelper, item);
		if (inventorySlot < 0) {
			return -1;
		}

		return 9 + convertPlayerInventorySlot(inventorySlot);
	}


	@Override
	protected int getToStack(AIHelper aiHelper) {
		return (craftingX * 2 + craftingY) + 1;
	}

	@Override
	protected int getMissingAmount(AIHelper aiHelper, int currentCount) {
		return itemCount - currentCount;
	}

}
