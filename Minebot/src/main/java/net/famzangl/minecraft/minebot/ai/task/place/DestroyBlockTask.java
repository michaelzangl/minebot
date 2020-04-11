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
package net.famzangl.minecraft.minebot.ai.task.place;

import net.famzangl.minecraft.minebot.ai.task.DestroyInRangeTask;
import net.minecraft.util.math.BlockPos;

/**
 * Simply destroy a block at a given position, assuming it is in reach.
 * 
 * @author michael
 *
 */
public class DestroyBlockTask extends DestroyInRangeTask {

	public DestroyBlockTask(BlockPos pos) {
		super(pos, pos);
	}
}
