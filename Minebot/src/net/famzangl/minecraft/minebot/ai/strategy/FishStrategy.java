package net.famzangl.minecraft.minebot.ai.strategy;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.animals.ThrowFishingRodTask;
import net.famzangl.minecraft.minebot.ai.task.DoFishTask;
import net.famzangl.minecraft.minebot.ai.task.WaitTask;

public class FishStrategy extends TaskStrategy {

	@Override
	public void searchTasks(AIHelper helper) {
		if (helper.getMinecraft().thePlayer.fishEntity == null) {
			addTask(new ThrowFishingRodTask());
			addTask(new WaitTask(10));
		} else {
			addTask(new DoFishTask());
		}
	}

	@Override
	public String getDescription() {
		return "Fishing";
	}

}
