package net.famzangl.minecraft.minebot.ai.task.move;

import java.util.List;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.strategy.TaskOperations;

public class JumpMoveTask extends HorizontalMoveTask {

	private final int oldX;
	private final int oldZ;

	public JumpMoveTask(int x, int y, int z, int oldX, int oldZ) {
		super(x, y, z);
		this.oldX = oldX;
		this.oldZ = oldZ;
	}

	@Override
	protected boolean doJump(AIHelper h) {
		// Pos player = h.getPlayerPosition();
		// return player.x != x && player.z != z;
		return true;
	}

	@Override
	public void runTick(AIHelper h, TaskOperations o) {
		if (!h.isAirBlock(oldX, y + 1, oldZ)) {
			h.faceAndDestroy(oldX, y + 1, oldZ);
		} else {
			super.runTick(h, o);
		}
	}

	@Override
	public String toString() {
		return "JumpMoveTask [oldX=" + oldX + ", oldZ=" + oldZ + ", x=" + x
				+ ", y=" + y + ", z=" + z + "]";
	}

	@Override
	public List<Pos> getPredestroyPositions(AIHelper helper) {
		final List<Pos> list = super.getPredestroyPositions(helper);
		list.add(0, new Pos(oldX, y + 1, oldZ));
		return list;
	}
}
