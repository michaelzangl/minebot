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
package net.famzangl.minecraft.minebot.ai.strategy;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSets;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Prevents a suffocation in walls because of server lags.
 * 
 * @author michael
 * 
 */
public class DoNotSuffocateStrategy extends AIStrategy {
	private static final Logger LOGGER = LogManager.getLogger(DoNotSuffocateStrategy.class);

	@Override
	public boolean takesOverAnyTime() {
		return true;
	}

	@Override
	public boolean checkShouldTakeOver(AIHelper helper) {
		BlockPos playerPosition = helper.getPlayerPosition();
		return (!safeFeet(helper, playerPosition) || !safeHead(helper, playerPosition));
	}

	@Override
	protected TickResult onGameTick(AIHelper helper) {
		BlockPos playerPosition = helper.getPlayerPosition();
		if (!safeHead(helper, playerPosition)) {
			// Do head first => being stuck inside head is worse.
			destroyBlockWeAreIn(helper, playerPosition.add(0, 1, 0));
			return TickResult.TICK_HANDLED;
		} else if (!safeFeet(helper, playerPosition)) {
			destroyBlockWeAreIn(helper, playerPosition);
			return TickResult.TICK_HANDLED;
		} else {
			return TickResult.NO_MORE_WORK;
		}
	}

	private void destroyBlockWeAreIn(AIHelper helper, BlockPos toDestroy) {
		if (LOGGER.isDebugEnabled()) {
			BlockState blockState = helper.getWorld().getBlockState(toDestroy);
			LOGGER.debug("Detected that we are inside unsafe block: " + blockState
					+ " (id=" + BlockSet.getStateId(blockState) + "). Attempting to destroy it.");
		}
		helper.faceAndDestroy(toDestroy);
	}

	private boolean safeHead(AIHelper helper, BlockPos p) {
		return BlockSets.HEAD_CAN_WALK_THROUGH.isAt(helper.getWorld(), p.add(0, 1, 0));
	}

	private boolean safeFeet(AIHelper helper, BlockPos p) {
		return BlockSets.FEET_CAN_WALK_THROUGH.isAt(helper.getWorld(), p);
	}

	@Override
	public String getDescription(AIHelper helper) {
		return "Destroy block the player is hanging in";
	}

	@Override
	public String toString() {
		return "DoNotSuffocateStrategy{}";
	}
}
