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
package net.famzangl.minecraft.minebot.build.blockbuild;

import net.famzangl.minecraft.minebot.ai.BlockItemFilter;
import net.famzangl.minecraft.minebot.ai.ItemFilter;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

public class ItemPlaceOnGroundTask extends BuildFlatOnGroundTask {

	public static final BlockSet BLOCKS = new BlockSet(Blocks.dirt,
			Blocks.stone, Blocks.cobblestone, Blocks.beacon, Blocks.bookshelf,
			Blocks.brick_block, Blocks.cake, Blocks.coal_block,
			Blocks.coal_ore, Blocks.crafting_table, Blocks.diamond_block,
			Blocks.diamond_ore, Blocks.emerald_block, Blocks.emerald_ore,
			Blocks.end_stone, Blocks.glass, Blocks.gold_block, Blocks.gold_ore,
			Blocks.grass, Blocks.gravel, Blocks.hay_block, Blocks.iron_block,
			Blocks.iron_ore, Blocks.lapis_block, Blocks.lapis_ore,
			Blocks.melon_block, Blocks.mossy_cobblestone, Blocks.nether_brick,
			Blocks.netherrack, Blocks.obsidian, Blocks.pumpkin,
			Blocks.quartz_block, Blocks.quartz_ore, Blocks.red_mushroom_block,
			Blocks.redstone_block, Blocks.redstone_lamp, Blocks.redstone_ore,
			Blocks.sand, Blocks.stonebrick, Blocks.tnt);
	private final Block block;

	protected ItemPlaceOnGroundTask(BlockPos forPosition, Block block) {
		super(forPosition);
		this.block = block;
	}

	@Override
	public ItemFilter getRequiredItem() {
		return new BlockItemFilter(block);
	}

	@Override
	public BuildTask withPositionAndRotation(BlockPos add, int rotateSteps,
			MirrorDirection mirror) {
		return new ItemPlaceOnGroundTask(add, block);
	}

	@Override
	public String toString() {
		return "ItemPlaceOnGroundTask [block=" + block + "]";
	}
}
