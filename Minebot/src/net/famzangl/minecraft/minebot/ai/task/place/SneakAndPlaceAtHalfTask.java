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

import java.util.Arrays;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.BlockItemFilter;
import net.famzangl.minecraft.minebot.ai.path.world.BlockBounds;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSets;
import net.famzangl.minecraft.minebot.ai.task.BlockHalf;
import net.famzangl.minecraft.minebot.ai.task.TaskOperations;
import net.famzangl.minecraft.minebot.ai.task.error.StringTaskError;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

public class SneakAndPlaceAtHalfTask extends SneakAndPlaceTask {

	protected final BlockHalf blockHalf;

	protected EnumFacing lookingDirection = null;

	protected final EnumFacing[] DIRS = new EnumFacing[] { EnumFacing.EAST,
			EnumFacing.NORTH, EnumFacing.WEST, EnumFacing.SOUTH };

	private int attempts;

	private final BlockPos positionToPlace;

	private Vec3 facePos;

	private double faceCentered = .1;

	protected EnumFacing[] getBuildDirs() {
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
	protected boolean faceBlock(AIHelper h, TaskOperations o) {
		final EnumFacing[] dirs = getBuildDirs();
		attempts++;
		boolean success = false;
		for (int i = 0; i < dirs.length; i++) {
			final EnumFacing useSide = dirs[attempts / 10 % dirs.length];
			if (!BlockSets.AIR.isAt(h.getWorld(),
					getPositionToPlaceAt().add(useSide.getDirectionVec()))) {
				success = faceSideBlock(h, useSide);
				attempts++;
				break;
			} else {
				attempts += 10;
				faceCentered = .1;
				facePos = null;
			}
		}
		if (attempts > 90) {
			o.desync(new StringTaskError("Could not face anywhere to place."));
		}
		return success;
	}

	@Override
	protected BlockPos getPositionToPlaceAt() {
		return positionToPlace == null ? super.getPositionToPlaceAt()
				: positionToPlace;
	}

	private boolean faceSideBlock(AIHelper h, EnumFacing useSide) {
		if (facePos == null) {
			BlockPos placeOn = getPositionToPlaceAt().add(
					useSide.getDirectionVec());
			BlockBounds bounds = h.getWorld().getBlockBounds(placeOn);
			BlockBounds faceArea = bounds.clampY(
					blockHalf == BlockHalf.UPPER_HALF ? 0.5 : 0,
					blockHalf == BlockHalf.LOWER_HALF ? 0.4 : 1 // <- 0.5 but
																// craftbukkit
																// has a problem
																// with that.
					).onlySide(useSide.getOpposite());
			facePos = faceArea.random(placeOn, Math.min(.9, faceCentered));
			faceCentered += .2;
		}
		if (h.face(facePos)) {
			facePos = null;
			return true;
		} else {
			return false;
		}

		// TODO: lookingDirection
		// TODO: h.getMinecraft().thePlayer.posX -
		// getPositionToPlaceAt().getX(),
		// TODO: h.getMinecraft().thePlayer.posZ -
		// getPositionToPlaceAt().getZ(),
	}

	private boolean isFacing(AIHelper h, EnumFacing useSide) {
		return h.isFacingBlock(
				getPositionToPlaceAt().add(useSide.getDirectionVec()),
				useSide.getOpposite(), blockHalf);
	}

	@Override
	protected boolean isFacingRightBlock(AIHelper h) {
		for (final EnumFacing d : getBuildDirs()) {
			if (isFacing(h, d)) {
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
