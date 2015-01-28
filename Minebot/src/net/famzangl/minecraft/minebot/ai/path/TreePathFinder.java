package net.famzangl.minecraft.minebot.ai.path;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.BlockWhitelist;
import net.famzangl.minecraft.minebot.ai.task.MineBlockTask;
import net.famzangl.minecraft.minebot.ai.task.WaitTask;
import net.famzangl.minecraft.minebot.ai.task.place.ReplantTask;
import net.famzangl.minecraft.minebot.build.WoodType;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

public class TreePathFinder extends MovePathFinder {
	private static final BlockWhitelist TREE_STUFF = new BlockWhitelist(Blocks.log, Blocks.log2, Blocks.leaves, Blocks.leaves2);
	private final WoodType type;
	private final boolean replant;

	public TreePathFinder(WoodType type, boolean replant) {
		this.type = type;
		this.replant = replant;
		shortFootBlocks = shortFootBlocks.unionWith(TREE_STUFF);
		shortHeadBlocks = shortHeadBlocks.unionWith(TREE_STUFF);
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
		if (type == null) {
			return Block.isEqualTo(block, Blocks.log)
					|| Block.isEqualTo(block, Blocks.log2);
		} else {
			return Block.isEqualTo(block, type.block)
					&& (0x3 & helper.getBlockIdWithMeta(
							x, y, z) & 0xf) == type.lowerBits;
		}
	}

	@Override
	protected void addTasksForTarget(Pos currentPos) {
		int mineAbove = 0;

		for (int i = 2; i < TREE_HEIGHT; i++) {
			if (isTree(currentPos.getX(), currentPos.getY() + i, currentPos.getZ())) {
				mineAbove = i;
			}
		}
		for (int i = 2; i <= mineAbove; i++) {
			if (!helper.hasSafeSides(currentPos.getX(), currentPos.getY() + i,
					currentPos.getZ())
					|| !helper.isSafeHeadBlock(currentPos.getX(), currentPos.getY() + i
							+ 1, currentPos.getZ())) {
				break;
			}
			if (!helper
					.isAirBlock(currentPos.getX(), currentPos.getY() + i, currentPos.getZ())) {
				addTask(new MineBlockTask(currentPos.add(0,i,0)));
			}
		}
		if (replant) {
			addTask(new ReplantTask(currentPos));
		}
		addTask(new WaitTask(mineAbove * 2));
	}
}
