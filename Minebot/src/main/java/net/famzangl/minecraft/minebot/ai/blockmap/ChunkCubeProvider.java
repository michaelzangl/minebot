package net.famzangl.minecraft.minebot.ai.blockmap;

public interface ChunkCubeProvider<T> {
	public T getForChunk(int chunkStartX, int chunkStartY, int chunkStartZ);
}
