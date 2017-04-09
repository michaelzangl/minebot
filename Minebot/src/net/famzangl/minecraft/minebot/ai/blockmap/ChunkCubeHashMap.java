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
	private ChunkCubeProvider<T> provider;

	public ChunkCubeHashMap() {
		this(null);
	}

	public ChunkCubeHashMap(ChunkCubeProvider<T> provider) {
		this.provider = provider;
	}

	public T get(int blockX, int blockY, int blockZ) {
		long cube = getChunkCubeId(blockX, blockY, blockZ);
		T t = map.get(cube);
		if (t == null && provider != null) {
			t = provider.getForChunk(blockX & ~0xf, blockY & ~0xf, blockZ & ~0xf);
			map.put(cube, t);
		}
		return t;
	}

	public static long getChunkCubeId(int blockX, int blockY, int blockZ) {
		long id = (blockX >> 4) & 0xfffffffl;
		id <<= 28;
		id |= (blockZ >> 4) & 0xfffffffl;
		id <<= 4;
		id |= (blockY >> 4) & 0xf;
		return id;
	}
}
