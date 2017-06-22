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

import java.util.ArrayList;

import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.famzangl.minecraft.minebot.ai.path.world.WorldData;
import net.famzangl.minecraft.minebot.ai.utils.BlockArea.AreaVisitor;
import net.famzangl.minecraft.minebot.ai.utils.BlockCuboid;
import net.minecraft.util.math.BlockPos;

public class BlockRangeScanner {
	private static final int HORIZONTAL_SCAN = 100;
	private static final int VERTICAL_SCAN = 20;
	private final BlockPos center;
	
	public interface BlockHandler {
		BlockSet getIds();

		void scanningDone(WorldData world);

		void scanBlock(WorldData world, int id, int x, int y, int z);
		
	}
	
	private final BlockHandler[] handlersCache = new BlockHandler[4096];
	
	private final ArrayList<BlockHandler> handlers = new ArrayList<BlockHandler>();
	
	private boolean scaningFinished;

	public BlockRangeScanner(BlockPos center) {
		this.center = center;
	}
	
	public void addHandler(BlockHandler h) {
		handlers.add(h);
		for (int i = 0; i < BlockSet.MAX_BLOCKIDS; i++) {
			if (h.getIds().containsAny(i)) {
				handlersCache[i] = h;
			}
		}
	}

	public void scanArea(WorldData world) {
		BlockCuboid area = new BlockCuboid(center.add(-HORIZONTAL_SCAN,  -VERTICAL_SCAN, -HORIZONTAL_SCAN), center.add(HORIZONTAL_SCAN,  VERTICAL_SCAN, HORIZONTAL_SCAN));
		area.accept(new AreaVisitor() {
			@Override
			public void visit(WorldData world, int x, int y, int z) {
				int id = world.getBlockId(x, y, z);					
				BlockHandler handler = handlersCache[id];
				if (handler != null) {
					handler.scanBlock(world, id, x, y, z);
				}
			}
		}, world);
		for (BlockHandler handler : handlers) {
			handler.scanningDone(world);
		}
		
		scaningFinished = true;
	}


	public void startAsync(final WorldData world) {
		new Thread("Block range finder") {
			@Override
			public void run() {
				try {
					scanArea(world);
				} catch (Throwable t) {
					t.printStackTrace();
					scaningFinished = true;
				}
			};
		}.start();
	}

	public boolean isScaningFinished() {
		return scaningFinished;
	}
}
