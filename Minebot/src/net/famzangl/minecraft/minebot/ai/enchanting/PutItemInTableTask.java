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
package net.famzangl.minecraft.minebot.ai.enchanting;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.task.inventory.PutItemInContainerTask;
import net.minecraft.client.gui.GuiEnchantment;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * Puts an item the table accepts in the enchantment table.
 * 
 * @author michael
 *
 */
public class PutItemInTableTask extends PutItemInContainerTask {
	private static final int TABLE_INV_OFFSET = 2;

	@Override
	protected int getStackToPut(AIHelper h) {
		final GuiEnchantment screen = (GuiEnchantment) h.getMinecraft().currentScreen;
		for (int i = TABLE_INV_OFFSET; i < 9 * 4 + TABLE_INV_OFFSET; i++) {
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
