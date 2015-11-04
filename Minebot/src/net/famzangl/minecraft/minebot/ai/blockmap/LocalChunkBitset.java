package net.famzangl.minecraft.minebot.ai.blockmap;

import java.util.BitSet;

/**
 * This is a 16 x 16 x 16 bitset.
 * @author michael
 *
 */
public class LocalChunkBitset {
	private BitSet bitset = new BitSet();
	
	public static int index(int blockX, int blockY, int blockZ) {
		return (blockX & 0xf) << 4 | (blockY & 0xf) << 8 | (blockZ & 0xf);
	}
	
	public boolean get(int blockX, int blockY, int blockZ) {
		return bitset.get(index(blockX, blockY, blockZ));
	}
	
	public void set(int blockX, int blockY, int blockZ, boolean val) {
		bitset.set(index(blockX, blockY, blockZ), val);
	}
}
