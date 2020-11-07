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
import net.famzangl.minecraft.minebot.ai.path.world.BlockSets;
import net.famzangl.minecraft.minebot.ai.path.world.WorldData;
import net.famzangl.minecraft.minebot.ai.path.world.WorldWithDelta;
import net.famzangl.minecraft.minebot.ai.utils.BlockArea;
import net.famzangl.minecraft.minebot.ai.utils.BlockArea.AreaVisitor;
import net.famzangl.minecraft.minebot.ai.utils.BlockCuboid;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Destroys all Blocks in a given area. Individual blocks can be excluded, see
 * 
 * @author Michael Zangl
 *
 */
public class DestroyInRangeTask extends AITask implements CanPrefaceAndDestroy {
	private static final Logger LOGGER = LogManager.getLogger(DestroyInRangeTask.class);
	private static final Marker MARKER_DESTROY_IN_RANGE = MarkerManager.getMarker("destroy_in_range");
	private class ClosestBlockFinder implements AreaVisitor<WorldData> {
		BlockPos next = null;
		double currentMin = Float.POSITIVE_INFINITY;
		private final AIHelper aiHelper;

		public ClosestBlockFinder(AIHelper aiHelper) {
			this.aiHelper = aiHelper;
		}

		@Override
		public void visit(WorldData world, int x, int y, int z) {
			final double rating = rate(aiHelper, x, y, z);
			if (rating >= 0 && rating < currentMin) {
				next = new BlockPos(x, y, z);
				currentMin = rating;
			}
		}
	}

	private class ApplyToDelta implements AreaVisitor<WorldWithDelta> {
		public ApplyToDelta() {
		}

		@Override
		public void visit(WorldWithDelta world, int x, int y, int z) {
			if (isSafeToDestroy(world, x, y, z)) {
				world.setBlock(new BlockPos(x, y, z), Blocks.AIR);
				y++;
				// Blocks that fall down are destroyed as well
				while (isSafeFallingBlock(world, x, y, z)) {
					world.setBlock(new BlockPos(x, y, z), Blocks.AIR);
					y++;
				}
			} else if (!noDestructionRequired(world, x, y, z)) {
				LOGGER.error(MARKER_DESTROY_IN_RANGE, "Cannot simulate the destroy for " + x + "," + y + ","
						+ z + ", block state at that position is: " + world.getBlockStateId(x, y, z));
			}
		}
	}

	private int facingAttempts;
	private volatile BlockPos currentAttemptingPos;
	private final ArrayList<BlockPos> failedBlocks = new ArrayList<BlockPos>();
	private BlockArea<WorldData> range;
	private Vector3d facingPos;
	private BlockPos lastFacingFor;

	/**
	 * Create a new {@link DestroyInRangeTask}.
	 * 
	 * @param p1
	 *            One corner
	 * @param p2
	 *            The other corner
	 */
	public DestroyInRangeTask(BlockPos p1, BlockPos p2) {
		this(new BlockCuboid<>(p1, p2));
	}

	public DestroyInRangeTask(BlockArea<WorldData> range) {
		this.range = range;
	}

	private BlockPos getNextToDestruct(AIHelper aiHelper) {
		if (currentAttemptingPos != null
				&& !noDestructionRequired(aiHelper.getWorld(),
						currentAttemptingPos.getX(),
						currentAttemptingPos.getY(),
						currentAttemptingPos.getZ())) {
			return currentAttemptingPos;
		}
		ClosestBlockFinder blockFinder = new ClosestBlockFinder(aiHelper);
		range.accept(blockFinder, aiHelper.getWorld());
		currentAttemptingPos = blockFinder.next;
		LOGGER.debug(MARKER_DESTROY_IN_RANGE, "Found next block {} with distance rating {}", blockFinder.next, blockFinder.currentMin);
		return currentAttemptingPos;
	}

	private double rate(AIHelper aiHelper, int x, int y, int z) {
		if (noDestructionRequired(aiHelper.getWorld(), x, y, z)) {
			return -1;
		} else {
			double distanceSq = aiHelper.getMinecraft().player.getDistanceSq(x + .5, y
					+ .5 - aiHelper.getMinecraft().player.getEyeHeight(), z + .5);
			double distance = Math.sqrt(distanceSq);

			// Use the change in player rotation as well => this prevents the player from spinning a lot
			// Will be in range 0..1
			double change = aiHelper
					.getRequiredAngularChangeTo(x + .5, y + .5, z + .5)
					/ Math.PI;
			if (change > .20) {
				distance += (change - .20) * 1.5;
			}

			return distance;
		}
	}

	protected boolean noDestructionRequired(WorldData world, int x, int y, int z) {
		return !isSafeToDestroy(world, x, y, z)
				|| failedBlocks.contains(new BlockPos(x, y, z));
	}

	private boolean isSafeToDestroy(WorldData world, int x, int y, int z) {
		BlockPos pos = world.getPlayerPosition();
		return !BlockSets.AIR.isAt(world, x, y, z)
				&& BlockSets.safeSideAround(world, x, y, z)
				&& (BlockSets.SAFE_CEILING.isAt(world, x, y + 1, z) || ((x != pos
						.getX() || y != pos.getY()) && isSafeFallingBlock(
						world, x, y + 1, z)));
	}

	private boolean isSafeFallingBlock(WorldData world, int x, int y, int z) {
		return BlockSets.FALLING.isAt(world, x, y, z)
				&& (/*
					 * BlockSets.FEET_CAN_WALK_THROUGH.isAt(world, x, y + 1, z)
					 * ||
					 */isSafeToDestroy(world, x, y, z));
	}

	@Override
	public boolean isFinished(AIHelper aiHelper) {
		return getNextToDestruct(aiHelper) == null;
	}

	@Override
	public void runTick(AIHelper aiHelper, TaskOperations taskOperations) {
		BlockPos destructPos = getNextToDestruct(aiHelper);
		if (facingAttempts > 23) {
			LOGGER.debug(MARKER_DESTROY_IN_RANGE, "Too many failed attempts to face block {}, skipping destruction of that block", destructPos);
			failedBlocks.add(destructPos);
			destructPos = getNextToDestruct(aiHelper);
			facingAttempts = 0;
		}
		if (destructPos != null) {
			if (facingAttempts % 5 == 4 || lastFacingFor == null
					|| !lastFacingFor.equals(destructPos)) {
				facingPos = aiHelper.getWorld().getFacingBounds(destructPos).random(destructPos, .9);
				lastFacingFor = destructPos;
			}

			BlockPos pos = checkFacingAcceptableBlock(aiHelper, destructPos, aiHelper.isFacing(facingPos));
			if (pos != null) {
				LOGGER.debug(MARKER_DESTROY_IN_RANGE, "Scheduled block is {}. Facing block at position {} and destrying it", destructPos, pos);
				aiHelper.selectToolFor(pos);
				aiHelper.overrideAttack();
				aiHelper.getStats().markIntentionalBlockBreak(pos);
				facingAttempts = 0;
			} else {
				LOGGER.debug(MARKER_DESTROY_IN_RANGE, "Facing block to destroy at {}", facingPos);
				aiHelper.face(facingPos);
				facingAttempts++;
			}
		}
	}

	protected boolean isAcceptedFacingPos(AIHelper aiHelper, BlockPos n, BlockPos pos) {
		return !noDestructionRequired(aiHelper.getWorld(), pos.getX(), pos.getY(),
				pos.getZ()) && range.contains(aiHelper.getWorld(), pos);
	}

	protected BlockPos checkFacingAcceptableBlock(AIHelper aiHelper, BlockPos n, boolean isFacingRightDirection) {
		RayTraceResult position = aiHelper.getObjectMouseOver();
		// If there is a block in the way while facing our desired block, destroy that one too
		if (isFacingRightDirection && position instanceof BlockRayTraceResult) {
			BlockPos pos = ((BlockRayTraceResult) position).getPos();
			if (isAcceptedFacingPos(aiHelper, n, pos)) {
				return pos;
			}
		}
		if (aiHelper.isFacingBlock(n)) {
			return n;
		}
		return null;
	}

	@Override
	public int getGameTickTimeout(AIHelper helper) {
		return 2 * super.getGameTickTimeout(helper);
	}

	@Override
	public String toString() {
		return "DestroyInRangeTask [range=" + range + ", facingAttempts="
				+ facingAttempts + ", failedBlocks=" + failedBlocks + "]";
	}

	@Override
	public List<BlockPos> getPredestroyPositions(AIHelper helper) {
		final BlockPos next = getNextToDestruct(helper);
		return next != null ? Arrays.asList(next) : Collections
				.<BlockPos> emptyList();
	}

	@Override
	public boolean applyToDelta(WorldWithDelta world) {
		// FIXME Check which blocks are really destroyed / fail if they are not.
		range.accept(new ApplyToDelta(), world);
		return true;
	}
}
