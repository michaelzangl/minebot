package net.famzangl.minecraft.minebot.build;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.BlockItemFilter;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.minecraft.init.Blocks;
import net.minecraft.util.MovementInput;

public class WalkTowardsTask implements AITask {

	private Pos fromPos;
	private Pos nextPos;

	private AITask subTask;

	public WalkTowardsTask(Pos fromPos, Pos nextPos) {
		this.fromPos = fromPos;
		this.nextPos = nextPos;
	}

	@Override
	public boolean isFinished(AIHelper h) {
		return subTask == null
				&& h.isStandingOn(nextPos.x, nextPos.y, nextPos.z)
			/*	&& getUpperCarpetY(h) < 0*/;
	}

	@Override
	public void runTick(AIHelper h) {
		if (subTask != null && subTask.isFinished(h)) {
			subTask = null;
		}
		if (subTask != null) {
			subTask.runTick(h);
		} else {
			int carpetY = getUpperCarpetY(h);
			double carpetBuildHeight = h.realBlockTopY(fromPos.x,
					Math.max(carpetY + 1, fromPos.y), fromPos.z);
			double destHeight = h
					.realBlockTopY(nextPos.x, nextPos.y, nextPos.z);
			if (carpetBuildHeight < destHeight - 1) {
				System.out.println("Moving upwards. Carpets are at " + carpetY);
				h.faceBlock(fromPos.x, Math.max(carpetY, fromPos.y - 1),
						fromPos.z);
				if (h.selectCurrentItem(new BlockItemFilter(Blocks.carpet))
						&& h.isFacingBlock(fromPos.x,
								Math.max(carpetY, fromPos.y - 1), fromPos.z, 1)) {
					h.overrideUseItem();
				}
				MovementInput i = new MovementInput();
				i.jump = true;
				h.overrideMovement(i);
			} else {
				h.walkTowards(nextPos.x + 0.5, nextPos.z + 0.5, carpetBuildHeight < destHeight - 0.5);
			}
			//TODO: Clean up carpets
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
