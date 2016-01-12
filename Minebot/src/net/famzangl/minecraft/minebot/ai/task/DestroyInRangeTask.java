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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSets;
import net.famzangl.minecraft.minebot.ai.path.world.WorldData;
import net.famzangl.minecraft.minebot.ai.path.world.WorldWithDelta;
import net.famzangl.minecraft.minebot.ai.render.PosMarkerRenderer;
import net.famzangl.minecraft.minebot.ai.utils.BlockArea;
import net.famzangl.minecraft.minebot.ai.utils.BlockArea.AreaVisitor;
import net.famzangl.minecraft.minebot.ai.utils.BlockCuboid;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;

/**
 * Destroys all Blocks in a given area. Individual blocks can be excluded, see
 * {@link #blacklist(BlockPos)}
 * 
 * @author Michael Zangl
 *
 */
public class DestroyInRangeTask extends AITask implements CanPrefaceAndDestroy {
	private static final Marker MARKER_DESTROY_IN_RANGE = MarkerManager.getMarker("destroy_in_range");
	private class ClosestBlockFinder implements AreaVisitor {
		BlockPos next = null;
		double currentMin = Float.POSITIVE_INFINITY;
		private final AIHelper h;

		public ClosestBlockFinder(AIHelper h) {
			this.h = h;
		}

		@Override
		public void visit(WorldData world, int x, int y, int z) {
			final double rating = rate(h, x, y, z);
			if (rating >= 0 && rating < currentMin) {
				next = new BlockPos(x, y, z);
				currentMin = rating;
			}
		}
	}

	private class ApplyToDelta implements AreaVisitor {


		public ApplyToDelta() {
		}

		@Override
		public void visit(WorldData world, int x, int y, int z) {
			if (isSafeToDestroy(world, x, y, z)) {
				// FIXME: Use generics instead of cast.
				((WorldWithDelta) world).setBlock(x, y, z, 0, 0);
				y++;
				while (isSafeFallingBlock(world, x, y, z)) {
					((WorldWithDelta) world).setBlock(x, y, z, 0, 0);
					y++;
				}
			} else {
				LOGGER.error(MARKER_DESTROY_IN_RANGE, "Cannot destroy for " + x + "," + y + ","
						+ z + ", block is: " + world.getBlockId(x, y, z));
			}
		}

	}

	private int facingAttempts;
	private volatile BlockPos currentAttemptingPos;
	private final ArrayList<BlockPos> failedBlocks = new ArrayList<BlockPos>();
	private BlockArea range;
	private Vec3 facingPos;
	private BlockPos lastFacingFor;

	private final PosMarkerRenderer renderer = new PosMarkerRenderer(0, 0, 255);

	/**
	 * Create a new {@link DestroyInRangeTask}.
	 * 
	 * @param p1
	 *            One corner
	 * @param p2
	 *            The other corner
	 */
	public DestroyInRangeTask(BlockPos p1, BlockPos p2) {
		this(new BlockCuboid(p1, p2));
	}

	public DestroyInRangeTask(BlockArea range) {
		this.range = range;
	}

	private BlockPos getNextToDestruct(AIHelper h) {
		if (currentAttemptingPos != null
				&& !noDestructionRequired(h.getWorld(),
						currentAttemptingPos.getX(),
						currentAttemptingPos.getY(),
						currentAttemptingPos.getZ())) {
			return currentAttemptingPos;
		}
		ClosestBlockFinder f = new ClosestBlockFinder(h);
		range.accept(f, h.getWorld());
		currentAttemptingPos = f.next;
		return currentAttemptingPos;
	}

	private double rate(AIHelper h, int x, int y, int z) {
		if (noDestructionRequired(h.getWorld(), x, y, z)) {
			return -1;
		} else {
			double distance = h.getMinecraft().thePlayer.getDistance(x + .5, y
					+ .5 - h.getMinecraft().thePlayer.getEyeHeight(), z + .5);
			// 0..1
			double change = h
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
	public boolean isFinished(AIHelper h) {
		return getNextToDestruct(h) == null;
	}

	@Override
	public void runTick(AIHelper h, TaskOperations o) {
		BlockPos n = getNextToDestruct(h);
		if (facingAttempts > 23) {
			failedBlocks.add(n);
			n = getNextToDestruct(h);
			facingAttempts = 0;
		}
		if (n != null) {
			if (facingAttempts % 5 == 4 || lastFacingFor == null
					|| !lastFacingFor.equals(n)) {
				facingPos = h.getWorld().getBlockBounds(n).random(n, .9);
				lastFacingFor = n;
			}

			BlockPos p = checkFacingAcceptableBlock(h, n, h.isFacing(facingPos));
			if (p != null) {
				h.selectToolFor(p);
				h.overrideAttack();
				facingAttempts = 0;
			} else {
				h.face(facingPos);
				facingAttempts++;
			}
		}
	}

	protected boolean isAcceptedFacingPos(AIHelper h, BlockPos n, BlockPos pos) {
		return !noDestructionRequired(h.getWorld(), pos.getX(), pos.getY(),
				pos.getZ());
	}

	protected BlockPos checkFacingAcceptableBlock(AIHelper h, BlockPos n, boolean isFacingRightDirection) {
		MovingObjectPosition hit = h.getObjectMouseOver();
		if (isFacingRightDirection && hit != null && hit.typeOfHit == MovingObjectType.BLOCK) {
			BlockPos pos = hit.getBlockPos();
			if (isAcceptedFacingPos(h, n, pos)) {
				return pos;
			}
		}
		if (h.isFacingBlock(n)) {
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

	// /**
	// * Add a {@link BlockPos} to the list of blocks that should be excluded
	// from
	// * this area.
	// *
	// * @param pos
	// */
	// public void blacklist(BlockPos pos) {
	// failedBlocks.add(pos);
	// }

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

	@Override
	public void drawMarkers(RenderTickEvent event, AIHelper helper) {
		BlockPos currentAttemptingPos2 = currentAttemptingPos;
		if (currentAttemptingPos2 != null) {
			renderer.render(event, helper, currentAttemptingPos2);
		}
		super.drawMarkers(event, helper);
	}
}
