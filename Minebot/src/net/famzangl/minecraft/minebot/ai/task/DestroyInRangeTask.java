package net.famzangl.minecraft.minebot.ai.task;

import java.util.ArrayList;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;

public class DestroyInRangeTask extends AITask {

	private final Pos minPos;
	private final Pos maxPos;
	private int facingAttempts;
	private final ArrayList<Pos> blacklist = new ArrayList<Pos>();

	public DestroyInRangeTask(Pos p1, Pos p2) {
		minPos = Pos.minPos(p1, p2);
		maxPos = Pos.maxPos(p1, p2);
	}

	private Pos getNextToDestruct(AIHelper h) {
		Pos next = null;
		double currentMin = Float.POSITIVE_INFINITY;

		for (int x = minPos.x; x <= maxPos.x; x++) {
			for (int y = minPos.y; y <= maxPos.y; y++) {
				for (int z = minPos.z; z <= maxPos.z; z++) {
					double rating = rate(h, x, y, z);
					if (rating >= 0 && rating < currentMin) {
						next = new Pos(x, y, z);
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
		return !h.isAirBlock(x, y, z)
				&& h.hasSafeSides(x, y, z)
				&& (h.isSafeHeadBlock(x, y + 1, z) || isSafeFallingBlock(h, x,
						y + 1, z));
	}

	private boolean isSafeFallingBlock(AIHelper h, int x, int y, int z) {
		return AIHelper.blockIsOneOf(h.getBlock(x, y, z),
				AIHelper.fallingBlocks)
				&& isSafeToDestroy(h, x, y, z)
				|| AIHelper.blockIsOneOf(h.getBlock(x, y, z),
						AIHelper.walkableBlocks);
	}

	@Override
	public boolean isFinished(AIHelper h) {
		return getNextToDestruct(h) == null;
	}

	@Override
	public void runTick(AIHelper h) {
		Pos n = getNextToDestruct(h);
		if (facingAttempts > 20) {
			blacklist.add(n);
			n = getNextToDestruct(h);
		}
		if (n != null) {
			if (h.isFacingBlock(n.x, n.y, n.z)) {
				h.faceAndDestroy(n.x, n.y, n.z);
				facingAttempts = 0;
			} else {
				h.faceBlock(n.x, n.y, n.z);
				facingAttempts++;
			}
		}
	}

	@Override
	public int getGameTickTimeout() {
		return 100 * (Math.abs(minPos.x - maxPos.x) + 1)
				* (Math.abs(minPos.y - maxPos.y) + 1)
				* (Math.abs(minPos.z - maxPos.z) + 1);
	}

	@Override
	public String toString() {
		return "DestroyInRangeTask [minPos=" + minPos + ", maxPos=" + maxPos
				+ ", facingAttempts=" + facingAttempts + ", blacklist="
				+ blacklist + "]";
	}

	public void blacklist(Pos pos) {
		blacklist.add(pos);
	}
}
