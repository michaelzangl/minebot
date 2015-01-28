package net.famzangl.minecraft.minebot.ai.task.move;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.BlockWhitelist;
import net.famzangl.minecraft.minebot.ai.ItemFilter;
import net.famzangl.minecraft.minebot.ai.task.TaskOperations;
import net.famzangl.minecraft.minebot.ai.task.error.PositionTaskError;
import net.famzangl.minecraft.minebot.ai.task.place.JumpingPlaceBlockAtFloorTask;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

/**
 * Move upwards by digging one block up and then jumping while placing a block
 * below the feet.
 * 
 * @author michael
 *
 */
public class UpwardsMoveTask extends JumpingPlaceBlockAtFloorTask {
	private boolean obsidianMining;

	/**
	 * FIXME: Find a nice, central place for digging times.
	 */
	private static final BlockWhitelist hardBlocks = new BlockWhitelist(
			Blocks.obsidian);

	public UpwardsMoveTask(BlockPos pos, ItemFilter filter) {
		super(pos, filter);
	}

	@Override
	public void runTick(AIHelper h, TaskOperations o) {
		if (!h.isAirBlock(pos.add(0, 1, 0))) {
			if (!h.isStandingOn(pos.add(0, -1, 0))) {
				o.desync(new PositionTaskError(pos.add(0, -1, 0)));
			}
			if (hardBlocks.contains(h.getBlock(pos.add(0, 1, 0)))) {
				obsidianMining = true;
			}
			h.faceAndDestroy(pos.add(0, 1, 0));
		} else if (h.canWalkOn(h.getBlock(pos.add(0, -1, 0)))
				&& !h.isAirBlock(pos.add(0, -1, 0))) {
			h.faceAndDestroy(pos.add(0, -1, 0));
		} else {
			super.runTick(h, o);
		}
	}

	@Override
	public int getGameTickTimeout() {
		return super.getGameTickTimeout()
				+ (obsidianMining ? HorizontalMoveTask.OBSIDIAN_TIME : 0);
	}

	@Override
	public String toString() {
		return "UpwardsMoveTask [pos=" + pos + "]";
	}

}
