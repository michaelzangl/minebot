package net.famzangl.minecraft.minebot.ai.strategy;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.path.MovePathFinder;
import net.famzangl.minecraft.minebot.ai.render.PosMarkerRenderer;
import net.famzangl.minecraft.minebot.ai.task.WaitTask;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;

public class PathFinderStrategy extends TaskStrategy {
	private final MovePathFinder pathFinder;
	private final String description;
	private boolean inShouldTakeOver;
	private boolean noPathFound;
	private final PosMarkerRenderer renderer = new PosMarkerRenderer(255, 128, 0);;

	// private final HealthWatcher watcher = new HealthWatcher();

	public PathFinderStrategy(MovePathFinder pathFinder, String description) {
		this.pathFinder = pathFinder;
		this.description = description;
	}

	@Override
	public void searchTasks(AIHelper helper) {
		if (!pathFinder.searchSomethingAround(helper.getPlayerPosition(),
				helper, this)) {
			// Path finding needs more time
			if (!(noPathFound && inShouldTakeOver)) {
				addTask(new WaitTask(1));
			}
		} else {
			if (!hasMoreTasks()) {
				noPathFound = true;
			}
		}
	}

	@Override
	public boolean checkShouldTakeOver(AIHelper helper) {
		inShouldTakeOver = true;
		try {
			return super.checkShouldTakeOver(helper);
		} finally {
			inShouldTakeOver = false;
		}
	}

	@Override
	public String getDescription(AIHelper helper) {
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

	@Override
	public void drawMarkers(RenderTickEvent event, AIHelper helper) {
		Pos target = pathFinder.getCurrentTarget();
		if (target != null) {
			renderer.render(event, helper, target, target.add(0, 1, 0));
		}
		super.drawMarkers(event, helper);
	}

}
