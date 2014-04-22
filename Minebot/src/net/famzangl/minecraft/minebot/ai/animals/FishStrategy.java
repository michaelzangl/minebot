package net.famzangl.minecraft.minebot.ai.animals;


import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.AIStrategy;
import net.famzangl.minecraft.minebot.ai.task.AITask;

public class FishStrategy implements AIStrategy {

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

}
