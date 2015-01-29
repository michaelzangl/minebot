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

import net.famzangl.minecraft.minebot.ai.task.inventory.TakeResultItem;
import net.minecraft.client.gui.GuiEnchantment;
import net.minecraft.item.ItemStack;

/**
 * Takes the enchanted item form the opened table.
 * 
 * @author michael
 *
 */
public class TakeEnchantedItemTask extends TakeResultItem {

	public TakeEnchantedItemTask() {
		super(GuiEnchantment.class, 0);
	}

	@Override
	protected boolean shouldTakeStack(ItemStack stack) {
		return stack.isItemEnchanted();
	}
}
