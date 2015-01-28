package net.famzangl.minecraft.minebot.ai.strategy;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.ClassItemFilter;
import net.famzangl.minecraft.minebot.ai.ItemFilter;
import net.famzangl.minecraft.minebot.ai.command.AIChatController;
import net.minecraft.item.ItemFood;

/**
 * Eat as soon as you are hungry.
 * 
 * @author michael
 *
 */
public class EatStrategy extends AIStrategy {
	// TODO: Is this really what we want?
	private static final ItemFilter FILTER = new ClassItemFilter(ItemFood.class);
	private boolean failed;

	@Override
	public boolean checkShouldTakeOver(AIHelper helper) {
		return needFood(helper) && helper.canSelectItem(FILTER);
	}

	private boolean needFood(AIHelper helper) {
		return helper.getMinecraft().thePlayer.getFoodStats().needFood();
	}

	@Override
	protected TickResult onGameTick(AIHelper helper) {
		if (needFood(helper)) {
			if (helper.selectCurrentItem(FILTER)) {
				helper.overrideSneak();
				helper.overrideUseItem();
				failed = false;
				return TickResult.TICK_HANDLED;
			} else {
				AIChatController.addChatLine("Could not find anything to eat");
				failed = true;
				return TickResult.NO_MORE_WORK;
			}
		} else {
			failed = false;
			return TickResult.NO_MORE_WORK;
		}
	}

	@Override
	public boolean hasFailed() {
		return failed;
	}

	@Override
	public String getDescription(AIHelper helper) {
		return "Eat.";
	}
}
