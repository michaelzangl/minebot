package net.famzangl.minecraft.minebot.ai.enchanting;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.task.PutItemInContainerTask;
import net.minecraft.client.gui.GuiEnchantment;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class PutItemInTableTask extends PutItemInContainerTask {

	@Override
	protected int getStackToPut(AIHelper h) {
		final GuiEnchantment screen = (GuiEnchantment) h.getMinecraft().currentScreen;
		for (int i = 1; i < 9 * 4 + 1; i++) {
			final Slot slot = screen.inventorySlots.getSlot(i);
			if (slot == null || !slot.canTakeStack(h.getMinecraft().thePlayer)) {
				continue;
			}
			final ItemStack stack = slot.getStack();
			if (stack != null && stack.isItemEnchantable()) {
				return i;
			}
		}
		return -1;
	}

}
