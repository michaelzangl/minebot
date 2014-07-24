package net.famzangl.minecraft.minebot.ai.strategy;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.AIStrategy;
import net.famzangl.minecraft.minebot.ai.AIStrategyFactory;
import net.famzangl.minecraft.minebot.ai.animals.ThrowFishingRodTask;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.famzangl.minecraft.minebot.ai.task.DoFishTask;

public class FishStrategy implements AIStrategy, AIStrategyFactory {

	@Override
	public void searchTasks(AIHelper helper) {
		if (helper.getMinecraft().thePlayer.fishEntity == null) {
			helper.addTask(new ThrowFishingRodTask());
		} else {
			helper.addTask(new DoFishTask());
		}
	}

	@Override
	public String getDescription() {
		return "Fishing";
	}

	@Override
	public AITask getOverrideTask(AIHelper helper) {
		return null;
	}

	@Override
	public AIStrategy produceStrategy(AIHelper helper) {
		return this;
	}

}
