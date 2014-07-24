package net.famzangl.minecraft.minebot.ai.task;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.minecraft.block.Block;

public class UseItemOnBlockTask extends UseItemTask {

	private final Block[] allowedBlocks;

	public UseItemOnBlockTask(Block... allowedBlocks) {
		this.allowedBlocks = allowedBlocks;
	}

	@Override
	protected boolean isBlockAllowed(AIHelper h, int blockX, int blockY,
			int blockZ) {
		final Block block = h.getBlock(blockX, blockY, blockZ);
		return AIHelper.blockIsOneOf(block, allowedBlocks);
	}

}
