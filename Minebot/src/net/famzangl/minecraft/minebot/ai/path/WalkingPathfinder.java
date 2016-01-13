/*******************************************************************************
 * This file is part of Minebot.
 *
 * Minebot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Minebot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Minebot.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package net.famzangl.minecraft.minebot.ai.path;

import java.util.LinkedList;

import net.famzangl.minecraft.minebot.ai.task.move.AlignToGridTask;
import net.famzangl.minecraft.minebot.build.WalkTowardsTask;
import net.famzangl.minecraft.minebot.settings.MinebotSettingsRoot;
import net.famzangl.minecraft.minebot.settings.PathfindingSetting;
import net.minecraft.util.BlockPos;

/**
 * This is a special type of pathfinder that attempts to walk to the target
 * instead of destroying everything on it's way
 * 
 * @author michael
 *
 */
public class WalkingPathfinder extends MovePathFinder {

	@Override
	protected PathfindingSetting loadSettings(MinebotSettingsRoot settingsRoot) {
		return settingsRoot.getPathfinding().getWalking();
	}
	
	private final int[] res = new int[14];
	
	@Override
	protected int[] getNeighbours(int currentNode) {
		final int cx = getX(currentNode);
		final int cz = getZ(currentNode);
		final int cy = getY(currentNode);
		final double height = getBlockHeight(cx, cy, cz);
		res[0] = getNeighbour(currentNode, cx, cy + 1, cz);
		res[1] = getNeighbour(currentNode, cx, cy - 1, cz);
		res[2] = getNeighbourIfHeightBelow(currentNode, cx + 1, cy + 1, cz, height);
		res[3] = getNeighbour(currentNode, cx + 1, cy, cz);
		res[4] = getNeighbour(currentNode, cx + 1, cy - 1, cz);
		res[5] = getNeighbourIfHeightBelow(currentNode, cx - 1, cy + 1, cz, height);
		res[6] = getNeighbour(currentNode, cx - 1, cy, cz);
		res[7] = getNeighbour(currentNode, cx - 1, cy - 1, cz);
		res[8] = getNeighbourIfHeightBelow(currentNode, cx, cy + 1, cz + 1, height);
		res[9] = getNeighbour(currentNode, cx, cy, cz + 1);
		res[10] = getNeighbour(currentNode, cx, cy - 1, cz + 1);
		res[11] = getNeighbourIfHeightBelow(currentNode, cx, cy + 1, cz - 1, height);
		res[12] = getNeighbour(currentNode, cx, cy, cz - 1);
		res[13] = getNeighbour(currentNode, cx, cy - 1, cz - 1);
		return res;
	}

	private int getNeighbourIfHeightBelow(int currentNode, int x, int y,
			int z, double height) {
		return getBlockHeight(x, y, z) <= height ? getNeighbour(currentNode, x, y, z) : -1;
	}

	private double getBlockHeight(int cx, int cy, int cz) {
		return world.getBlockBounds(cx, cy, cz).getMaxY();
	}
}
