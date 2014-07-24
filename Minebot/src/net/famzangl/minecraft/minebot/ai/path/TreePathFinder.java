package net.famzangl.minecraft.minebot.ai.path;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.task.MineBlockTask;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

public class TreePathFinder extends MovePathFinder {
	public TreePathFinder(AIHelper helper) {
		super(helper);
	}

	private static final int TREE_HEIGHT = 7;

	@Override
	protected float rateDestination(int distance, int x, int y, int z) {
		int points = 0;
		if (isTree(x, y, z)) {
			points++;
		}
		if (isTree(x, y + 1, z)) {
			points++;
		}
		for (int i = 2; i < TREE_HEIGHT; i++) {
			if (!(helper.hasSafeSides(x, y + i, z) && helper.isSafeHeadBlock(x,
					y + i + 1, z))) {
				break;
			} else if (isTree(x, y + i, z)) {
				points++;
			}
		}

		return points == 0 ? -1 : distance + 20 - points * 2;
	}

	private boolean isTree(int x, int y, int z) {
		final Block block = helper.getBlock(x, y, z);
		return Block.isEqualTo(block, Blocks.log)
				|| Block.isEqualTo(block, Blocks.log2);
	}

	@Override
	protected void addTasksForTarget(Pos currentPos) {
		int mineAbove = 0;

		for (int i = 2; i < TREE_HEIGHT; i++) {
			if (isTree(currentPos.x, currentPos.y + i, currentPos.z)) {
				mineAbove = i;
			}
		}
		for (int i = 2; i <= mineAbove; i++) {
			if (!helper.hasSafeSides(currentPos.x, currentPos.y + i,
					currentPos.z)
					|| !helper.isSafeHeadBlock(currentPos.x, currentPos.y + i
							+ 1, currentPos.z)) {
				break;
			}
			if (!helper
					.isAirBlock(currentPos.x, currentPos.y + i, currentPos.z)) {
				helper.addTask(new MineBlockTask(currentPos.x,
						currentPos.y + i, currentPos.z));
			}
		}
	}
}
