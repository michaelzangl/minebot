package net.famzangl.minecraft.minebot.ai.strategy;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.AIStrategy;
import net.famzangl.minecraft.minebot.ai.path.BuildWayPathfinder;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.famzangl.minecraft.minebot.ai.task.WaitTask;
import net.minecraftforge.common.util.ForgeDirection;

public class BuildWayStrategy implements AIStrategy {

	private BuildWayPathfinder pathfinder;
	private final ForgeDirection dir;
	private final Pos pos;

	public BuildWayStrategy(ForgeDirection dir, Pos pos) {
		this.dir = dir;
		this.pos = pos;
	}

	@Override
	public String getDescription() {
		return "Build way";
	}

	@Override
	public void searchTasks(AIHelper helper) {
		if (pathfinder == null) {
			boolean onSlab = true;
			pathfinder = new BuildWayPathfinder(helper, dir.offsetX,
					dir.offsetZ, pos.x, pos.y + 1, pos.z);
		}
		if (!pathfinder.addContinuingTask(helper.getPlayerPosition()) && !pathfinder
				.searchSomethingAround(helper.getPlayerPosition())) {
			helper.addTask(WaitTask.instance);
		}
	}

	@Override
	public AITask getOverrideTask(AIHelper helper) {
		return null;
	}

}
