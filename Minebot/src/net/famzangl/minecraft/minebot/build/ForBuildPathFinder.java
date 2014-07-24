package net.famzangl.minecraft.minebot.build;

import java.util.Arrays;
import java.util.LinkedList;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.path.MovePathFinder;
import net.famzangl.minecraft.minebot.ai.task.move.AlignToGridTask;
import net.famzangl.minecraft.minebot.build.blockbuild.BuildTask;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

public class ForBuildPathFinder extends MovePathFinder {

	private static final int NEIGHBOURS_PER_DIRECTION = 6;
	private static final Block[] FENCES = new Block[] { Blocks.fence,
			Blocks.cobblestone_wall };
	/**
	 * Task we want to prepare for.
	 */
	private final BuildTask task;
	int[] res = new int[NEIGHBOURS_PER_DIRECTION * 4];

	public ForBuildPathFinder(AIHelper helper, BuildTask task) {
		super(helper);
		this.task = task;
	}

	@Override
	protected int[] getNeighbours(int currentNode) {
		Arrays.fill(res, -1);
		final int cx = getX(currentNode);
		final int cz = getZ(currentNode);
		getNeighbours(res, 0 * NEIGHBOURS_PER_DIRECTION, currentNode, cx + 1,
				cz);
		getNeighbours(res, 1 * NEIGHBOURS_PER_DIRECTION, currentNode, cx - 1,
				cz);
		getNeighbours(res, 2 * NEIGHBOURS_PER_DIRECTION, currentNode, cx,
				cz + 1);
		getNeighbours(res, 3 * NEIGHBOURS_PER_DIRECTION, currentNode, cx,
				cz - 1);
		return res;
	}

	private void getNeighbours(int[] fill, int offset, int currentNode, int x,
			int z) {
		final int cy = getY(currentNode);
		for (int y = cy + 1; y < cy + 4; y++) {
			if (!helper.isAirBlock(getX(currentNode), y + 1, getZ(currentNode))) {
				break;
			}
			fill[offset++] = getNeighbour(currentNode, x, y, z);
		}

		if (helper.isAirBlock(x, cy + 1, z)) {
			for (int y = cy; y > cy - 3; y--) {
				if (!helper.isAirBlock(x, y, z)) {
					break;
				}
				fill[offset++] = getNeighbour(currentNode, x, y, z);
			}
		}
	}

	@Override
	protected boolean isForbiddenBlock(Block block) {
		return !(helper.canWalkOn(block) || helper.canWalkThrough(block));
	}

	@Override
	protected boolean checkGroundBlock(int currentNode, int cx, int cy, int cz) {
		return helper.isSafeGroundBlock(cx, cy - 1, cz)
				|| helper.isAirBlock(cx, cy - 1, cz)
				&& AIHelper.blockIsOneOf(helper.getBlock(cx, cy - 2, cz),
						FENCES);
	}

	@Override
	protected int distanceFor(int from, int to) {
		if (getY(from) + 1 < getY(to)) {
			return 1 + Math.abs(getY(from) - getY(to)) * 4;
		} else {
			return 1 + Math.abs(getY(from) - getY(to));
		}
	}

	@Override
	protected float rateDestination(int distance, int x, int y, int z) {
		if (task.couldBuildFrom(helper, x, y, z)) {
			return distance;
		}
		return -1;
	}

	@Override
	protected void foundPath(LinkedList<Pos> path) {
		Pos currentPos = path.removeFirst();
		helper.addTask(new AlignToGridTask(currentPos.x, currentPos.y,
				currentPos.z));
		while (!path.isEmpty()) {
			final Pos nextPos = path.removeFirst();
			helper.addTask(new WalkTowardsTask(currentPos, nextPos));
			currentPos = nextPos;
		}
	}

}
