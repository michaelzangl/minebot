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

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSets;
import net.famzangl.minecraft.minebot.ai.path.world.WorldData;
import net.famzangl.minecraft.minebot.ai.path.world.WorldWithDelta;
import net.famzangl.minecraft.minebot.ai.tools.ToolRater;
import net.famzangl.minecraft.minebot.ai.utils.BlockArea;
import net.famzangl.minecraft.minebot.ai.utils.BlockArea.AreaVisitor;
import net.famzangl.minecraft.minebot.ai.utils.BlockCuboid;
import net.famzangl.minecraft.minebot.settings.MinebotSettings;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

/**
 * Destroys all Blocks in a given area. Individual blocks can be excluded, see
 * {@link #blacklist(BlockPos)}
 * 
 * @author Michael Zangl
 *
 */
public class DestroyInRangeTask extends AITask implements CanPrefaceAndDestroy {
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
			} else {
				System.out.println("No destruction for " + x + "," + y + ","
						+ z + ", block is: " + world.getBlockId(x, y, z));
			}
		}

	}

	private int facingAttempts;
	private final ArrayList<BlockPos> failedBlocks = new ArrayList<BlockPos>();
	private BlockArea range;
	private Vec3 facingPos;
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
		this(new BlockCuboid(p1, p2));
	}

	public DestroyInRangeTask(BlockArea range) {
		this.range = range;
	}

	private BlockPos getNextToDestruct(AIHelper h) {
		ClosestBlockFinder f = new ClosestBlockFinder(h);
		range.accept(f, h.getWorld());
		return f.next;
	}

	private double rate(AIHelper h, int x, int y, int z) {
		if (noDestructionRequired(h.getWorld(), x, y, z)) {
			return -1;
		} else {
			return h.getMinecraft().thePlayer.getDistanceSq(x + .5,
					y + .5 - h.getMinecraft().thePlayer.getEyeHeight(), z + .5);
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
				&& (BlockSets.FEET_CAN_WALK_THROUGH.isAt(world, x, y + 1, z) || isSafeToDestroy(
						world, x, y + 1, z));
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

			if (isFacingAcceptableBlock(h, n, h.isFacing(facingPos))) {
				ToolRater settings = MinebotSettings.getSettings()
						.getToolRater();
				h.selectToolFor(n, settings);
				h.overrideAttack();
				facingAttempts = 0;
			} else {
				h.face(facingPos);
				facingAttempts++;
			}
		}
	}

	protected boolean isFacingAcceptableBlock(AIHelper h, BlockPos n,
			boolean isFacingRightDirection) {
		return h.isFacingBlock(n);
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
}
