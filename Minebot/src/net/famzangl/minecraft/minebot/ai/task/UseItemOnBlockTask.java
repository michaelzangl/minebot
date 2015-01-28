package net.famzangl.minecraft.minebot.ai.task;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.BlockWhitelist;
import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;

public class UseItemOnBlockTask extends UseItemTask {

	private final BlockWhitelist allowedBlocks;

	public UseItemOnBlockTask(BlockWhitelist allowedBlocks) {
		this.allowedBlocks = allowedBlocks;
	}

	@Override
	protected boolean isBlockAllowed(AIHelper h, BlockPos pos) {
		final Block block = h.getBlock(pos);
		return allowedBlocks.contains(block);
	}

}
