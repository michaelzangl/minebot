package net.famzangl.minecraft.minebot.ai.net;

import java.util.List;

import net.famzangl.minecraft.minebot.ai.net.MinebotNetHandler.PersistentChat;
import net.minecraft.entity.Entity;

public interface NetworkHelper {

	void resetFishState();

	boolean fishIsCaptured(Entity expectedPos);

	void addChunkChangeListener(ChunkListener l);

	void removeChunkChangeListener(ChunkListener l);

	/**
	 * Gets a list of chat messages received since game start.
	 * 
	 * @return The list of chat messages.
	 */
	public List<PersistentChat> getChatMessages();
}
