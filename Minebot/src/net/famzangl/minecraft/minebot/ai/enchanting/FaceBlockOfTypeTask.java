package net.famzangl.minecraft.minebot.ai.enchanting;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.minecraft.init.Blocks;

public class FaceBlockOfTypeTask implements AITask {

	@Override
	public boolean isFinished(AIHelper h) {
		Pos pos = h.findBlock(Blocks.enchanting_table);
		return pos != null && h.isFacingBlock(pos.x, pos.y, pos.z);
	}

	@Override
	public void runTick(AIHelper h) {
		Pos pos = h.findBlock(Blocks.enchanting_table);
		if (pos == null) {
			System.out.println("Could not find block around player.");
		}

		h.face(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5);
	}

}
