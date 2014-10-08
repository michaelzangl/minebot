package net.famzangl.minecraft.minebot.ai.scanner;

import java.util.ArrayList;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;

public class BlockRangeScanner {
	private static final int HORIZONTAL_SCAN = 100;
	private static final int VERTICAL_SCAN = 20;
	private final Pos center;
	
	public interface BlockHandler {
		int[] getIds();

		void scanningDone(AIHelper helper);

		void scanBlock(AIHelper helper, int id, int x, int y, int z);
		
	}
	
	private final BlockHandler[] handlersCache = new BlockHandler[4096];
	
	private final ArrayList<BlockHandler> handlers = new ArrayList<BlockHandler>();
	
	private boolean scaningFinished;

	public BlockRangeScanner(Pos center) {
		this.center = center;
	}
	
	public void addHandler(BlockHandler h) {
		handlers.add(h);
		for (int i : h.getIds()) {
			handlersCache[i] = h;
		}
	}

	public void scanArea(AIHelper helper) {
		for (int y = center.y - VERTICAL_SCAN; y <= center.y + VERTICAL_SCAN; y++) {
			for (int z = center.z - HORIZONTAL_SCAN; z <= center.z
					+ HORIZONTAL_SCAN; z++) {
				for (int x = center.x - HORIZONTAL_SCAN; x <= center.x
						+ HORIZONTAL_SCAN; x++) {
					int id = helper.getBlockId(x, y, z);					
					BlockHandler handler = handlersCache[id];
					if (handler != null) {
						handler.scanBlock(helper, id, x, y, z);
					}
				}
			}
		}
		for (BlockHandler handler : handlers) {
			handler.scanningDone(helper);
		}
		
		scaningFinished = true;
	}


	public void startAsync(final AIHelper helper) {
		new Thread("Block range finder") {
			@Override
			public void run() {
				try {
				scanArea(helper);
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
