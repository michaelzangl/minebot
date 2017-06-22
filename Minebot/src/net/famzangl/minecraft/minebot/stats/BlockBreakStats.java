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
package net.famzangl.minecraft.minebot.stats;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.famzangl.minecraft.minebot.ai.command.BlockWithData;
import net.famzangl.minecraft.minebot.ai.path.world.WorldData;
import net.minecraft.util.math.BlockPos;

/**
 * A recorder of the block breaks.
 * 
 * @author michael
 *
 */
public class BlockBreakStats {
	public static interface BlockBreakStatsChangeListener {

		void blockStatsChanged();

	}

	public class BlockBreakStatsSlice {
		private int start;
		private int end;

		public BlockBreakStatsSlice(int start, int end) {
			this.start = start;
			this.end = end;
		}

		public float getAverage() {
			float sum = 0;
			for (int i = start; i < end; i++) {
				sum += entries.get(i).getCount();
			}
			return sum / (end - start);
		}
	}

	public static class BlockBreakStatEntry {

		private List<BlockBreakStatBreak> breaks;

		public BlockBreakStatEntry(List<BlockBreakStatBreak> toAdd) {
			this.breaks = toAdd;
		}

		public int getCount() {
			return breaks.size();
		}

	}

	private static class BlockBreakStatBreak {
		private BlockPos pos;
		private BlockWithData block;

		public BlockBreakStatBreak(BlockPos pos, BlockWithData block) {
			this.pos = pos;
			this.block = block;
		}
	}

	private static final int TICKS_PER_SECOND = 20;
	private int ticksCollected = 0;

	private ArrayList<BlockBreakStatEntry> entries = new ArrayList<BlockBreakStatEntry>();

	private ArrayList<BlockBreakStatBreak> currentTickEntries = new ArrayList<BlockBreakStatBreak>();
	private CopyOnWriteArrayList<BlockBreakStatsChangeListener> listeners = new CopyOnWriteArrayList<BlockBreakStatsChangeListener>();

	public synchronized void nextGameTick() {
		ticksCollected++;
		if (ticksCollected >= TICKS_PER_SECOND) {
			ticksCollected = 0;
			List<BlockBreakStatBreak> toAdd;
			if (currentTickEntries.size() == 0) {
				toAdd = Collections.<BlockBreakStatBreak> emptyList();
			} else {
				toAdd = currentTickEntries;
				currentTickEntries = new ArrayList<BlockBreakStatBreak>();
			}
			entries.add(new BlockBreakStatEntry(toAdd));
			for (BlockBreakStatsChangeListener l : listeners) {
				l.blockStatsChanged();
			}
		}
	}

	public void addBlockBreak(WorldData world, BlockPos pos) {
		BlockWithData block = world.getBlock(pos);
		addBlockBreak(pos, block);
	}

	public synchronized void addBlockBreak(BlockPos pos, BlockWithData block) {
		currentTickEntries.add(new BlockBreakStatBreak(pos, block));
	}

	public synchronized ArrayList<BlockBreakStatEntry> getEntries() {
		return entries;
	}

	public void addChangeListener(BlockBreakStatsChangeListener listener) {
		listeners.add(listener);
	}

	public synchronized BlockBreakStatsSlice getStatsSlice(int secondsInPast) {
		int start = entries.size() - secondsInPast;
		start = clampToRange(start, entries.size() - 1);
		int end = clampToRange(start + secondsInPast, entries.size());
		return new BlockBreakStatsSlice(start, end);
	}

	private int clampToRange(int start, int max) {
		if (start >= max) {
			start = max;
		} else if (start < 0) {
			start = 0;
		}
		return start;
	}
}
