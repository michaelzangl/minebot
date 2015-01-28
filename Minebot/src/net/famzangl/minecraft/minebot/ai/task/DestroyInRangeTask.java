package net.famzangl.minecraft.minebot.ai.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.minecraft.util.BlockPos;

/**
 * Destroys all Blocks in a given area. Individual blocks can be excluded, see
 * {@link #blacklist(BlockPos)}
 * 
 * @author michael
 *
 */
public class DestroyInRangeTask extends AITask implements CanPrefaceAndDestroy {
	private final BlockPos minPos;
	private final BlockPos maxPos;
	private int facingAttempts;
	private final ArrayList<BlockPos> blacklist = new ArrayList<BlockPos>();

	/**
	 * Create a new {@link DestroyInRangeTask}.
	 * 
	 * @param p1
	 *            One corner
	 * @param p2
	 *            The other corner
	 */
	public DestroyInRangeTask(BlockPos p1, BlockPos p2) {
		minPos = Pos.minPos(p1, p2);
		maxPos = Pos.maxPos(p1, p2);
	}

	private BlockPos getNextToDestruct(AIHelper h) {
		BlockPos next = null;
		double currentMin = Float.POSITIVE_INFINITY;

		for (int x = minPos.getX(); x <= maxPos.getX(); x++) {
			for (int y = minPos.getY(); y <= maxPos.getY(); y++) {
				for (int z = minPos.getZ(); z <= maxPos.getZ(); z++) {
					final double rating = rate(h, x, y, z);
					if (rating >= 0 && rating < currentMin) {
						next = new BlockPos(x, y, z);
						currentMin = rating;
					}
					// System.out.println(String.format("%d, %d, %d: %f", x, y,
					// z,
					// (float) rating));
				}
			}
		}

		return next;
	}

	private double rate(AIHelper h, int x, int y, int z) {
		if (!isSafeToDestroy(h, x, y, z)
				|| blacklist.contains(new Pos(x, y, z))) {
			return -1;
		} else {
			return h.getMinecraft().thePlayer.getDistanceSq(x + .5, y + .5,
					z + .5);
		}
	}

	private boolean isSafeToDestroy(AIHelper h, int x, int y, int z) {
		Pos pos = h.getPlayerPosition();
		return !h.isAirBlock(x, y, z)
				&& h.hasSafeSides(x, y, z)
				&& (h.isSafeHeadBlock(x, y + 1, z) || ((x != pos.getX() || y != pos
						.getY()) && isSafeFallingBlock(h, x, y + 1, z)));
	}

	private boolean isSafeFallingBlock(AIHelper h, int x, int y, int z) {
		return AIHelper.fallingBlocks.contains(h.getBlock(x, y, z))
				&& isSafeToDestroy(h, x, y, z)
				|| AIHelper.walkableBlocks.contains(h.getBlock(x, y, z));
	}

	@Override
	public boolean isFinished(AIHelper h) {
		return getNextToDestruct(h) == null;
	}

	@Override
	public void runTick(AIHelper h, TaskOperations o) {
		BlockPos n = getNextToDestruct(h);
		if (facingAttempts > 20) {
			blacklist.add(n);
			n = getNextToDestruct(h);
		}
		if (n != null) {
			if (h.isFacingBlock(n)) {
				h.faceAndDestroy(n);
				facingAttempts = 0;
			} else {
				h.faceBlock(n);
				facingAttempts++;
			}
		}
	}

	@Override
	public int getGameTickTimeout() {
		return 100 * (Math.abs(minPos.getX() - maxPos.getX()) + 1)
				* (Math.abs(minPos.getY() - maxPos.getY()) + 1)
				* (Math.abs(minPos.getZ() - maxPos.getZ()) + 1);
	}

	@Override
	public String toString() {
		return "DestroyInRangeTask [minPos=" + minPos + ", maxPos=" + maxPos
				+ ", facingAttempts=" + facingAttempts + ", blacklist="
				+ blacklist + "]";
	}

	/**
	 * Add a {@link BlockPos} to the list of blocks that should be excluded from
	 * this area.
	 * 
	 * @param pos
	 */
	public void blacklist(BlockPos pos) {
		blacklist.add(pos);
	}

	@Override
	public List<BlockPos> getPredestroyPositions(AIHelper helper) {
		final BlockPos next = getNextToDestruct(helper);
		return next != null ? Arrays.asList(next) : Collections
				.<BlockPos> emptyList();
	}
}
