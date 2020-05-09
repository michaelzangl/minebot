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
import net.famzangl.minecraft.minebot.ai.ItemFilter;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.famzangl.minecraft.minebot.ai.task.SkipWhenSearchingPrefetch;
import net.famzangl.minecraft.minebot.ai.task.TaskOperations;
import net.famzangl.minecraft.minebot.ai.task.error.SelectTaskError;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

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
	public boolean isFinished(AIHelper aiHelper) {
		return aiHelper.canSelectItem(itemFiler)
				&& aiHelper.getMinecraft().currentScreen == null;
	}

	@Override
	public void runTick(AIHelper aiHelper, TaskOperations taskOperations) {
		if (aiHelper.getMinecraft().currentScreen instanceof InventoryScreen) {
			final InventoryScreen screen = (InventoryScreen) aiHelper.getMinecraft().currentScreen;
			for (int i = 9; i < 9 * 4; i++) {
				final Slot slot = screen.getContainer().getSlot(i);
				final ItemStack stack = slot.getStack();
				if (slot == null || stack == null
						|| !slot.canTakeStack(aiHelper.getMinecraft().player)
						|| !itemFiler.matches(stack)) {
					continue;
				}
				LOGGER.trace(MARKER_GET_ON_HOTBAR, "Swapping inventory slot " + i);
				swap(aiHelper, screen, i);
				aiHelper.getMinecraft().displayGuiScreen(null);
				break;
			}
		} else if (!inventoryOpened && aiHelper.hasItemInInvetory(itemFiler)) {
//			aiHelper.getMinecraft()
//					.getConnection()
//					.sendPacket(
//							new CPacketClientStatus(
//									CPacketClientStatus.State.));
			aiHelper.getMinecraft().displayGuiScreen(
					new InventoryScreen(aiHelper.getMinecraft().player));
			inventoryOpened = true;
		} else {
			taskOperations.desync(new SelectTaskError(itemFiler));
		}
	}

	/**
	 * Swap a stack with Stack 5 on the hotbar.
	 * 
	 * @param aiHelper
	 * @param screen
	 * @param i
	 */
	private void swap(AIHelper aiHelper, InventoryScreen screen, int i) {
		final PlayerController playerController = aiHelper.getMinecraft().playerController;
		final int windowId = screen.getContainer().windowId;
		final ClientPlayerEntity player = aiHelper.getMinecraft().player;
		playerController.windowClick(windowId, i, 0, ClickType.PICKUP, player);
		playerController.windowClick(windowId, 35 + 5, 0, ClickType.PICKUP, player);
		playerController.windowClick(windowId, i, 0, ClickType.PICKUP, player);
	}

	@Override
	public String toString() {
		return "GetOnHotBarTask{" +
				"itemFiler=" + itemFiler +
				'}';
	}
}
