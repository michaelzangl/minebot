package net.famzangl.minecraft.minebot.ai.task.move;

import java.util.List;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.strategy.TaskOperations;
import net.minecraft.util.BlockPos;

public class JumpMoveTask extends HorizontalMoveTask {

	private final int oldX;
	private final int oldZ;

	public JumpMoveTask(BlockPos pos, int oldX, int oldZ) {
		super(pos);
		this.oldX = oldX;
		this.oldZ = oldZ;
	}

	@Override
	protected boolean doJump(AIHelper h) {
		Pos player = h.getPlayerPosition();
		return player.getX() != pos.getX() || player.getZ() != pos.getZ();
	}

	@Override
	public void runTick(AIHelper h, TaskOperations o) {
		if (!h.canWalkThrough(h.getBlock(oldX, pos.getY() + 1, oldZ))) {
			h.faceAndDestroy(new BlockPos(oldX, pos.getY() + 1, oldZ));
		} else {
			super.runTick(h, o);
		}
	}

	@Override
	public String toString() {
		return "JumpMoveTask [oldX=" + oldX + ", oldZ=" + oldZ + ", pos=" + pos
				+ "]";
	}

	@Override
	public List<BlockPos> getPredestroyPositions(AIHelper helper) {
		final List<BlockPos> list = super.getPredestroyPositions(helper);
		list.add(0, new BlockPos(oldX, pos.getY() + 1, oldZ));
		return list;
	}
}
