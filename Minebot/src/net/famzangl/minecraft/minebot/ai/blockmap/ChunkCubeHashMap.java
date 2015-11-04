package net.famzangl.minecraft.minebot.ai.blockmap;

import java.util.HashMap;

/**
 * This is a 16x16x16 cube -> object map.n
 * 
 * @author michael
 *
 */
public class ChunkCubeHashMap<T> {

	private HashMap<Long, T> map;
	private ChunkCubeProvider<T> p;

	public ChunkCubeHashMap() {
		this(null);
	}

	public ChunkCubeHashMap(ChunkCubeProvider<T> p) {
		this.p = p;
	}

	public T get(int blockX, int blockY, int blockZ) {
		long cube = getChunkCubeId(blockX, blockY, blockZ);
		T t = map.get(cube);
		if (t == null && p != null) {
			t = p.getForChunk(blockX & ~0xf, blockY & ~0xf, blockZ & ~0xf);
			map.put(cube, t);
		}
		return t;
	}

	public static long getChunkCubeId(int blockX, int blockY, int blockZ) {
		long l = (blockX >> 4) & 0xfffffffl;
		l <<= 28;
		l |= (blockZ >> 4) & 0xfffffffl;
		l <<= 4;
		l |= (blockY >> 4) & 0xf;
		return l;
	}
}
