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
package net.famzangl.minecraft.minebot.build;

import java.util.Arrays;
import java.util.LinkedList;

import net.famzangl.minecraft.minebot.ai.BlockItemFilter;
import net.famzangl.minecraft.minebot.ai.path.MovePathFinder;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSets;
import net.famzangl.minecraft.minebot.ai.task.move.AlignToGridTask;
import net.famzangl.minecraft.minebot.build.blockbuild.BuildTask;
import net.famzangl.minecraft.minebot.settings.MinebotSettingsRoot;
import net.famzangl.minecraft.minebot.settings.PathfindingSetting;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

/**
 * This path finder finds the way to th next build task.
 * 
 * @author michael
 *
 */
public class ForBuildPathFinder extends MovePathFinder {

	private static final int NEIGHBOURS_PER_DIRECTION = 6;
	private static final BlockSet FENCES = new BlockSet(
			Blocks.cobblestone_wall).unionWith(BlockSets.FENCE);
	/**
	 * Task we want to prepare for.
	 */
	private final BuildTask task;
	int[] res = new int[NEIGHBOURS_PER_DIRECTION * 4];
	private boolean canBuildUp;
	private boolean noPathFound;

	public ForBuildPathFinder(BuildTask task) {
		this.task = task;
//		allowedGroundForUpwardsBlocks = allowedGroundBlocks;
//		footAllowedBlocks = BlockSets.FEET_CAN_WALK_THROUGH;
//		headAllowedBlocks = BlockSets.HEAD_CAN_WALK_TRHOUGH;
//		footAllowedBlocks = footAllowedBlocks.intersectWith(forbiddenBlocks
//				.invert());
//		headAllowedBlocks = headAllowedBlocks.intersectWith(forbiddenBlocks
//				.invert());
	}
	
	@Override
	protected PathfindingSetting loadSettings(MinebotSettingsRoot settingsRoot) {
		return settingsRoot.getPathfinding().getConstructionPathfinder();
	}

	@Override
	protected boolean runSearch(BlockPos playerPosition) {
		canBuildUp = helper.canSelectItem(new BlockItemFilter(Blocks.carpet));
		return super.runSearch(playerPosition);
	}

	@Override
	protected int[] getNeighbours(int currentNode) {
		Arrays.fill(res, -1);
		final int cx = getX(currentNode);
		final int cz = getZ(currentNode);
		getNeighbours(res, 0 * NEIGHBOURS_PER_DIRECTION, currentNode, cx + 1,
				cz);
		getNeighbours(res, 1 * NEIGHBOURS_PER_DIRECTION, currentNode, cx - 1,
				cz);
		getNeighbours(res, 2 * NEIGHBOURS_PER_DIRECTION, currentNode, cx,
				cz + 1);
		getNeighbours(res, 3 * NEIGHBOURS_PER_DIRECTION, currentNode, cx,
				cz - 1);
		return res;
	}

	private void getNeighbours(int[] fill, int offset, int currentNode, int x,
			int z) {
		final int cy = getY(currentNode);
		final int max = canBuildUp ? 3 : 1;
		int offsetAdd = 0;
		for (int y = cy + 1; y <= cy + max; y++) {
			if (!BlockSets.AIR.isAt(world, getX(currentNode), y + 1, getZ(currentNode))) {
				break;
			}
			fill[offset + offsetAdd++] = getNeighbour(currentNode, x, y, z);
		}

		if (BlockSets.AIR.isAt(world, x, cy + 1, z)) {
			for (int y = cy; y > cy - 3; y--) {
				if (!BlockSets.AIR.isAt(world, x, y, z)) {
					break;
				}
				fill[offset + offsetAdd++] = getNeighbour(currentNode, x, y, z);
			}
		}
	}

	@Override
	protected boolean checkGroundBlock(int currentNode, int cx, int cy, int cz) {
		return BlockSets.SAFE_GROUND.isAt(world, cx, cy - 1, cz)
				|| BlockSets.AIR.isAt(world, cx, cy - 1, cz)
				&& FENCES.isAt(world, cx, cy - 2, cz);
	}

	@Override
	protected int distanceFor(int from, int to) {
		if (getY(from) + 1 < getY(to)) {
			return 1 + Math.abs(getY(from) - getY(to)) * 4;
		} else {
			return 1 + Math.abs(getY(from) - getY(to));
		}
	}

	@Override
	protected float rateDestination(int distance, int x, int y, int z) {
		if (task.couldBuildFrom(helper, x, y, z)) {
			return distance;
		}
		return -1;
	}

	@Override
	protected void foundPath(LinkedList<BlockPos> path) {
		BlockPos currentPos = path.removeFirst();
		addTask(new AlignToGridTask(currentPos.getX(), currentPos.getY(),
				currentPos.getZ()));
		while (!path.isEmpty()) {
			final BlockPos nextPos = path.removeFirst();
			addTask(new WalkTowardsTask(currentPos, nextPos));
			currentPos = nextPos;
		}
	}

	@Override
	protected void noPathFound() {
		noPathFound = true;
		super.noPathFound();
	}

	public boolean isNoPathFound() {
		return noPathFound;
	}
}
