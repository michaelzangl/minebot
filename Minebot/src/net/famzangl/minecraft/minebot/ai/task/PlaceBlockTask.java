package net.famzangl.minecraft.minebot.ai.task;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.BlockItemFilter;
import net.famzangl.minecraft.minebot.ai.strategy.TaskOperations;
import net.famzangl.minecraft.minebot.ai.task.error.SelectTaskError;
import net.minecraft.block.Block;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Place a block somewhere within reach
 * 
 * @author michael
 * 
 */
public class PlaceBlockTask extends AITask {
	private final Pos placeOn;
	private final ForgeDirection onSide;
	private int attemptsLeft = 20;
	private final Block block;

	public PlaceBlockTask(Pos placeOn, ForgeDirection onSide, Block block) {
		super();
		this.placeOn = placeOn;
		this.onSide = onSide;
		this.block = block;
	}

	@Override
	public boolean isFinished(AIHelper h) {
		return attemptsLeft <= 0
				|| Block.isEqualTo(h.getBlock(placeOn.add(onSide.offsetX,
						onSide.offsetY, onSide.offsetZ)), block);
	}

	@Override
	public void runTick(AIHelper h, TaskOperations o) {
		final BlockItemFilter f = new BlockItemFilter(block);
		if (!h.selectCurrentItem(f)) {
			o.desync(new SelectTaskError(f));
		}

		h.faceSideOf(placeOn.x, placeOn.y, placeOn.z, onSide);
		if (h.isFacingBlock(placeOn.x, placeOn.y, placeOn.z, onSide)) {
			h.overrideUseItem();
		}
		attemptsLeft--;
	}

}
