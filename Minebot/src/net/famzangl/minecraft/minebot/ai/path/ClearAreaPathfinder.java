package net.famzangl.minecraft.minebot.ai.path;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.task.DestroyInRangeTask;
import net.minecraft.init.Blocks;

public class ClearAreaPathfinder extends MovePathFinder {

	private final Pos minPos;
	private final Pos maxPos;
	private int topY;

	public ClearAreaPathfinder(AIHelper helper) {
		super(helper);
		minPos = Pos.minPos(helper.getPos1(), helper.getPos2());
		maxPos = Pos.maxPos(helper.getPos1(), helper.getPos2());
		topY = maxPos.y;
	}

	@Override
	protected float rateDestination(int distance, int x, int y, int z) {
		if (isInArea(x, y, z)
				&& (!isClearedBlock(x, y, z) || (!isClearedBlock(x,
						y + 1, z) && y < maxPos.y))) {
			float bonus = 0.0001f * (x - minPos.x) + 0.001f * (y - minPos.y);
			return distance + bonus + (topY <= y ? 5 : (maxPos.y - y) * 3);
		} else {
			return -1;
		}
	}

	private boolean isInArea(int x, int y, int z) {
		return minPos.x <= x
				&& x <= maxPos.x
				&& minPos.y <= y
				&& y <= maxPos.y
				&& minPos.z <= z
				&& z <= maxPos.z;
	}

	private boolean isClearedBlock(int x, int y, int z) {
		return AIHelper.blockIsOneOf(helper.getBlock(x, y, z), Blocks.air, Blocks.torch);
	}

	@Override
	protected void addTasksForTarget(Pos currentPos) {
		super.addTasksForTarget(currentPos);
		Pos top = currentPos;
		for (int i = 1; i < 6; i++) {
			Pos pos = currentPos.add(0, i, 0);
			if (pos.y <= maxPos.y) {
				top = pos;
			}
		}
		helper.addTask(new DestroyInRangeTask(currentPos, top));
	}
	
	@Override
	protected int materialDistance(int x, int y, int z, boolean asFloor) {
		return isInArea(x, y, z) ? 0 : super.materialDistance(x, y, z, asFloor);
	}

	public int getAreaSize() {
		return (maxPos.x - minPos.x + 1) * (maxPos.y - minPos.y + 1)
				* (maxPos.z - minPos.z + 1);
	}

	public float getToClearCount() {
		int count = 0;
		int newTopY = minPos.y;
		for (int y = minPos.y; y <= maxPos.y; y++) {
			for (int z = minPos.z; z <= maxPos.z; z++) {
				for (int x = minPos.x; x <= maxPos.x; x++) {
					if (!isClearedBlock(x, y, z)) {
						count++;
						newTopY = Math.max(y, newTopY);
					}
				}
			}
		}
		topY = newTopY;
		return count;
	}
}
