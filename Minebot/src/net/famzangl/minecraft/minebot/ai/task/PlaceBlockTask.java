package net.famzangl.minecraft.minebot.ai.task;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.BlockItemFilter;
import net.famzangl.minecraft.minebot.ai.strategy.TaskOperations;
import net.famzangl.minecraft.minebot.ai.task.error.SelectTaskError;
import net.minecraft.block.Block;
import net.minecraft.util.EnumFacing;

/**
 * Place a block somewhere within reach
 * 
 * @author michael
 * 
 */
public class PlaceBlockTask extends AITask {
	private final Pos placeOn;
	private final EnumFacing onSide;
	private int attemptsLeft = 20;
	private final Block block;

	public PlaceBlockTask(Pos placeOn, EnumFacing onSide, Block block) {
		super();
		this.placeOn = placeOn;
		this.onSide = onSide;
		this.block = block;
	}

	@Override
	public boolean isFinished(AIHelper h) {
		return attemptsLeft <= 0
				|| Block.isEqualTo(h.getBlock(placeOn.offset(onSide)), block);
	}

	@Override
	public void runTick(AIHelper h, TaskOperations o) {
		final BlockItemFilter f = new BlockItemFilter(block);
		if (!h.selectCurrentItem(f)) {
			o.desync(new SelectTaskError(f));
		}

		h.faceSideOf(placeOn, onSide);
		if (h.isFacingBlock(placeOn, onSide)) {
			h.overrideUseItem();
		}
		attemptsLeft--;
	}

}
