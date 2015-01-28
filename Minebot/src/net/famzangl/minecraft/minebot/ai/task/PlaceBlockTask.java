package net.famzangl.minecraft.minebot.ai.task;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.BlockItemFilter;
import net.famzangl.minecraft.minebot.ai.task.error.SelectTaskError;
import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

/**
 * Place a block on a given side of an other block. Useful for e.g. attaching a
 * torch on a wall.
 * 
 * @author michael
 * 
 */
public class PlaceBlockTask extends AITask {
	private final BlockPos placeOn;
	private final EnumFacing onSide;
	private int attemptsLeft = 20;
	private final Block block;

	/**
	 * 
	 * @param placeOn
	 *            Where to place the block on (the existing block to click).
	 * @param onSide
	 *            On which side the block should be placed.
	 * @param block
	 *            The Block to place.
	 */
	public PlaceBlockTask(BlockPos placeOn, EnumFacing onSide, Block block) {
		super();
		if (placeOn == null || onSide == null || block == null) {
			throw new NullPointerException();
		}
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
