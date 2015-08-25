package net.famzangl.minecraft.minebot.ai.net;

import net.minecraft.entity.Entity;

public interface NetworkHelper {

	void resetFishState();

	boolean fishIsCaptured(Entity expectedPos);

	void addChunkChangeListener(ChunkListener l);
	void removeChunkChangeListener(ChunkListener l);

}
