package net.famzangl.minecraft.minebot.ai.path;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;

public class ClearAreaPathfinder extends MovePathFinder {

	private Pos minPos;
	private Pos maxPos;

	public ClearAreaPathfinder(AIHelper helper) {
		super(helper);
		minPos = Pos.minPos(helper.getPos1(), helper.getPos2());
		maxPos = Pos.maxPos(helper.getPos1(), helper.getPos2());
	}

	@Override
	protected float rateDestination(int distance, int x, int y, int z) {
		if (minPos.x <= x && x <= maxPos.x && minPos.y <= y && y <= maxPos.y
				&& minPos.z <= z && z <= maxPos.z && (!helper.isAirBlock(x, y, z) || !helper.isAirBlock(x, y + 1, z))) {
			return distance + ((maxPos.y == y) ? 5 : (maxPos.y - y) * 3);
		} else {
			return -1;
		}
	}
}
