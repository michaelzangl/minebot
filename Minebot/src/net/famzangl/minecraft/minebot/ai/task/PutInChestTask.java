package net.famzangl.minecraft.minebot.ai.task;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.minecraft.client.gui.inventory.GuiChest;

public class PutInChestTask extends PutItemInContainerTask {
	private final int inventorySlot;

	/**
	 * Puts the given item in the current chest.
	 * 
	 * @param inventorySlot
	 */
	public PutInChestTask(int inventorySlot) {
		this.inventorySlot = inventorySlot;

	}

	@Override
	protected int getStackToPut(AIHelper h) {
		GuiChest screen = (GuiChest) h.getMinecraft().currentScreen;
		int slots = screen.inventorySlots.inventorySlots.size();
		int iSlot;
		if (inventorySlot < 9) {
			iSlot = inventorySlot + 3 * 9;
		} else {
			iSlot = inventorySlot - 9;
		}
		return iSlot + (slots - 9 * 4);
	}

}
