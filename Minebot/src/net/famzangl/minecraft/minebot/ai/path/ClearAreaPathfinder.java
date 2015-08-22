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

import java.util.HashSet;
import java.util.LinkedList;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.famzangl.minecraft.minebot.ai.path.world.WorldData;
import net.famzangl.minecraft.minebot.ai.task.DestroyInRangeTask;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

public class ClearAreaPathfinder extends MovePathFinder {

	private final BlockPos minPos;
	private final BlockPos maxPos;
	private int topY;
//	private final HashSet<BlockPos> foundPositions = new HashSet<BlockPos>();
//	private BlockPos pathEndPosition;

	public ClearAreaPathfinder(BlockPos pos1, BlockPos pos2) {
		minPos = Pos.minPos(pos1, pos2);
		maxPos = Pos.maxPos(pos1, pos2);
		topY = maxPos.getY();
	}

//	@Override
//	protected boolean runSearch(BlockPos playerPosition) {
//		foundPositions.clear();
//		pathEndPosition = playerPosition;
//		do {
//			final boolean finished = super.runSearch(pathEndPosition);
//			if (!finished) {
//				return false;
//			}
//		} while (foundPositions.size() < 20 && pathEndPosition != null);
//		return true;
//	}

//	@Override
//	protected void foundPath(LinkedList<Pos> path) {
//		for (final Pos p : path) {
//			foundPositions.add(p);
//			foundPositions.add(p.add(0, 1, 0));
//			pathEndPosition = p;
//		}
//
//		super.foundPath(path);
//	}

	@Override
	protected void noPathFound() {
//		pathEndPosition = null;
		super.noPathFound();
	}

	@Override
	protected float rateDestination(int distance, int x, int y, int z) {
		if (isInArea(x, y, z)
				&& (!isTemporaryCleared(x, y, z) || !isTemporaryCleared(x,
						y + 1, z) && y < maxPos.getY())) {
			final float bonus = 0.0001f * (x - minPos.getX()) + 0.001f
					* (y - minPos.getY());
			int layerMalus;
			if (topY <= y) {
				layerMalus = 5;
			} else if (!isInArea(x, y + 1, z)
					|| isTemporaryCleared(x, y + 1, z)) {
				layerMalus = 2;
			} else if (isInArea(x, y + 1, z)
					&& !isTemporaryCleared(x, y + 2, z)) {
				layerMalus = 2;
			} else {
				layerMalus = 0;
			}
			return distance + bonus + layerMalus + (maxPos.getY() - y) * 2;
		} else {
			return -1;
		}
	}

	private boolean isTemporaryCleared(int x, int y, int z) {
		return isClearedBlock(world, x, y, z);
//				|| foundPositions.contains(new BlockPos(x, y, z));
	}

	private boolean isInArea(int x, int y, int z) {
		return minPos.getX() <= x && x <= maxPos.getX() && minPos.getY() <= y && y <= maxPos.getY()
				&& minPos.getZ() <= z && z <= maxPos.getZ();
	}
	
	private static final BlockSet clearedBlocks = new BlockSet(Blocks.air,
			Blocks.torch);

	private static boolean isClearedBlock(WorldData world, int x, int y, int z) {
		return clearedBlocks.isAt(world, x, y, z);
	}

	@Override
	protected void addTasksForTarget(BlockPos currentPos) {
		super.addTasksForTarget(currentPos);
		BlockPos top = currentPos;
		for (int i = 1; i < 6; i++) {
			final BlockPos pos = currentPos.add(0, i, 0);
			if (pos.getY() <= maxPos.getY()) {
				top = pos;
			}
		}
		addTask(new DestroyInRangeTask(currentPos, top));
	}

	@Override
	protected int materialDistance(int x, int y, int z, boolean asFloor) {
		return isInArea(x, y, z) ? 0 : super.materialDistance(x, y, z, asFloor);
	}

	public int getAreaSize() {
		return (maxPos.getX() - minPos.getX() + 1) * (maxPos.getY() - minPos.getY() + 1)
				* (maxPos.getZ() - minPos.getZ() + 1);
	}

	public int getToClearCount(AIHelper helper) {
		WorldData world = helper.getWorld();
		int count = 0;
		int newTopY = minPos.getY();
		for (int y = minPos.getY(); y <= maxPos.getY(); y++) {
			for (int z = minPos.getZ(); z <= maxPos.getZ(); z++) {
				for (int x = minPos.getX(); x <= maxPos.getX(); x++) {
					if (!isClearedBlock(world, x, y, z)) {
						count++;
						newTopY = Math.max(y, newTopY);
					}
				}
			}
		}
		topY = newTopY;
		System.out.println("top Y:  " + newTopY);
		return count;
	}
}
