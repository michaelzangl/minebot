package net.famzangl.minecraft.minebot.ai.task.place;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.BlockItemFilter;
import net.famzangl.minecraft.minebot.ai.strategy.TaskOperations;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.famzangl.minecraft.minebot.ai.task.error.SelectTaskError;
import net.minecraft.util.MovementInput;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Sneak towards (x, y, z) and then place the block below you.
 * 
 * @author michael
 * 
 */
public class SneakAndPlaceTask extends AITask {

	protected final int x;
	protected final int y;
	protected final int z;
	protected final BlockItemFilter filter;
	protected final Pos relativeFrom;
	/**
	 * Direction we need to walk.
	 */
	private final ForgeDirection inDirection;
	private final double minBuildHeight;
	private int faceTimer;

	/**
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param filter
	 * @param relativeFrom
	 *            Vector: place position -> start standing pos.
	 * @param minBuildHeight
	 */
	public SneakAndPlaceTask(int x, int y, int z, BlockItemFilter filter,
			Pos relativeFrom, double minBuildHeight) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.filter = filter;
		this.relativeFrom = relativeFrom;
		this.minBuildHeight = minBuildHeight;
		final ForgeDirection foundInDir = AIHelper.getDirectionForXZ(
				-relativeFrom.x, -relativeFrom.z);
		if (relativeFrom.y != 1 || foundInDir == null) {
			throw new IllegalArgumentException();
		}
		inDirection = foundInDir;
	}

	@Override
	public boolean isFinished(AIHelper h) {
		return !h.isAirBlock(x, y - 1, z) && !h.isJumping();
	}

	@Override
	public void runTick(AIHelper h, TaskOperations o) {
		if (faceTimer > 0) {
			faceTimer--;
		}
		if (h.sneakFrom(x + relativeFrom.x, y - 1, z + relativeFrom.z,
				inDirection)) {
			final boolean hasRequiredHeight = h.getMinecraft().thePlayer.boundingBox.minY > minBuildHeight - 0.05;
			if (hasRequiredHeight) {
				if (faceTimer == 0) {
					faceBlock(h, o);
					faceTimer = 3;
				} else if (isFacingRightBlock(h)) {
					if (h.selectCurrentItem(filter)) {
						h.overrideUseItem();
					} else {
						o.desync(new SelectTaskError(filter));
					}
				}
			} else {
				final MovementInput i = new MovementInput();
				i.jump = true;
				h.overrideMovement(i);
			}
		}
	}

	protected boolean isFacingRightBlock(AIHelper h) {
		return h.isFacingBlock(x + relativeFrom.x, y - 1, z + relativeFrom.z,
				inDirection);
	}

	protected void faceBlock(AIHelper h, TaskOperations o) {
		h.faceSideOf(x + relativeFrom.x, y - 1, z + relativeFrom.z, inDirection);
	}

	@Override
	public String toString() {
		return "SneakAndPlaceTask [x=" + x + ", y=" + y + ", z=" + z
				+ ", filter=" + filter + ", relativeFrom=" + relativeFrom
				+ ", inDirection=" + inDirection + ", minBuildHeight="
				+ minBuildHeight + "]";
	}
}
