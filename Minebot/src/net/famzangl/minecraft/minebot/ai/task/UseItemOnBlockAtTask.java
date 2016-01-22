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
import net.famzangl.minecraft.minebot.ai.ItemFilter;
import net.minecraft.util.BlockPos;

/**
 * Use the given item at the block at the given position.
 * 
 * @author michael
 *
 */
public class UseItemOnBlockAtTask extends UseItemTask {

	private final BlockPos pos;

	public UseItemOnBlockAtTask(ItemFilter filter, BlockPos pos) {
		super(filter);
		this.pos = pos;
	}

	public UseItemOnBlockAtTask(BlockPos pos) {
		super();
		this.pos = pos;
	}

	@Override
	public String toString() {
		return "UseItemOnBlockAtTask [pos=" + pos + "]";
	}

	@Override
	protected boolean isBlockAllowed(AIHelper h, BlockPos pos) {
		return this.pos.equals(pos);
	}

	@Override
	protected void notFacingBlock(AIHelper h) {
		h.faceBlock(pos);
	}
	
	public BlockPos getPos() {
		return pos;
	}
}
