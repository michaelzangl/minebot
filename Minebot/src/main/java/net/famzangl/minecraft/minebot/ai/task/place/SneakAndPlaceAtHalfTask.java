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
package net.famzangl.minecraft.minebot.ai.task.place;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.BlockItemFilter;
import net.famzangl.minecraft.minebot.ai.path.world.BlockBounds;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSets;
import net.famzangl.minecraft.minebot.ai.path.world.WorldData;
import net.famzangl.minecraft.minebot.ai.task.BlockHalf;
import net.famzangl.minecraft.minebot.ai.task.TaskOperations;
import net.famzangl.minecraft.minebot.ai.task.error.StringTaskError;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Arrays;

public class SneakAndPlaceAtHalfTask extends SneakAndPlaceTask {

	protected final static class PlacingDirection implements
			Comparable<PlacingDirection> {

		private final BlockPos pos;
		private final Direction direction;
		private final BlockHalf blockHalf;
		private final WorldData world;

		public PlacingDirection(BlockPos pos, Direction direction,
				BlockHalf half, WorldData world) {
			this.pos = pos;
			this.direction = direction;
			this.blockHalf = half;
			this.world = world;
		}

		protected BlockBounds getBounds() {
			BlockPos placeOn = getPlaceOn();
			BlockBounds bounds = world.getBlockBounds(placeOn);
			BlockBounds faceArea = bounds.clampY(
					blockHalf == BlockHalf.UPPER_HALF ? 0.5 : 0.1,
					blockHalf == BlockHalf.LOWER_HALF ? 0.45 : 0.9 // <- 0.5 but
																// craftbukkit
																// has a problem
																// with that.
					).onlySide(direction.getOpposite());
			return faceArea;
		}

		public Vec3d getRandomPoint(double faceCentered) {
			return getBounds().random(getPlaceOn(), Math.min(.9, faceCentered));
		}

		@Override
		public int compareTo(PlacingDirection o) {
			Vec3d player = world.getExactPlayerPosition();
			Vec3d facing = getRandomPoint(0);
			Vec3d facingO = o.getRandomPoint(0);
			return -Double.compare(player.distanceTo(facing),
					player.distanceTo(facingO));
		}

		public BlockPos getPlaceOn() {
			return pos.offset(direction);
		}

		public boolean isFacing(AIHelper aiHelper) {
			return aiHelper.isFacingBlock(getPlaceOn(), direction.getOpposite(),
					blockHalf);
		}

		public boolean canPlaceOn() {
			return !BlockSets.AIR.isAt(world, getPlaceOn());
		}
	}

	protected final Direction[] DIRS = new Direction[] { Direction.EAST,
			Direction.NORTH, Direction.WEST, Direction.SOUTH };

	protected final BlockHalf blockHalf;

	protected Direction lookingDirection = null;

	private int attempts;

	private final BlockPos positionToPlace;

	private Vec3d facePos;

	private double faceCentered = .3;

	private PlacingDirection[] dirs;

	protected PlacingDirection[] getBuildDirs(AIHelper aiHelper) {
		// we sort them by distance. This order is kept afterwards.
		if (dirs == null) {
			Direction[] orders = createBuildDirsUnordered();
			dirs = new PlacingDirection[orders.length];
			for (int i = 0; i < orders.length; i++) {
				Direction f = orders[i];
				dirs[i] = new PlacingDirection(positionToPlace, f, blockHalf,
						aiHelper.getWorld());
			}
			Arrays.sort(dirs);
		}
		return dirs;
	}

	protected Direction[] createBuildDirsUnordered() {
		return DIRS;
	}

	/**
	 * 
	 * @param destinationStandPosition
	 *            The position one over the block to place.
	 * @param filter
	 *            The items to use.
	 * @param startStandPosition
	 * @param minBuildHeight
	 * @param blockHalf
	 */
	public SneakAndPlaceAtHalfTask(BlockPos destinationStandPosition,
			BlockItemFilter filter, BlockPos startStandPosition,
			double minBuildHeight, BlockHalf blockHalf) {
		super(destinationStandPosition, filter, startStandPosition,
				minBuildHeight);
		this.blockHalf = blockHalf;
		positionToPlace = destinationStandPosition.add(0, -1, 0);
	}

	public SneakAndPlaceAtHalfTask(BlockPos destinationStandPosition,
			BlockItemFilter filter, BlockPos startStandPosition,
			BlockPos positionToPlaceAt, double minBuildHeight,
			BlockHalf blockHalf) {
		super(destinationStandPosition, filter, startStandPosition,
				minBuildHeight);
		this.blockHalf = blockHalf;
		this.positionToPlace = positionToPlaceAt;
	}

	@Override
	protected boolean faceBlock(AIHelper aiHelper, TaskOperations taskOperations) {
		final PlacingDirection[] dirs = getBuildDirs(aiHelper);
		attempts++;
		boolean success = false;
		for (int i = 0; i < dirs.length; i++) {
			final PlacingDirection useSide = dirs[attempts / 10 % dirs.length];
			if (useSide.canPlaceOn()) {
				success = faceSideBlock(aiHelper, useSide);
				break;
			} else {
				attempts += 10;
				faceCentered = .3;
				facePos = null;
			}
		}
		if (attempts > 90) {
			taskOperations.desync(new StringTaskError("Could not face anywhere to place."));
		}
		return success;
	}

	@Override
	protected BlockPos getPositionToPlaceAt() {
		return positionToPlace == null ? super.getPositionToPlaceAt()
				: positionToPlace;
	}

	private boolean faceSideBlock(AIHelper aiHelper, PlacingDirection direction) {
		if (facePos == null) {
			facePos = direction.getRandomPoint(faceCentered);
			faceCentered += .2;
		}
		if (aiHelper.face(facePos)) {
			facePos = null;
			return true;
		} else {
			return false;
		}

		// TODO: lookingDirection
		// TODO: aiHelper.getMinecraft().player.posX -
		// getPositionToPlaceAt().getX(),
		// TODO: aiHelper.getMinecraft().player.posZ -
		// getPositionToPlaceAt().getZ(),
	}

	private boolean isFacing(AIHelper aiHelper, PlacingDirection d) {
		return d.isFacing(aiHelper);
	}

	@Override
	protected boolean isFacingRightBlock(AIHelper aiHelper) {
		for (final PlacingDirection d : getBuildDirs(aiHelper)) {
			if (isFacing(aiHelper, d)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "SneakAndPlaceAtHalfTask [side=" + blockHalf
				+ ", lookingDirection=" + lookingDirection + ", attempts="
				+ attempts + ", positionToPlace=" + positionToPlace
				+ ", destinationStandPosition=" + destinationStandPosition
				+ ", startStandPosition=" + startStandPosition + ", filter="
				+ filter + "]";
	}
}
