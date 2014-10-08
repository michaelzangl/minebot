package net.famzangl.minecraft.minebot.ai.strategy;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.ItemFilter;
import net.famzangl.minecraft.minebot.ai.command.AIChatController;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;

public class EatStrategy extends AIStrategy {
	private static final ItemFilter FILTER = new ItemFilter() {
		
		@Override
		public boolean matches(ItemStack itemStack) {
			return itemStack != null && itemStack.getItem() instanceof ItemFood;
		}
	};

	@Override
	public boolean checkShouldTakeOver(AIHelper helper) {
		return needFood(helper) && helper.canSelectItem(FILTER);
	}

	private boolean needFood(AIHelper helper) {
		return helper.getMinecraft().thePlayer.getFoodStats().needFood();
	}

	@Override
	protected TickResult onGameTick(AIHelper helper) {
		if (needFood(helper))  {
			if (helper.selectCurrentItem(FILTER)) {
				helper.overrideSneak();
				helper.overrideUseItem();
				return TickResult.TICK_HANDLED;
			} else {
				AIChatController.addChatLine("Could not find anything to eat");
				return TickResult.NO_MORE_WORK;
			}
		} else {
			return TickResult.NO_MORE_WORK;
		}
	}
	
	@Override
	public String getDescription(AIHelper helper) {
		return "Eat.";
	}
}
