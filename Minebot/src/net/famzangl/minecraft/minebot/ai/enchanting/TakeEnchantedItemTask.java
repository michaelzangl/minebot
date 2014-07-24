package net.famzangl.minecraft.minebot.ai.enchanting;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.minecraft.client.gui.GuiEnchantment;

public class TakeEnchantedItemTask implements AITask {

	@Override
	public boolean isFinished(AIHelper h) {
		if (!(h.getMinecraft().currentScreen instanceof GuiEnchantment)) {
			return false;
		}
		final GuiEnchantment screen = (GuiEnchantment) h.getMinecraft().currentScreen;
		return !screen.inventorySlots.getSlot(0).getHasStack();
	}

	@Override
	public void runTick(AIHelper h) {
		if (!(h.getMinecraft().currentScreen instanceof GuiEnchantment)) {
			System.out.println("Screen not opened.");
			return;
		}
		final GuiEnchantment screen = (GuiEnchantment) h.getMinecraft().currentScreen;
		if (screen.inventorySlots.getSlot(0).getHasStack()
				&& screen.inventorySlots.getSlot(0).getStack()
						.isItemEnchanted()) {
			h.getMinecraft().playerController.windowClick(
					screen.inventorySlots.windowId, 0, 0, 1,
					h.getMinecraft().thePlayer);
			System.out.println("Taking item");
			return;
		} else {
			System.out.println("No good stack in slot0.");
			return;
		}
	}

}
