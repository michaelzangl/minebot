package net.famzangl.minecraft.minebot.ai.task;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.ItemFilter;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C16PacketClientStatus;

/**
 * Gets the item on the hotbar out of the inventory.
 * 
 * @author michael
 * 
 */
public class GetOnHotBarTask implements AITask {
	private ItemFilter itemFiler;
	private boolean inventoryOpened;

	public GetOnHotBarTask(ItemFilter itemFiler) {
		super();
		this.itemFiler = itemFiler;
	}

	@Override
	public boolean isFinished(AIHelper h) {
		return h.canSelectItem(itemFiler)
				&& h.getMinecraft().currentScreen == null;
	}

	@Override
	public void runTick(AIHelper h) {
		if (h.getMinecraft().currentScreen instanceof GuiInventory) {
			GuiInventory screen = (GuiInventory) h.getMinecraft().currentScreen;
			for (int i = 9; i < 9 * 4; i++) {
				Slot slot = screen.inventorySlots.getSlot(i);
				ItemStack stack = slot.getStack();
				if (slot == null || stack == null
						|| !slot.canTakeStack(h.getMinecraft().thePlayer)
						|| !itemFiler.matches(stack)) {
					continue;
				}
				System.out.println("Swapping inventory slot " + i);
				swap(h, screen, i);
				h.getMinecraft().displayGuiScreen(null);
				break;
			}
		} else if (!inventoryOpened && h.hasItemInInvetory(itemFiler)) {
			h.getMinecraft()
					.getNetHandler()
					.addToSendQueue(
							new C16PacketClientStatus(
									C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
			h.getMinecraft().displayGuiScreen(
					new GuiInventory(h.getMinecraft().thePlayer));
			inventoryOpened = true;
		} else {
			h.desync();
		}
	}

	/**
	 * Swap a stack with Stack 5 on the hotbar.
	 * @param h
	 * @param screen
	 * @param i
	 */
	private void swap(AIHelper h, GuiInventory screen, int i) {
		PlayerControllerMP playerController = h.getMinecraft().playerController;
		int windowId = screen.inventorySlots.windowId;
		EntityClientPlayerMP player = h.getMinecraft().thePlayer;
		playerController.windowClick(windowId, i, 0, 0, player);
		playerController.windowClick(windowId, 35 + 5, 0, 0, player);
		playerController.windowClick(windowId, i, 0, 0, player);
	}

}
