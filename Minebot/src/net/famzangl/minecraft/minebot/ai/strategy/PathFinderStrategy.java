package net.famzangl.minecraft.minebot.ai.strategy;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.AIStrategy;
import net.famzangl.minecraft.minebot.ai.path.HealthWatcher;
import net.famzangl.minecraft.minebot.ai.path.MovePathFinder;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.famzangl.minecraft.minebot.ai.task.DestroyBlockTask;
import net.famzangl.minecraft.minebot.ai.task.WaitTask;
import net.minecraft.block.Block;

public class PathFinderStrategy implements AIStrategy {
	private MovePathFinder pathFinder;
	private String description;
	private HealthWatcher watcher = new HealthWatcher();

	public PathFinderStrategy(MovePathFinder pathFinder, String description) {
		this.pathFinder = pathFinder;
		this.description = description;
	}

	@Override
	public void searchTasks(AIHelper helper) {
		if (!pathFinder.searchSomethingAround(helper.getPlayerPosition())) {
			helper.addTask(WaitTask.instance);
		}
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public AITask getOverrideTask(AIHelper helper) {
		Pos pos = helper.getPlayerPosition();
		Block headBlock = helper.getBlock(pos.x, pos.y + 1, pos.z);
		if (!helper.canWalkThrough(headBlock)) {
			return new DestroyBlockTask(pos.x, pos.y + 1, pos.z);
		}
		Block floorBlock = helper.getBlock(pos.x, pos.y, pos.z);
		if (!helper.canWalkOn(floorBlock)) {
			return new DestroyBlockTask(pos.x, pos.y, pos.z);
		}
		return watcher.getOverrideTask(helper.getMinecraft().thePlayer.getHealth());
	}
}
