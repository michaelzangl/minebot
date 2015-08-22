package net.famzangl.minecraft.minebot.ai.net;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFishHook;

public interface NetworkHelper {

	void resetFishState();

	boolean fishIsCaptured(Entity expectedPos);

	void addChunkChangeListener(ChunkListener l);
	void removeChunkChangeListener(ChunkListener l);

}
