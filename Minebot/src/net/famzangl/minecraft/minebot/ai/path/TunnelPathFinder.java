package net.famzangl.minecraft.minebot.ai.path;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.task.DestroyInRangeTask;

public class TunnelPathFinder extends AlongTrackPathFinder {

	private final int addToSide;
	private final int addToTop;

	public TunnelPathFinder(AIHelper helper, int dx, int dz, int cx, int cy,
			int cz, int addToSide, int addToTop) {
		super(helper, dx, dz, cx, cy, cz);
		this.addToSide = addToSide;
		this.addToTop = addToTop;
	}

	@Override
	protected float rateDestination(int distance, int x, int y, int z) {
		if (isOnTrack(x, z) && y == cy && !helper.isAirBlock(x, y, z)) {
			return distance + 1;
		} else {
			return -1;
		}
	}

	@Override
	protected void addTasksForTarget(Pos currentPos) {
		Pos p1, p2;
		if (dx == 0) {
			p1 = new Pos(currentPos.x + addToSide, currentPos.y, currentPos.z);
			p2 = new Pos(currentPos.x - addToSide, currentPos.y + 1 + addToTop, currentPos.z);
		} else {
			p1 = new Pos(currentPos.x, currentPos.y, currentPos.z + addToSide);
			p2 = new Pos(currentPos.x, currentPos.y + 1 + addToTop, currentPos.z - addToSide);
		}
		helper.addTask(new DestroyInRangeTask(p1, p2));
	}

	@Override
	public String toString() {
		return "TunnelPathFinder [addToSide=" + addToSide + ", addToTop="
				+ addToTop + ", dx=" + dx + ", dz=" + dz + ", cx=" + cx
				+ ", cy=" + cy + ", cz=" + cz + "]";
	}

}
