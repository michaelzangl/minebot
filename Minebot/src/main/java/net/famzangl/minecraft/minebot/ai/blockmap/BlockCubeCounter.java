package net.famzangl.minecraft.minebot.ai.blockmap;

import net.minecraft.util.math.BlockPos;

/**
 * This is a map that assigns an integer to every {@link BlockPos}. It has
 * optimizations for empty maps and only covers a 16x16x16 area.
 * 
 * @author Michael Zangl
 *
 */
public class BlockCubeCounter {

	private int[] array = null;

	public int get(int blockX, int blockY, int blockZ) {
		if (array == null) {
			return 0;
		}
		int index = LocalChunkBitset.index(blockX, blockY, blockZ);
		return array[index];
	}

	public void increment(int blockX, int blockY, int blockZ, int delta) {
		if (array == null) {
			array = new int[16 * 16 * 16];
		}
		int index = LocalChunkBitset.index(blockX, blockY, blockZ);
		array[index] += delta;
	}
}
