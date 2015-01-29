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
import net.famzangl.minecraft.minebot.ai.strategy.TaskStrategy;
import net.minecraft.client.gui.GuiEnchantment;

/**
 * Strategy: kill mobs, until we have enough levels.
 * 
 * @author michael
 * 
 */

public class XPFarmStrategy extends TaskStrategy {

	private final int level;
	private final boolean doEnchant;

	public XPFarmStrategy(boolean doEnchant, int level) {
		this.doEnchant = doEnchant;
		this.level = level;
	}

	@Override
	public void searchTasks(AIHelper helper) {
		if (hasLevelsToEnchant(helper) && doEnchant) {
			if (enchantmentTableOpened(helper)) {
				addTask(new PutItemInTableTask());
				addTask(new SelectEnchantmentTask());
			} else {
				addTask(new FaceBlockOfTypeTask());
				addTask(new ClickOnEnchantmentTable());
			}
		} else if (enchantmentTableOpened(helper)) {
			addTask(new TakeEnchantedItemTask());
			addTask(new CloseScreenTask());
		} else if (helper.getMinecraft().thePlayer.experienceLevel < level) {
			addTask(new FaceAnyMobTask());
			addTask(new KillAnyMobTask());
		}
	}

	private boolean hasLevelsToEnchant(AIHelper helper) {
		return helper.getMinecraft().thePlayer.experienceLevel >= level;
	}

	private boolean enchantmentTableOpened(AIHelper helper) {
		return helper.getMinecraft().currentScreen instanceof GuiEnchantment;
	}

	@Override
	public String getDescription(AIHelper helper) {
		return "Enchanting for level " + level;
	}
}
