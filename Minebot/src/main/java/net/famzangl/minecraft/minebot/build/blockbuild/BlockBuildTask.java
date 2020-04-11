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
import net.famzangl.minecraft.minebot.ai.command.BlockWithDataOrDontcare;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;

public class BlockBuildTask extends AbstractBuildTask {

	public static final BlockSet BLOCKS = new BlockSet(
			Blocks.DIRT,
			Blocks.STONE,
			Blocks.COBBLESTONE,
			Blocks.BEACON,
			Blocks.BOOKSHELF,
			Blocks.BRICK_BLOCK,
			Blocks.CAKE,
			Blocks.COAL_BLOCK,
			Blocks.COAL_ORE,
			Blocks.CRAFTING_TABLE,
			Blocks.DIAMOND_BLOCK,
			Blocks.DIAMOND_ORE,
			Blocks.EMERALD_BLOCK,
			Blocks.EMERALD_ORE,
			Blocks.END_STONE,
			Blocks.GLASS,
			Blocks.GOLD_BLOCK,
			Blocks.GOLD_ORE,
			Blocks.GRASS, 
			Blocks.GRAVEL, 
			Blocks.HAY_BLOCK, 
			Blocks.IRON_BLOCK,
			Blocks.IRON_ORE,
			Blocks.LAPIS_BLOCK, 
			Blocks.LAPIS_ORE,
			Blocks.MELON_BLOCK, 
			Blocks.MOSSY_COBBLESTONE, 
			Blocks.NETHER_BRICK,
			Blocks.NETHERRACK,
			Blocks.OBSIDIAN, 
			Blocks.PUMPKIN,
			Blocks.QUARTZ_BLOCK,
			Blocks.QUARTZ_ORE, 
			Blocks.RED_MUSHROOM_BLOCK,
			Blocks.REDSTONE_BLOCK,
			Blocks.REDSTONE_LAMP, 
			Blocks.REDSTONE_ORE,
			Blocks.SAND, 
			Blocks.STONEBRICK,
			Blocks.TNT,
			Blocks.PLANKS,
			Blocks.WOOL,
			Blocks.STAINED_GLASS, 
			Blocks.STAINED_HARDENED_CLAY);
	protected final BlockWithDataOrDontcare blockToPlace;

	public BlockBuildTask(BlockPos forPosition,
			BlockWithDataOrDontcare blockToPlace) {
		super(forPosition);
		this.blockToPlace = blockToPlace;
	}

	@Override
	protected BlockItemFilter getItemToPlaceFilter() {
		return new BlockItemFilter(blockToPlace.toBlockSet());
	}
	

	@Override
	public BuildTask withPositionAndRotation(BlockPos add, int rotateSteps,
			MirrorDirection mirror) {
		return new BlockBuildTask(add, this.blockToPlace);
	}

	@Override
	public Object[] getCommandArguments() {
		return new Object[] { blockToPlace.toBlockString() };
	}
}
