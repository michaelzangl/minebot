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

import java.util.LinkedList;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.BlockItemFilter;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSets;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.famzangl.minecraft.minebot.ai.task.TaskOperations;
import net.famzangl.minecraft.minebot.ai.task.error.SelectTaskError;
import net.famzangl.minecraft.minebot.ai.task.move.HorizontalMoveTask;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovementInput;

/**
 * This task lets you walk from one position to an other, adjacent position. In
 * contrast to the {@link HorizontalMoveTask}, it won't destroy blocks. It can
 * walk up by placing carpets on the floor and destroying them afterwards.
 * 
 * @author michael
 *
 */
public class WalkTowardsTask extends AITask {

	private static final BlockSet CARPETS = new BlockSet(
			Blocks.carpet);

	private static final BlockItemFilter CARPET = new BlockItemFilter(CARPETS);
	private final BlockPos fromPos;
	private final BlockPos nextPos;
	private final boolean placeCarpets;

	private AITask subTask;

	private final LinkedList<BlockPos> carpets = new LinkedList<BlockPos>();
	private boolean wasStandingOnDest;

	public WalkTowardsTask(BlockPos fromPos, BlockPos nextPos) {
		this(fromPos, nextPos, true);
	}
	
	public WalkTowardsTask(BlockPos fromPos, BlockPos nextPos, boolean placeCarpets) {
		this.fromPos = fromPos;
		this.nextPos = nextPos;
		this.placeCarpets = placeCarpets;
	}

	@Override
	public boolean isFinished(AIHelper aiHelper) {
		return subTask == null
				&& aiHelper.isStandingOn(nextPos.getX(), nextPos.getY(),
						nextPos.getZ()) && carpets.isEmpty();
		/* && getUpperCarpetY(h) < 0 */
	}

	@Override
	public void runTick(AIHelper aiHelper, TaskOperations taskOperations) {
		if (subTask != null && subTask.isFinished(aiHelper)) {
			subTask = null;
		}
		if (subTask != null) {
			subTask.runTick(aiHelper, taskOperations);
		} else {
			final int carpetY = getUpperCarpetY(aiHelper);
			final double carpetBuildHeight = aiHelper.realBlockTopY(fromPos.getX(),
					Math.max(carpetY + 1, fromPos.getY()), fromPos.getZ());
			final double destHeight = aiHelper.realBlockTopY(nextPos.getX(),
					nextPos.getY(), nextPos.getZ());
			if (carpetBuildHeight < destHeight - 1 && placeCarpets) {
				System.out.println("Moving upwards. Carpets are at " + carpetY);
				final int floorY = Math.max(carpetY, fromPos.getY() - 1);
				BlockPos floor = new BlockPos(fromPos.getX(), floorY,
						fromPos.getZ());
				aiHelper.faceBlock(floor);
				if (aiHelper.isFacingBlock(floor, EnumFacing.UP)) {
					if (aiHelper.selectCurrentItem(CARPET)) {
						aiHelper.overrideUseItem();
						carpets.add(new BlockPos(fromPos.getX(), floorY + 1,
								fromPos.getZ()));
					} else {
						taskOperations.desync(new SelectTaskError(CARPET));
					}
				}
				final MovementInput i = new MovementInput();
				i.jump = true;
				aiHelper.overrideMovement(i);
			} else if ((aiHelper.isStandingOn(nextPos.getX(), nextPos.getY(),
					nextPos.getZ()) || wasStandingOnDest)
					&& !carpets.isEmpty()) {
				// Destruct everything after arriving at dest. Then walk to dest
				// again.

				while (!carpets.isEmpty()) {
					// Clean up carpets we already "lost"
					final BlockPos last = carpets.getLast();
					if (BlockSets.AIR.isAt(aiHelper.getWorld(), last)) {
						carpets.removeLast();
					}
				}

				final int x = fromPos.getX() - nextPos.getX();
				final int z = fromPos.getX() - nextPos.getX();
				if (aiHelper.sneakFrom(nextPos, AIHelper.getDirectionForXZ(x, z))) {
					final BlockPos last = carpets.getLast();
					aiHelper.faceAndDestroy(last);
				}

				wasStandingOnDest = true;
			} else {
				aiHelper.walkTowards(nextPos.getX() + 0.5, nextPos.getZ() + 0.5,
						carpetBuildHeight < destHeight - 0.5);
			}
		}
	}

	/**
	 * Gets the Y of the topmost carpet that was placed. -1 if there was none.
	 * 
	 * @param aiHelper
	 * @return
	 */
	private int getUpperCarpetY(AIHelper aiHelper) {
		int upperCarpet = -1;
		for (int y = BlockSets.AIR.unionWith(CARPETS).isAt(aiHelper.getWorld(),
				fromPos) ? fromPos.getY() : fromPos.getY() + 1; y < nextPos
				.getY(); y++) {
			if (CARPETS.contains(aiHelper.getBlock(fromPos.getX(), y, fromPos.getZ()))) {
				upperCarpet = y;
			} else {
				break;
			}
		}
		return upperCarpet;
	}

	@Override
	public String toString() {
		return "WalkTowardsTask [currentPos=" + fromPos + ", nextPos="
				+ nextPos + ", subTask=" + subTask + "]";
	}
}
