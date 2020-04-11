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
package net.famzangl.minecraft.minebot.ai.task;

import net.famzangl.minecraft.minebot.ai.strategy.TaskStrategy;
import net.famzangl.minecraft.minebot.ai.task.error.TaskError;

/**
 * Callbacks for {@link AITask}s to communicate back to the {@link TaskStrategy}
 * @author michael
 *
 */
public interface TaskOperations {

	/**
	 * This should be called whenever the current task could not achieve it's
	 * goal. All following tasks are unscheduled.
	 * 
	 * @param taskError
	 */
	public abstract void desync(TaskError taskError);

	/**
	 * This can be called by the current task to do a look ahead and already let
	 * the next task to it's face and destroy. Use with care.
	 * 
	 * @return
	 */
	public boolean faceAndDestroyForNextTask();
}
