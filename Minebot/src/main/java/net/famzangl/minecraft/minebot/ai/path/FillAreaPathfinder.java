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
import net.famzangl.minecraft.minebot.ai.command.AICommandParameter.BlockFilter;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSets;
import net.famzangl.minecraft.minebot.ai.path.world.WorldData;
import net.famzangl.minecraft.minebot.ai.task.WaitTask;
import net.famzangl.minecraft.minebot.ai.utils.BlockCuboid;
import net.famzangl.minecraft.minebot.build.blockbuild.AbstractBuildTask;
import net.famzangl.minecraft.minebot.build.blockbuild.BlockBuildTask;
import net.famzangl.minecraft.minebot.settings.MinebotSettingsRoot;
import net.famzangl.minecraft.minebot.settings.PathfindingSetting;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class FillAreaPathfinder extends MovePathFinder {
	private static final Marker MARKER_FILL = MarkerManager.getMarker("fill");
	private static final Logger LOGGER = LogManager.getLogger(FillAreaPathfinder.class);
	private static final BlockSet PLACEABLE_BLOCKS = BlockSets.SIMPLE_CUBE;
	private static final BlockSet GROUND_BLOCKS = BlockSet.builder().add(BlockSets.SIMPLE_CUBE).add(
			BlockSets.FALLING).build();

	public static class FillBlocks extends BlockFilter {

		@Override
		public boolean matches(BlockState block) {
			return PLACEABLE_BLOCKS.contains(block);
		}

	}

	private final BlockCuboid<WorldData> fillCuboid;
	private int currentFillLayer;
	private final BlockState blockToPlace;

	public FillAreaPathfinder(BlockCuboid<WorldData> cuboid,
			BlockState blockToPlace) {
		fillCuboid = cuboid;
		this.blockToPlace = blockToPlace;
		currentFillLayer = cuboid.getMin().getY();
		LOGGER.debug(MARKER_FILL, "Filling " + cuboid);
	}

	@Override
	protected PathfindingSetting loadSettings(MinebotSettingsRoot settingsRoot) {
		return settingsRoot.getPathfinding().getNonDestructive();
	}

	@Override
	protected boolean runSearch(BlockPos playerPosition) {
		if (generatePlaceBlockTask(playerPosition)) {
			return true;
		} else {
			return super.runSearch(playerPosition);
		}
	}

	@Override
	protected void noPathFound() {
		super.noPathFound();
		if (currentFillLayer < fillCuboid.getMax().getY()) {
			currentFillLayer++;
			LOGGER.debug(MARKER_FILL, "Advance to layer " + currentFillLayer);
			addTask(new WaitTask());
		} else {
			AIChatController.addChatLine("Done filling area");
		}
	}

	@Override
	protected float rateDestination(int distance, int x, int y, int z) {
		if (!BlockSets.SIMPLE_CUBE.isAt(world, x, y - 1, z)) {
			// Only place on a cube for now.
			return -1;
		}

		for (BlockPos pos : AbstractBuildTask.STANDABLE) {
			int blockPosY = y - pos.getY();
			if (y != currentFillLayer) {
				continue;
			}
			int blockPosX = x - pos.getX();
			int blockPosZ = z - pos.getZ();
			if (fillCuboid.contains(world, blockPosX, blockPosY, blockPosZ)
					&& BlockSets.AIR.isAt(world, blockPosX, blockPosY,
							blockPosZ)) {
				return distance;
			}
		}
		return -1;
	}

	@Override
	protected void addTasksForTarget(BlockPos currentPos) {
		if (!generatePlaceBlockTask(currentPos)) {
			LOGGER.error(MARKER_FILL, "Could not find any task at "
					+ currentPos);
		}
	}

	private boolean generatePlaceBlockTask(BlockPos currentPos) {
		for (BlockPos pos : AbstractBuildTask.STANDABLE) {
			BlockPos placeAtPos = currentPos.subtract(pos);
			if (placeAtPos.getY() == currentFillLayer
					&& fillCuboid.contains(world, placeAtPos)
					&& BlockSets.AIR.isAt(world, placeAtPos)
					&& BlockSets.AIR.isAt(world, placeAtPos.up())
					&& BlockSets.AIR.isAt(world, placeAtPos.up(2))
					&& BlockSets.safeSideAround(world, placeAtPos.up())
					&& BlockSets.safeSideAround(world, placeAtPos.up(2))) {
				addTask(new BlockBuildTask(placeAtPos, blockToPlace)
						.getPlaceBlockTask(pos));
				return true;
			}
		}
		return false;
	}
}
