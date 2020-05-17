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
package net.famzangl.minecraft.minebot.ai.path;

import net.famzangl.minecraft.minebot.ai.command.AIChatController;
import net.famzangl.minecraft.minebot.ai.path.world.WorldData;
import net.famzangl.minecraft.minebot.ai.utils.BlockArea;
import net.famzangl.minecraft.minebot.ai.utils.BlockCuboid;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GoToPathfinder extends WalkingPathfinder {
	private static final Logger LOGGER = LogManager.getLogger(GoToPathfinder.class);
	private static final int HORIZONTAL_SEARCH_MIN = (int) (HORIZONTAL_SEARCH_DISTANCE * .8);
	private static final int VERTICAL_SEARCH_MIN = (int) (VERTICAL_SEARCH_DISTANCE * .8);

	private final BlockPos destination;
	private BlockArea<WorldData> targetArea;

	public GoToPathfinder(BlockPos destination) {
		this.destination = destination;
	}

	@Override
	protected boolean runSearch(BlockPos playerPosition) {
		if (playerPosition.equals(destination)) {
			return true;
		}
		targetArea = computeTargetArea(playerPosition, destination);

		LOGGER.debug("Pathfinder target area is: {}", targetArea);

		return super.runSearch(playerPosition);
	}

	static BlockArea<WorldData> computeTargetArea(BlockPos playerPosition, BlockPos destination) {
		BlockPos posDiff = destination.subtract(playerPosition);
		BlockArea<WorldData> area = new BlockCuboid<>(
				destination, destination
		);
		// X
		if (posDiff.getX() > HORIZONTAL_SEARCH_MIN) {
			area = area.unionWith(
					new BlockCuboid<>(
							playerPosition.add(HORIZONTAL_SEARCH_MIN, -VERTICAL_SEARCH_DISTANCE, -HORIZONTAL_SEARCH_DISTANCE),
							playerPosition.add(HORIZONTAL_SEARCH_DISTANCE, VERTICAL_SEARCH_DISTANCE, HORIZONTAL_SEARCH_DISTANCE)
					));
		} else if (posDiff.getX() < -HORIZONTAL_SEARCH_MIN) {
			area = area.unionWith(
					new BlockCuboid<>(
							playerPosition.add(-HORIZONTAL_SEARCH_MIN, -VERTICAL_SEARCH_DISTANCE, -HORIZONTAL_SEARCH_DISTANCE),
							playerPosition.add(-HORIZONTAL_SEARCH_DISTANCE, VERTICAL_SEARCH_DISTANCE, HORIZONTAL_SEARCH_DISTANCE)
					));
		}
		// Y
		if (posDiff.getY() > VERTICAL_SEARCH_MIN) {
			area = area.unionWith(
					new BlockCuboid<>(
							playerPosition.add(-HORIZONTAL_SEARCH_DISTANCE, VERTICAL_SEARCH_MIN, -HORIZONTAL_SEARCH_DISTANCE),
							playerPosition.add(HORIZONTAL_SEARCH_DISTANCE, VERTICAL_SEARCH_DISTANCE, HORIZONTAL_SEARCH_DISTANCE)
					));
		} else if (posDiff.getY() < -VERTICAL_SEARCH_MIN) {
			area = area.unionWith(
					new BlockCuboid<>(
							playerPosition.add(-HORIZONTAL_SEARCH_DISTANCE, -VERTICAL_SEARCH_MIN, -HORIZONTAL_SEARCH_DISTANCE),
							playerPosition.add(HORIZONTAL_SEARCH_DISTANCE, -VERTICAL_SEARCH_DISTANCE, HORIZONTAL_SEARCH_DISTANCE)
					));
		}

		// Z
		if (posDiff.getZ() > HORIZONTAL_SEARCH_MIN) {
			area = area.unionWith(
					new BlockCuboid<>(
							playerPosition.add(-HORIZONTAL_SEARCH_DISTANCE, -VERTICAL_SEARCH_DISTANCE, HORIZONTAL_SEARCH_MIN),
							playerPosition.add(HORIZONTAL_SEARCH_DISTANCE, VERTICAL_SEARCH_DISTANCE, HORIZONTAL_SEARCH_DISTANCE)
					));
		} else if (posDiff.getZ() < -HORIZONTAL_SEARCH_MIN) {
			area = area.unionWith(
					new BlockCuboid<>(
							playerPosition.add(-HORIZONTAL_SEARCH_DISTANCE, -VERTICAL_SEARCH_DISTANCE, -HORIZONTAL_SEARCH_MIN),
							playerPosition.add(HORIZONTAL_SEARCH_DISTANCE, VERTICAL_SEARCH_DISTANCE, -HORIZONTAL_SEARCH_DISTANCE)
					));
		}
		return area;
	}

	@Override
	protected float rateDestination(int distance, int x, int y, int z) {
		return targetArea.contains(world, x, y, z) ? distance : -1;
	}

	@Override
	protected void noPathFound() {
		if (statsVisited < 50) {
			AIChatController.addChatLine("Cannot reach destination. Cannot go far from start position.");
		}
	}

	@Override
	public String toString() {
		return "GoToPathfinder{" +
				"destination=" + destination +
				", targetArea=" + targetArea +
				'}';
	}
}
