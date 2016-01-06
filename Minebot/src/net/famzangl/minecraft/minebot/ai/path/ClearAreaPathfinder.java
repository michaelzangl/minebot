/*******************************************************************************
 * This file is part of Minebot.
 *
 * Minebot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Minebot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Minebot.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package net.famzangl.minecraft.minebot.ai.path;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.command.BlockWithDataOrDontcare;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSets;
import net.famzangl.minecraft.minebot.ai.path.world.WorldData;
import net.famzangl.minecraft.minebot.ai.task.DestroyInRangeTask;
import net.famzangl.minecraft.minebot.ai.utils.BlockArea.AreaVisitor;
import net.famzangl.minecraft.minebot.ai.utils.BlockCuboid;
import net.famzangl.minecraft.minebot.ai.utils.BlockFilteredArea;
import net.famzangl.minecraft.minebot.ai.utils.BlockIntersection;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

public class ClearAreaPathfinder extends MovePathFinder {
	public enum ClearMode {
		VISIT_EVERY_POS(6, 0), CLEAR_3X5X3(5, 1), CLEAR_3X2X3(2, 1), CLEAR_5X4X5(4, 2);

		public final int maxHeight, maxExtendXZ;

		ClearMode(int maxHeight, int maxExtendX) {
			this.maxHeight = maxHeight;
			this.maxExtendXZ = maxExtendX;
		}
	}

	private int topY;
	private final BlockCuboid area;
	private ClearMode mode;

	private static final BlockSet CLEARED_BLOCKS = new BlockSet(Blocks.air,
			Blocks.torch);

	private final BlockSet clearedBlocks;

	public ClearAreaPathfinder(BlockCuboid area,
			BlockWithDataOrDontcare block, ClearMode mode) {
		this.area = area;
		this.mode = mode;
		topY = area.getMax().getY();
		if (block == null) {
			clearedBlocks = CLEARED_BLOCKS;
		} else {
			clearedBlocks = block.toBlockSet().invert();
		}
	}

	@Override
	protected float rateDestination(int distance, int x, int y, int z) {
		if (isInArea(x, y, z)
				&& (!isTemporaryCleared(x, y, z) || !isTemporaryCleared(x,
						y + 1, z) && y < area.getMax().getY())) {
			final float bonus = 0.0001f * (x - area.getMin().getX()) + 0.001f
					* (y - area.getMin().getY());
			int layerMalus;
			if (topY <= y) {
				layerMalus = 5;
			} else if (!isInArea(x, y + 1, z)
					|| isTemporaryCleared(x, y + 1, z)) {
				layerMalus = 2;
			} else if (isInArea(x, y + 1, z)
					&& !isTemporaryCleared(x, y + 2, z)) {
				layerMalus = 2;
			} else {
				layerMalus = 0;
			}
			return distance + bonus + layerMalus + (area.getMax().getY() - y)
					* 2;
		} else {
			return -1;
		}
	}

	private boolean isTemporaryCleared(int x, int y, int z) {
		return isClearedBlock(world, x, y, z);
	}

	private boolean isInArea(int x, int y, int z) {
		return area.contains(world, x, y, z);
	}

	protected boolean isClearedBlock(WorldData world, int x, int y, int z) {
		return clearedBlocks.isAt(world, x, y, z);
	}

	@Override
	protected void addTasksForTarget(BlockPos currentPos) {
		super.addTasksForTarget(currentPos);
		BlockPos top = currentPos;
		for (int i = 1; i < mode.maxHeight; i++) {
			final BlockPos pos = currentPos.add(0, i, 0);
			if (pos.getY() <= area.getMax().getY()) {
				top = pos;
			}
		}
		// avoid gravel/...
		while (!BlockSets.SAFE_CEILING.isAt(world, top)
				&& top.getY() > currentPos.getY() + 1) {
			top = top.add(0, -1, 0);
		}
		BlockCuboid range = new BlockCuboid(currentPos, top);
		range = range.extendXZ(mode.maxExtendXZ);
		BlockIntersection clamped = range.intersectWith(new BlockFilteredArea(
				area, clearedBlocks.invert()));

		addTask(new DestroyInRangeTask(clamped));
	}

	@Override
	protected int materialDistance(int x, int y, int z, boolean asFloor) {
		return isToBeClearedMaterial(x, y, z) ? 0 : super.materialDistance(x, y, z, asFloor);
	}

	private boolean isToBeClearedMaterial(int x, int y, int z) {
		return isInArea(x, y, z) && !clearedBlocks.isAt(world, x, y, z);
	}

	public int getAreaSize() {
		return area.getVolume();
	}

	private class AreaTopVisitor implements AreaVisitor {
		private int count = 0;
		private int newTopY;

		public AreaTopVisitor(int initialTopY) {
			newTopY = initialTopY;
		}

		@Override
		public void visit(WorldData world, int x, int y, int z) {
			if (!isClearedBlock(world, x, y, z)) {
				count++;
				newTopY = Math.max(y, newTopY);
			}
		}

		@Override
		public String toString() {
			return "AreaTopVisitor [count=" + count + ", newTopY=" + newTopY
					+ "]";
		}
	}

	public int getToClearCount(AIHelper helper) {
		WorldData world = helper.getWorld();
		AreaTopVisitor v = new AreaTopVisitor(area.getMin().getY());
		area.accept(v, world);
		topY = v.newTopY;
		System.out.println("top Y:  " + v);
		return v.count;
	}
}
