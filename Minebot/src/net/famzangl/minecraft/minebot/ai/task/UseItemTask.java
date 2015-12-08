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
	public boolean isFinished(AIHelper h) {
		return clicked;
	}

	@Override
	public void runTick(AIHelper h, TaskOperations o) {
		if (!clicked) {
			if (filter != null) {
				if (!h.selectCurrentItem(filter)) {
					return;
				}
			}

			final MovingObjectPosition objectMouseOver = h.getObjectMouseOver();
			if (objectMouseOver == null
					|| objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) {
				notFacingBlock(h);
				return;
			}
			// Check is facing to make sure top facing check passed.
			if (!h.isFacingBlock(objectMouseOver.getBlockPos())
					|| !isBlockAllowed(h, objectMouseOver.getBlockPos())) {
				notFacingBlock(h);
				return;
			}

			h.overrideUseItem();
			clicked = true;
		}
	}

	protected void notFacingBlock(AIHelper h) {
	}

	protected boolean isBlockAllowed(AIHelper h, BlockPos pos) {
		return true;
	}
}
