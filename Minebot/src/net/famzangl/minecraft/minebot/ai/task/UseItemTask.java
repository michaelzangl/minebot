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
import net.famzangl.minecraft.minebot.ai.ItemFilter;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;

/**
 * Uses an item on something. Use subclases to define what to use it for.
 * 
 * @author michael
 *
 */
public abstract class UseItemTask extends AITask {
	private boolean clicked;
	private final ItemFilter filter;

	public UseItemTask() {
		this(null);
	}

	public UseItemTask(ItemFilter filter) {
		this.filter = filter;
	}

	@Override
	public boolean isFinished(AIHelper aiHelper) {
		return clicked;
	}

	@Override
	public void runTick(AIHelper aiHelper, TaskOperations taskOperations) {
		if (!clicked) {
			if (filter != null) {
				if (!aiHelper.selectCurrentItem(filter)) {
					return;
				}
			}

			final MovingObjectPosition position = aiHelper.getObjectMouseOver();
			if (position == null
					|| position.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) {
				notFacingBlock(aiHelper);
				return;
			}
			// Check is facing to make sure top facing check passed.
			if (!aiHelper.isFacingBlock(position.getBlockPos())
					|| !isBlockAllowed(aiHelper, position.getBlockPos())) {
				notFacingBlock(aiHelper);
				return;
			}

			aiHelper.overrideUseItem();
			clicked = true;
		}
	}

	protected void notFacingBlock(AIHelper aiHelper) {
	}

	protected boolean isBlockAllowed(AIHelper aiHelper, BlockPos pos) {
		return true;
	}
}
