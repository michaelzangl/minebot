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
package net.famzangl.minecraft.minebot.ai.task;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.tools.ToolRater;
import net.famzangl.minecraft.minebot.settings.MinebotSettings;

public class ThrowFishingRodTask extends AITask {

	private int time = 4;
	
	@Override
	public boolean isFinished(AIHelper aiHelper) {
		return aiHelper.getMinecraft().player.fishingBobber != null && time < 1;
	}

	@Override
	public void runTick(AIHelper aiHelper, TaskOperations taskOperations) {
		ToolRater settings = MinebotSettings.getSettings().getFishingRater();
		if (aiHelper.selectToolFor(null, settings).wasSuccessful()) {
			time--;
			if (time == 2) {
				aiHelper.overrideUseItem();
			}
		}
	}
	
	@Override
	public int getGameTickTimeout(AIHelper helper) {
		return 40;
	}

}
