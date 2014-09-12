package net.famzangl.minecraft.minebot.ai.task;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.strategy.TaskOperations;
import net.famzangl.minecraft.minebot.ai.task.error.StringTaskError;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.inventory.Slot;

public class PutOnCraftingTableTask extends AITask {

	private final int craftingSlot = 0;
	private final int inventorySlot = 0;
	private int itemCount;

	@Override
	public boolean isFinished(AIHelper h) {
		return !(h.getMinecraft().currentScreen instanceof GuiCrafting);
	}

	@Override
	public void runTick(AIHelper h, TaskOperations o) {
		GuiCrafting screen = (GuiCrafting) h.getMinecraft().currentScreen;
		// Offset: 10 blocks.
		int iSlot = 1 + inventorySlot;
		if (inventorySlot < 9) {
			iSlot += 9 * 4;
		}
		Slot takeFrom = screen.inventorySlots.getSlot(iSlot);
		if (!takeFrom.getHasStack()) {
			o.desync(new StringTaskError("Cannot take item from slot "
					+ inventorySlot));
		}
		int takeFromCount = takeFrom.getStack().stackSize;
		if (takeFromCount < itemCount) {
			o.desync(new StringTaskError("Not enough items to take."));
		}

		final PlayerControllerMP playerController = h.getMinecraft().playerController;
		final int windowId = screen.inventorySlots.windowId;
		final EntityClientPlayerMP player = h.getMinecraft().thePlayer;

		playerController.windowClick(windowId, iSlot, 0, 0, player);

		if (takeFromCount > itemCount) {
			playerController.windowClick(windowId, craftingSlot + 1, 1, 0,
					player);
			playerController.windowClick(windowId, iSlot, 0, 0, player);
		} else {
		}
	}
}
