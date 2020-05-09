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
import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSets;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;

public class BlockBuildTask extends AbstractBuildTask {

	public static final BlockSet BLOCKS = BlockSet.builder().add(
			Blocks.DIRT,
			Blocks.STONE,
			Blocks.COBBLESTONE,
			Blocks.BEACON,
			Blocks.BOOKSHELF,
			Blocks.BRICKS,
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
			Blocks.GRASS_BLOCK,
			Blocks.GRAVEL, 
			Blocks.HAY_BLOCK, 
			Blocks.IRON_BLOCK,
			Blocks.IRON_ORE,
			Blocks.LAPIS_BLOCK, 
			Blocks.LAPIS_ORE,
			Blocks.MELON,
			Blocks.MOSSY_COBBLESTONE, 
			Blocks.NETHER_BRICKS,
			Blocks.NETHERRACK,
			Blocks.OBSIDIAN, 
			Blocks.PUMPKIN,
			Blocks.QUARTZ_BLOCK,
			Blocks.NETHER_QUARTZ_ORE,
			Blocks.CHISELED_QUARTZ_BLOCK,
			Blocks.RED_MUSHROOM_BLOCK,
			Blocks.REDSTONE_BLOCK,
			Blocks.REDSTONE_LAMP, 
			Blocks.REDSTONE_ORE,
			Blocks.SAND, 
			Blocks.STONE_BRICKS,
			Blocks.TNT)
			.add(BlockSets.PLANKS).add(BlockSets.WOOL)
			.add(BlockSets.STAINED_GLASS)
			.add(BlockSets.TERRACOTTA)
			.add(BlockSets.GLAZED_TERRACOTTA)
			.build();
	protected final BlockState blockToPlace;

	public BlockBuildTask(BlockPos forPosition,
						  BlockState blockToPlace) {
		super(forPosition);
		this.blockToPlace = blockToPlace;
	}

	@Override
	protected BlockItemFilter getItemToPlaceFilter() {
		return new BlockItemFilter(BlockSet.builder().add(blockToPlace).build());
	}
	

	@Override
	public BuildTask withPositionAndRotation(BlockPos add, int rotateSteps,
			MirrorDirection mirror) {
		return new BlockBuildTask(add, this.blockToPlace);
	}

	@Override
	public Object[] getCommandArguments() {
		// TODO: Add subtypes
		return new Object[] { blockToPlace.getBlock().getRegistryName() + ""};
	}
}
