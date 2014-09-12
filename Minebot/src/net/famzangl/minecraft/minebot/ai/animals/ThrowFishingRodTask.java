package net.famzangl.minecraft.minebot.ai.animals;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.ItemFilter;
import net.famzangl.minecraft.minebot.ai.strategy.TaskOperations;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemStack;

public class ThrowFishingRodTask extends AITask {

	private int time = 4;
	
	private final static class FishingRodFilter implements ItemFilter {
		@Override
		public boolean matches(ItemStack itemStack) {
			return itemStack != null
					&& itemStack.getItem() instanceof ItemFishingRod;
		}
	}

	@Override
	public boolean isFinished(AIHelper h) {
		return h.getMinecraft().thePlayer.fishEntity != null && time > 3;
	}

	@Override
	public void runTick(AIHelper h, TaskOperations o) {
		if (h.selectCurrentItem(new FishingRodFilter())) {
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
