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
import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.famzangl.minecraft.minebot.ai.task.UseItemOnBlockTask;
import net.minecraft.client.gui.GuiEnchantment;
import net.minecraft.init.Blocks;

public class ClickOnEnchantmentTable extends UseItemOnBlockTask {

	public ClickOnEnchantmentTable() {
		super(new BlockSet(Blocks.enchanting_table));
	}

	@Override
	public boolean isFinished(AIHelper h) {
		return h.getMinecraft().currentScreen instanceof GuiEnchantment;
	}

}
