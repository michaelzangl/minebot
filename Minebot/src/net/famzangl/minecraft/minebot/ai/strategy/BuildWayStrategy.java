package net.famzangl.minecraft.minebot.ai.strategy;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.path.BuildWayPathfinder;

public class BuildWayStrategy extends PathFinderStrategy {


	public BuildWayStrategy(BuildWayPathfinder pathfinder) {
		super(pathfinder, "Build a way.");
	}

	@Override
	public String getDescription(AIHelper helper) {
		return "Build way";
	}

	@Override
	public void searchTasks(AIHelper helper) {
		super.searchTasks(helper);
	}

}
