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
import net.minecraft.client.gui.inventory.GuiChest;

/**
 * Put one inventory slot in the chest that is currently open.
 * 
 * @author michael
 *
 */
public class PutInChestTask extends PutItemInContainerTask {
	private final int inventorySlot;

	/**
	 * Puts the given item in the current chest.
	 * 
	 * @param inventorySlot
	 */
	public PutInChestTask(int inventorySlot) {
		this.inventorySlot = inventorySlot;

	}

	@Override
	protected int getStackToPut(AIHelper h) {
		GuiChest screen = (GuiChest) h.getMinecraft().currentScreen;
		int slots = screen.inventorySlots.inventorySlots.size();
		int iSlot;
		if (inventorySlot < 9) {
			iSlot = inventorySlot + 3 * 9;
		} else {
			iSlot = inventorySlot - 9;
		}
		return iSlot + (slots - 9 * 4);
	}

}
