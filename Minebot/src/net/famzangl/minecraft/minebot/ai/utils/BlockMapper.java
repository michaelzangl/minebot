package net.famzangl.minecraft.minebot.ai.utils;

import net.famzangl.minecraft.minebot.ai.path.world.WorldData;

/**
 * This is a function that maps a given value to each block.
 * 
 * @author Michael Zangl
 *
 */
public interface BlockMapper<ValT> {
	/**
	 * Gets the value for a given block.
	 * 
	 * @param world
	 *            The world we work on.
	 * @param x
	 *            The x coordinate
	 * @param y
	 *            The y coordinate
	 * @param z
	 *            The z coordinate
	 * @return The value for the position.
	 */
	ValT getValueFor(WorldData world, int x, int y, int z);
}
