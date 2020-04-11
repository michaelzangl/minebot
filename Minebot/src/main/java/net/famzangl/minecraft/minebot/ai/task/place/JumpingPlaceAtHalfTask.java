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
import net.famzangl.minecraft.minebot.ai.ItemFilter;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSets;
import net.famzangl.minecraft.minebot.ai.task.BlockHalf;
import net.famzangl.minecraft.minebot.ai.task.TaskOperations;
import net.famzangl.minecraft.minebot.ai.task.error.StringTaskError;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

/**
 * Place a block at the given upper/lower side of its adjacent blocks by
 * standing at the place where the block should go, then jumping up and placing
 * the block while being in the air.
 * 
 * @author michael
 *
 */
public class JumpingPlaceAtHalfTask extends JumpingPlaceBlockAtFloorTask {
	private static final Marker MARKER_JUMPING_PLACE_HALF = MarkerManager.getMarker("jumping_place_half");

	public final static Direction[] TRY_FOR_LOWER = new Direction[] {
			Direction.DOWN, Direction.EAST, Direction.NORTH,
			Direction.WEST, Direction.SOUTH };

	public final static Direction[] TRY_FOR_UPPER = new Direction[] {
			Direction.EAST, Direction.NORTH, Direction.WEST,
			Direction.SOUTH };

	protected BlockHalf side;
	protected Direction lookingDirection = null;

	private int attempts;

	public JumpingPlaceAtHalfTask(BlockPos pos, ItemFilter filter,
			BlockHalf side) {
		super(pos, filter);
		this.side = side;
	}

	@Override
	protected void faceBlock(AIHelper aiHelper, TaskOperations taskOperations) {
		final Direction[] dirs = getBuildDirs();
		for (int i = 0; i < dirs.length; i++) {
			if (faceSideBlock(aiHelper, dirs[attempts++ % dirs.length])) {
				return;
			}
		}
		taskOperations.desync(new StringTaskError("Could not face anywhere to place."));
	}

	protected Direction[] getBuildDirs() {
		return side == BlockHalf.UPPER_HALF ? TRY_FOR_UPPER : TRY_FOR_LOWER;
	}

	protected boolean faceSideBlock(AIHelper aiHelper, Direction dir) {
		LOGGER.trace(MARKER_JUMPING_PLACE_HALF, "Facing side " + dir);
		BlockPos facingBlock = getPlaceAtPos().offset(dir);
		if (BlockSets.AIR.isAt(aiHelper.getWorld(), facingBlock)) {
			return false;
		} else {
			aiHelper.faceSideOf(facingBlock, dir.getOpposite(),
					getSide(dir) == BlockHalf.UPPER_HALF ? 0.5 : 0,
					getSide(dir) == BlockHalf.LOWER_HALF ? 0.5 : 1,
					aiHelper.getMinecraft().player.getPosX() - pos.getX(),
					aiHelper.getMinecraft().player.getPosZ() - pos.getZ(),
					lookingDirection);
			return true;
		}
	}

	protected BlockHalf getSide(Direction dir) {
		return dir == Direction.DOWN ? BlockHalf.UPPER_HALF
				: BlockHalf.LOWER_HALF;
	}

	@Override
	protected boolean isFacingRightBlock(AIHelper aiHelper) {
		for (final Direction d : getBuildDirs()) {
			if (isFacing(aiHelper, d)) {
				return true;
			}
		}
		return false;
	}
}
