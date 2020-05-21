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
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.ItemStack;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

/**
 * Take the resulting items from the crafting/enchanting/... table.
 * 
 * @author michael
 *
 */
public class TakeResultItem extends AITask {
	private static final Marker MARKER_TAKE_RESULT = MarkerManager
			.getMarker("take_result");
	private final Class<? extends ContainerScreen<?>> containerClass;
	private final int slot;
	private boolean tookItem;

	public TakeResultItem(Class<? extends ContainerScreen<?>> containerClass, int slot) {
		this.containerClass = containerClass;
		this.slot = slot;
	}

	@Override
	public boolean isFinished(AIHelper aiHelper) {
		return tookItem;
	}

	@Override
	public void runTick(AIHelper aiHelper, TaskOperations taskOperations) {
		Screen currentScreen = aiHelper.getMinecraft().currentScreen;
		if (!containerClass.isInstance(currentScreen)) {
			LOGGER.error(
					MARKER_TAKE_RESULT,
					"Screen not opened. Expected one of "
							+ containerClass.getCanonicalName() + " but got "
							+ currentScreen);
			taskOperations.desync(new StringTaskError("No screen opened."));
			tookItem = true;
			return;
		}
		final ContainerScreen<?> screen = (ContainerScreen<?>) currentScreen;
		ItemStack stack = screen.getContainer().getInventory().get(slot);
		if (stack.isEmpty()) {
			taskOperations.desync(new StringTaskError("The slot to take the item from is empty."));
			LOGGER.error(MARKER_TAKE_RESULT, "Empty stack in " + slot + ".");
		} else if (!shouldTakeStack(stack.getStack())) {
			taskOperations.desync(new StringTaskError("No good stack in slot."));
			LOGGER.error(MARKER_TAKE_RESULT, "No good stack in slot " + slot + ".");
		} else {
			aiHelper.getMinecraft().playerController.windowClick(
					screen.getContainer().windowId, slot, 0, ClickType.QUICK_MOVE,
					aiHelper.getMinecraft().player);
			LOGGER.trace(MARKER_TAKE_RESULT, "Taking item");
			tookItem = true;
		}
	}

	protected boolean shouldTakeStack(ItemStack stack) {
		return true;
	}

	@Override
	public String toString() {
		return "TakeResultItem{" +
				"slot=" + slot +
				'}';
	}
}
