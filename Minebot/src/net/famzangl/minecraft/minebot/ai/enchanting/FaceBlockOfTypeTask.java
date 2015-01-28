package net.famzangl.minecraft.minebot.ai.enchanting;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.strategy.TaskOperations;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

public class FaceBlockOfTypeTask extends AITask {

	@Override
	public boolean isFinished(AIHelper h) {
		final BlockPos pos = h.findBlock(Blocks.enchanting_table);
		return pos != null && h.isFacingBlock(pos.getX(), pos.getY(), pos.getZ());
	}

	@Override
	public void runTick(AIHelper h, TaskOperations o) {
		final BlockPos pos = h.findBlock(Blocks.enchanting_table);
		if (pos == null) {
			System.out.println("Could not find block around player.");
		}

		h.face(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
	}

}
