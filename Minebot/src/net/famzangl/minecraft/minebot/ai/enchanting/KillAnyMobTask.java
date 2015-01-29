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
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.famzangl.minecraft.minebot.ai.task.TaskOperations;
import net.minecraft.util.MovingObjectPosition;

public class KillAnyMobTask extends AITask {

	int tickCount;

	@Override
	public boolean isFinished(AIHelper h) {
		final MovingObjectPosition objectMouseOver = h.getObjectMouseOver();
		return objectMouseOver == null
				|| objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY;
	}

	@Override
	public void runTick(AIHelper h, TaskOperations o) {
		tickCount++;
		if (tickCount % 10 == 5) {
			h.overrideAttack();
		}
	}

}
