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

import net.famzangl.minecraft.minebot.ai.BlockItemFilter;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSets;
import net.famzangl.minecraft.minebot.ai.task.move.DownwardsMoveTask;
import net.famzangl.minecraft.minebot.ai.task.move.UpwardsMoveTask;
import net.famzangl.minecraft.minebot.ai.task.place.DestroyBlockTask;
import net.famzangl.minecraft.minebot.ai.task.place.PlaceBlockAtFloorTask;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

public class LayRailPathFinder extends AlongTrackPathFinder {

	public LayRailPathFinder(int dx, int dz, int cx, int cy, int cz) {
		super(dx, dz, cx, cy, cz, -1);
	}

	@Override
	protected int getNeighbour(int currentNode, int cx, int cy, int cz) {
		final int res = super.getNeighbour(currentNode, cx, cy, cz);
		if (res > 0 && BlockSets.RAILS.isAt(world, cx, cy + 1, cz)) {
			return -1;
		}
		return res;
	}

	@Override
	protected float rateDestination(int distance, int x, int y, int z) {
		if (isRedstoneBlockPosition(x, y, z)
				&& !new BlockSet(Blocks.REDSTONE_BLOCK).isAt(world, x, y, z)) {
			return distance + 2;
		} else if (isOnTrack(x, z) && y == cy
				&& !BlockSets.RAILS.isAt(world, x, y, z)) {
			return distance + 5;
		} else {
			return -1;
		}
	}

	private boolean isRedstoneBlockPosition(int x, int y, int z) {
		return y == cy - 1 && isOnTrack(x, z) && placeAccRail(x, z)
				&& BlockSets.AIR.isAt(world, x, y + 2, z);
	}

	private boolean placeAccRail(int x, int z) {
		return getStepNumber(x, z) % 8 == 0;
	}

	@Override
	protected void addTasksForTarget(BlockPos currentPos) {
		if (isRedstoneBlockPosition(currentPos.getX(), currentPos.getY(),
				currentPos.getZ())) {
			// For those server lags
			addTask(new UpwardsMoveTask(currentPos.add(0, 1, 0),
					new BlockItemFilter(Blocks.REDSTONE_BLOCK)));
		} else if (placeAccRail(currentPos.getX(), currentPos.getZ())) {
			if (!new BlockSet(Blocks.REDSTONE_BLOCK).isAt(world, currentPos.add(0, -1, 0))
					&& BlockSets.safeSideAround(world, currentPos.add(0,-1,0))
					&& BlockSets.SAFE_GROUND.isAt(world, currentPos.add(0,-2,0))) {
				addTask(new DownwardsMoveTask(currentPos.add(0, -1, 0)));
				addTask(new UpwardsMoveTask(currentPos, new BlockItemFilter(
						Blocks.REDSTONE_BLOCK)));
			}
			placeRail(currentPos, Blocks.GOLDEN_RAIL);
		} else {
			placeRail(currentPos, Blocks.RAIL);
		}
	}

	private void placeRail(BlockPos currentPos, Block rail) {
		if (!BlockSets.AIR.isAt(world, currentPos)) {
			addTask(new DestroyBlockTask(currentPos));
		}
		addTask(new PlaceBlockAtFloorTask(currentPos, new BlockItemFilter(rail)));
	}

	@Override
	public String toString() {
		return "LayRailPathFinder [dx=" + dx + ", dz=" + dz + ", cx=" + cx
				+ ", cy=" + cy + ", cz=" + cz + "]";
	}

}