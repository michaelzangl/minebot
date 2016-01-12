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
package net.famzangl.minecraft.minebot.build.reverse.factories;

import net.famzangl.minecraft.minebot.ai.command.BlockWithDataOrDontcare;
import net.famzangl.minecraft.minebot.ai.path.world.WorldData;
import net.famzangl.minecraft.minebot.build.blockbuild.BuildTask;
import net.famzangl.minecraft.minebot.build.reverse.TaskDescription;
import net.famzangl.minecraft.minebot.build.reverse.UnsupportedBlockException;
import net.minecraft.util.BlockPos;


/**
 * This is a factory for build task descriptions. Those build tasks are then turned into a text file.
 * @author Michael Zangl
 *
 */
public interface BuildTaskFactory {
	public BuildTask getTask(BlockPos position, BlockWithDataOrDontcare forBlock);
	
	/**
	 * Attempts to create a task description (textual representation of the task) for the given position.
	 * @param world The world state.
	 * @param position The position the build task is at.
	 * @return The task description.
	 * @throws UnsupportedBlockException If the block should have been handled but some error occurred (unknown state, ...)
	 */
	public TaskDescription getTaskDescription(WorldData world, BlockPos position) throws UnsupportedBlockException;
}
