package net.famzangl.minecraft.minebot.ai.enchanting;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.minecraft.client.gui.GuiEnchantment;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class PutItemInTableTask implements AITask {

	@Override
	public boolean isFinished(AIHelper h) {
		GuiEnchantment screen = (GuiEnchantment) h.getMinecraft().currentScreen;
		return screen.inventorySlots.getSlot(0).getHasStack();
	}

	@Override
	public void runTick(AIHelper h) {
		GuiEnchantment screen = (GuiEnchantment) h.getMinecraft().currentScreen;
		for (int i = 1; i < 9 * 4 + 1; i++) {
			Slot slot = screen.inventorySlots.getSlot(i);
			if (slot == null || !slot.canTakeStack(h.getMinecraft().thePlayer)) {
				continue;
			}
			ItemStack stack = slot.getStack();
			if (stack != null && stack.isItemEnchantable()) {
				h.getMinecraft().playerController.windowClick(screen.inventorySlots.windowId,
						i, 0, 1, h.getMinecraft().thePlayer);
				// ItemStack itemstack3 =
				// screen.inventorySlots.transferStackInSlot(mc.thePlayer,
				// i);
				// System.out.println("Selected slot " + i + ". Got stack "
				// + itemstack3 + " (should be null)");
				return;
			}
		}
		System.out.println("No item to put.");
		h.desync();
	}

}
