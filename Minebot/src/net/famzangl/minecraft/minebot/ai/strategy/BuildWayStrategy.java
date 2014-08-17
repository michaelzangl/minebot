package net.famzangl.minecraft.minebot.ai.strategy;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.path.BuildWayPathfinder;

public class BuildWayStrategy extends PathFinderStrategy {

	private final BuildWayPathfinder pathfinder;

	public BuildWayStrategy(BuildWayPathfinder pathfinder) {
		super(pathfinder, "Build a way.");
		this.pathfinder = pathfinder;
	}

	@Override
	public String getDescription() {
		return "Build way";
	}

	@Override
	public void searchTasks(AIHelper helper) {
		if (!pathfinder.addContinuingTask(helper.getPlayerPosition())) {
			super.searchTasks(helper);
		}
	}

}
