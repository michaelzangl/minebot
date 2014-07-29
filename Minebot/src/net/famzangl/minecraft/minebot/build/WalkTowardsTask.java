package net.famzangl.minecraft.minebot.build;

import java.util.LinkedList;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.BlockItemFilter;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.minecraft.init.Blocks;
import net.minecraft.util.MovementInput;

public class WalkTowardsTask extends AITask {

	private static final BlockItemFilter CARPET = new BlockItemFilter(
			Blocks.carpet);
	private final Pos fromPos;
	private final Pos nextPos;

	private AITask subTask;

	private final LinkedList<Pos> carpets = new LinkedList<Pos>();
	private boolean wasStandingOnDest;

	public WalkTowardsTask(Pos fromPos, Pos nextPos) {
		this.fromPos = fromPos;
		this.nextPos = nextPos;
	}

	@Override
	public boolean isFinished(AIHelper h) {
		return subTask == null
				&& h.isStandingOn(nextPos.x, nextPos.y, nextPos.z)
				&& carpets.isEmpty();
		/* && getUpperCarpetY(h) < 0 */
	}

	@Override
	public void runTick(AIHelper h) {
		if (subTask != null && subTask.isFinished(h)) {
			subTask = null;
		}
		if (subTask != null) {
			subTask.runTick(h);
		} else {
			final int carpetY = getUpperCarpetY(h);
			final double carpetBuildHeight = h.realBlockTopY(fromPos.x,
					Math.max(carpetY + 1, fromPos.y), fromPos.z);
			final double destHeight = h.realBlockTopY(nextPos.x, nextPos.y,
					nextPos.z);
			if (carpetBuildHeight < destHeight - 1) {
				System.out.println("Moving upwards. Carpets are at " + carpetY);
				int floorY = Math.max(carpetY, fromPos.y - 1);
				h.faceBlock(fromPos.x, floorY, fromPos.z);
				if (h.isFacingBlock(fromPos.x, floorY, fromPos.z, 1)) {
					if (h.selectCurrentItem(CARPET)) {
						h.overrideUseItem();
						carpets.add(new Pos(fromPos.x, floorY + 1, fromPos.z));
					} else {
						h.buildManager.missingItem(CARPET);
					}
				}
				final MovementInput i = new MovementInput();
				i.jump = true;
				h.overrideMovement(i);
			} else if ((h.isStandingOn(nextPos.x, nextPos.y, nextPos.z) || wasStandingOnDest)
					&& !carpets.isEmpty()) {
				// Destruct everything after arriving at dest. Then walk to dest
				// again.

				while (!carpets.isEmpty()) {
					// Clean up carpets we already "lost"
					Pos last = carpets.getLast();
					if (h.isAirBlock(last.x, last.y, last.z)) {
						carpets.removeLast();
					}
				}

				int x = fromPos.x - nextPos.x;
				int z = fromPos.x - nextPos.x;
				if (h.sneakFrom(nextPos.x, nextPos.y, nextPos.z,
						AIHelper.getDirectionForXZ(x, z))) {
					Pos last = carpets.getLast();
					h.faceAndDestroy(last.x, last.y, last.z);
				}

				wasStandingOnDest = true;
			} else {
				h.walkTowards(nextPos.x + 0.5, nextPos.z + 0.5,
						carpetBuildHeight < destHeight - 0.5);
			}
		}
	}

	/**
	 * Gets the Y of the topmost carpet that was placed. -1 if there was none.
	 * 
	 * @param h
	 * @return
	 */
	private int getUpperCarpetY(AIHelper h) {
		int upperCarpet = -1;
		for (int y = AIHelper.blockIsOneOf(
				h.getBlock(fromPos.x, fromPos.y, fromPos.z), Blocks.air,
				Blocks.carpet) ? fromPos.y : fromPos.y + 1; y < nextPos.y; y++) {
			if (AIHelper.blockIsOneOf(h.getBlock(fromPos.x, y, fromPos.z),
					Blocks.carpet)) {
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
