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

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.ItemFilter;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.famzangl.minecraft.minebot.ai.task.SkipWhenSearchingPrefetch;
import net.famzangl.minecraft.minebot.ai.task.TaskOperations;
import net.famzangl.minecraft.minebot.ai.task.error.SelectTaskError;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C16PacketClientStatus;

/**
 * Gets the item on the hotbar out of the inventory. Currently only uses slot 5.
 * 
 * @author michael
 * 
 */
@SkipWhenSearchingPrefetch
public class GetOnHotBarTask extends AITask {
	private static final Marker MARKER_GET_ON_HOTBAR = MarkerManager.getMarker("get_on_hotbar");
	private final ItemFilter itemFiler;
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
	public void runTick(AIHelper h, TaskOperations o) {
		if (h.getMinecraft().currentScreen instanceof GuiInventory) {
			final GuiInventory screen = (GuiInventory) h.getMinecraft().currentScreen;
			for (int i = 9; i < 9 * 4; i++) {
				final Slot slot = screen.inventorySlots.getSlot(i);
				final ItemStack stack = slot.getStack();
				if (slot == null || stack == null
						|| !slot.canTakeStack(h.getMinecraft().thePlayer)
						|| !itemFiler.matches(stack)) {
					continue;
				}
				LOGGER.trace(MARKER_GET_ON_HOTBAR, "Swapping inventory slot " + i);
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
			o.desync(new SelectTaskError(itemFiler));
		}
	}

	/**
	 * Swap a stack with Stack 5 on the hotbar.
	 * 
	 * @param h
	 * @param screen
	 * @param i
	 */
	private void swap(AIHelper h, GuiInventory screen, int i) {
		final PlayerControllerMP playerController = h.getMinecraft().playerController;
		final int windowId = screen.inventorySlots.windowId;
		final EntityPlayerSP player = h.getMinecraft().thePlayer;
		playerController.windowClick(windowId, i, 0, 0, player);
		playerController.windowClick(windowId, 35 + 5, 0, 0, player);
		playerController.windowClick(windowId, i, 0, 0, player);
	}

}
