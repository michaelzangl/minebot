package net.famzangl.minecraft.minebot.ai.animals;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.ItemFilter;
import net.famzangl.minecraft.minebot.ai.strategy.TaskOperations;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemStack;

public class ThrowFishingRodTask extends AITask {

	private final static class FishingRodFilter implements ItemFilter {
		@Override
		public boolean matches(ItemStack itemStack) {
			return itemStack != null
					&& itemStack.getItem() instanceof ItemFishingRod;
		}
	}

	@Override
	public boolean isFinished(AIHelper h) {
		return h.getMinecraft().thePlayer.fishEntity != null;
	}

	@Override
	public void runTick(AIHelper h, TaskOperations o) {
		if (h.selectCurrentItem(new FishingRodFilter())) {
			h.overrideUseItem();
		}
	}

}
