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
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.famzangl.minecraft.minebot.ai.task.TaskOperations;
import net.famzangl.minecraft.minebot.ai.task.error.StringTaskError;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;

/**
 * Take the resulting items from the crafting/enchanting/... table.
 * 
 * @author michael
 *
 */
public class TakeResultItem extends AITask {
	private final Class<? extends GuiContainer> containerClass;
	private final int slot;
	private boolean tookItem;

	public TakeResultItem(Class<? extends GuiContainer> containerClass, int slot) {
		this.containerClass = containerClass;
		this.slot = slot;
	}

	@Override
	public boolean isFinished(AIHelper h) {
		return tookItem;
	}

	@Override
	public void runTick(AIHelper h, TaskOperations o) {
		if (!containerClass.isInstance(h.getMinecraft().currentScreen)) {
			System.out.println("Screen not opened.");
			o.desync(new StringTaskError("No screen opened."));
			tookItem = true;
			return;
		}
		final GuiContainer screen = (GuiContainer) h.getMinecraft().currentScreen;
		if (screen.inventorySlots.getSlot(slot).getHasStack()
				&& shouldTakeStack(screen.inventorySlots.getSlot(slot)
						.getStack())) {
			h.getMinecraft().playerController.windowClick(
					screen.inventorySlots.windowId, slot, 0, 1,
					h.getMinecraft().thePlayer);
			System.out.println("Taking item");
			tookItem = true;
			return;
		} else {
			o.desync(new StringTaskError("No good stack in slot."));
			System.out.println("No good stack in slot " + slot + ".");
			return;
		}
	}

	protected boolean shouldTakeStack(ItemStack stack) {
		return true;
	}
}
