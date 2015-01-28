package net.famzangl.minecraft.minebot.ai.task;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.ItemFilter;
import net.minecraft.util.BlockPos;

/**
 * Use the given item at the block at the given position.
 * 
 * @author michael
 *
 */
public class UseItemOnBlockAtTask extends UseItemTask {

	private final BlockPos pos;

	public UseItemOnBlockAtTask(ItemFilter filter, BlockPos pos) {
		super(filter);
		this.pos = pos;
	}

	public UseItemOnBlockAtTask(BlockPos pos) {
		super();
		this.pos = pos;
	}

	@Override
	public String toString() {
		return "UseItemOnBlockAtTask [pos=" + pos + "]";
	}

	@Override
	protected boolean isBlockAllowed(AIHelper h, BlockPos pos) {
		return this.pos.equals(pos);
	}

	@Override
	protected void notFacingBlock(AIHelper h) {
		h.faceBlock(pos);
	}
}
