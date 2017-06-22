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

import java.util.List;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.minecraft.util.math.BlockPos;

/**
 * An interface that is implemented by all {@link AITask}s that do destroy
 * blocks. That way, the bot can already start to destroy them while walking
 * there.
 * 
 * @author michael
 *
 */
public interface CanPrefaceAndDestroy {

	/**
	 * Return a list that this task faces and destroys that could already be
	 * mined before arriving at the target location.
	 * 
	 * @param helper
	 *            The AI helper.
	 * @return A list of block positions, preferably ordered the way the task
	 *         destroys them.
	 */
	List<BlockPos> getPredestroyPositions(AIHelper helper);

}
