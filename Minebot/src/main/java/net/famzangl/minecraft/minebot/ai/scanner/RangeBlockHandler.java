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
package net.famzangl.minecraft.minebot.ai.scanner;

import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSets;
import net.famzangl.minecraft.minebot.ai.path.world.WorldData;
import net.famzangl.minecraft.minebot.ai.scanner.BlockRangeScanner.BlockHandler;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map.Entry;

public abstract class RangeBlockHandler<ReachData> implements BlockHandler {
	private static final BlockSet THROUGH_REACHABLE = BlockSet.builder().add(
			Blocks.TORCH).add(BlockSets.AIR).build();
	private final Hashtable<BlockPos, ArrayList<ReachData>> reachable = new Hashtable<BlockPos, ArrayList<ReachData>>();

	@Override
	public void scanningDone(WorldData world) {
		updatePositionCache(world);
	}

	protected abstract Collection<Entry<BlockPos, ReachData>> getTargetPositions();

	private void updatePositionCache(WorldData world) {
		reachable.clear();
		for (Entry<BlockPos, ReachData> c : getTargetPositions()) {
			addPositionToCache(world, c.getKey(), c.getValue());
		}
	}

	protected void addPositionToCache(WorldData world, BlockPos pos, ReachData c) {
		for (Direction d : new Direction[] { Direction.NORTH,
				Direction.SOUTH, Direction.EAST, Direction.WEST }) {
			addPositions(world, pos, c, d);
		}
	}

	private void addPositions(WorldData world, BlockPos pos, ReachData c,
			Direction d) {
		int dvertMax = 4;
		for (int dhor = 0; dhor < 4; dhor++) {
			int y = pos.getY() - dhor;
			for (int dvert = 1; dvert <= dvertMax; dvert++) {
				int x = pos.getX() + dvert * d.getXOffset();
				int z = pos.getZ() + dvert * d.getZOffset();
				if (!THROUGH_REACHABLE.isAt(world, x, y, z)) {
					dvertMax = dvert;
				} else if (dvert > 1) {
					BlockPos allowed = new BlockPos(x, y, z);
					addReachable(allowed, c);
				}
			}
		}
	}

	private void addReachable(BlockPos allowed, ReachData c) {
		ArrayList<ReachData> list = reachable.get(allowed);
		if (list == null) {
			list = new ArrayList<ReachData>();
			reachable.put(allowed, list);
		}
		list.add(c);
	}

	public ArrayList<ReachData> getReachableForPos(BlockPos pos) {
		return reachable.get(pos);
	}

}
