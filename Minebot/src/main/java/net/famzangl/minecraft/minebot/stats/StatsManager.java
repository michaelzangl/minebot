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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import net.famzangl.minecraft.minebot.ai.command.BlockWithData;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSets;
import net.famzangl.minecraft.minebot.ai.path.world.WorldData;
import net.minecraft.util.math.BlockPos;

/**
 * This is the entry point for the stats.
 * 
 * @author Michael Zangl
 */
public class StatsManager {

	private static final long INTENTIONAL_BLOCK_BREAK_TICKS = 20 * 3;

	private static class IntentionalBreak {

		private long gameTickTimer;
		private BlockWithData block;
		private BlockPos pos;

		public IntentionalBreak(long gameTickTimer, BlockWithData block,
				BlockPos pos) {
			this.gameTickTimer = gameTickTimer;
			this.block = block;
			this.pos = pos;
		}

	}

	private long gameTickTimer = 0;

	private BlockBreakStats blockStats = new BlockBreakStats();

	private HashMap<BlockPos, IntentionalBreak> intentionalBreaks = new HashMap<BlockPos, IntentionalBreak>();

	private WorldData world;

	public StatsManager() {
	}

	public void setGameTickTimer(WorldData minecraftWorld) {
		world = minecraftWorld;
		long newTickTimer = minecraftWorld.getWorldTime();
		if (this.gameTickTimer != newTickTimer) {
			long clearIntentionalBreaksBefore = newTickTimer
					- INTENTIONAL_BLOCK_BREAK_TICKS;
			if (this.gameTickTimer + 1 != newTickTimer) {
				// probably a teleport somewhere.
				markWorldChange();
				clearIntentionalBreaksBefore = 0;
			}
			this.gameTickTimer = newTickTimer;

			for (Iterator<Entry<BlockPos, IntentionalBreak>> iterator = intentionalBreaks
					.entrySet().iterator(); iterator.hasNext();) {
				Entry<BlockPos, IntentionalBreak> e = iterator.next();
				if (e.getValue().gameTickTimer < clearIntentionalBreaksBefore) {
					iterator.remove();
				} else if (BlockSets.AIR.isAt(world, e.getKey())) {
					blockDisappearedInClient(e.getValue());
					iterator.remove();
				}
			}
			blockStats.nextGameTick();
		}
	}

	/**
	 * Called whenever the world was (probably) changed or whenever a teleport
	 * occurred.
	 */
	public synchronized void markWorldChange() {
	}

	/**
	 * Marks that the bot is intentionally attempting breaking a block at the
	 * position.
	 * 
	 * @param pos
	 */
	public synchronized void markIntentionalBlockBreak(BlockPos pos) {
		BlockWithData block = world.getBlock(pos);
		if (!BlockSets.AIR.isAt(world, pos)) {
			intentionalBreaks.put(pos, new IntentionalBreak(gameTickTimer,
					block, pos));
		}
	}

	private synchronized void blockDisappearedInClient(IntentionalBreak intentionalBreak) {
		blockStats.addBlockBreak(intentionalBreak.pos, intentionalBreak.block);
	}

	public synchronized BlockBreakStats getBlockStats() {
		return blockStats;
	}
}
