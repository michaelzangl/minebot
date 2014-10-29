package net.famzangl.minecraft.minebot.ai.animals;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.ClassItemFilter;
import net.famzangl.minecraft.minebot.ai.strategy.TaskOperations;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.minecraft.item.ItemFishingRod;

public class ThrowFishingRodTask extends AITask {

	private int time = 4;
	
	@Override
	public boolean isFinished(AIHelper h) {
		return h.getMinecraft().thePlayer.fishEntity != null && time < 1;
	}

	@Override
	public void runTick(AIHelper h, TaskOperations o) {
		if (h.selectCurrentItem(new ClassItemFilter(ItemFishingRod.class))) {
			time--;
			if (time == 2) {
				h.overrideUseItem();
			}
		}
	}
	
	@Override
	public int getGameTickTimeout() {
		return 950;
	}

}
