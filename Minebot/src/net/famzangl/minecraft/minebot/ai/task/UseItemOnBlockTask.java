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

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;


/**
 * 
 * @author michael
 *
 */
public abstract class UseItemOnBlockTask extends UseItemTask {

	private final BlockSet allowedBlocks;

	public UseItemOnBlockTask(BlockSet allowedBlocks) {
		this.allowedBlocks = allowedBlocks;
	}

	@Override
	protected boolean isBlockAllowed(AIHelper h, BlockPos pos) {
		final Block block = h.getBlock(pos);
		return allowedBlocks.contains(block);
	}

}
