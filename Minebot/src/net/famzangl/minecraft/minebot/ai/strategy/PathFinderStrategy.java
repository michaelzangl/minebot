package net.famzangl.minecraft.minebot.ai.strategy;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.path.MovePathFinder;
import net.famzangl.minecraft.minebot.ai.task.WaitTask;

public class PathFinderStrategy extends TaskStrategy {
	private final MovePathFinder pathFinder;
	private final String description;

	// private final HealthWatcher watcher = new HealthWatcher();

	public PathFinderStrategy(MovePathFinder pathFinder, String description) {
		this.pathFinder = pathFinder;
		this.description = description;
	}

	@Override
	public void searchTasks(AIHelper helper) {
		if (!pathFinder.searchSomethingAround(helper.getPlayerPosition(),
				helper, this)) {
			addTask(new WaitTask(1));
		}
	}

	@Override
	public String getDescription() {
		return description;
	}

	// @Override
	// public AITask getOverrideTask(AIHelper helper) {
	// final Pos pos = helper.getPlayerPosition();
	// final Block headBlock = helper.getBlock(pos.x, pos.y + 1, pos.z);
	// if (!helper.canWalkThrough(headBlock)) {
	// return new DestroyBlockTask(pos.x, pos.y + 1, pos.z);
	// }
	// final Block floorBlock = helper.getBlock(pos.x, pos.y, pos.z);
	// if (!helper.canWalkOn(floorBlock)) {
	// return new DestroyBlockTask(pos.x, pos.y, pos.z);
	// }
	// return watcher.getOverrideTask(helper.getMinecraft().thePlayer
	// .getHealth());
	// }

	@Override
	public String toString() {
		return "PathFinderStrategy [pathFinder=" + pathFinder
				+ ", description=" + description + "]";
	}

}
