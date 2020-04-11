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
package net.famzangl.minecraft.minebot.ai.commands;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.ai.command.AICommandInvocation;
import net.famzangl.minecraft.minebot.ai.command.AICommandParameter;
import net.famzangl.minecraft.minebot.ai.command.AICommandParameter.BlockFilter;
import net.famzangl.minecraft.minebot.ai.command.ParameterType;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSets;
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.CraftStrategy;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

@AICommand(helpText = "Crafts items of the given type.", name = "minebot")
public class CommandCraft {

	/**
	 * Blocks that can not be crafted.
	 */
	private static final BlockSet simpleBlocks = BlockSet.builder().add(
			Blocks.AIR, Blocks.BREWING_STAND, Blocks.NETHER_WART,
			Blocks.CAULDRON, Blocks.FLOWER_POT, Blocks.WHEAT, Blocks.SUGAR_CANE,
			Blocks.CAKE, Blocks.SKELETON_SKULL, Blocks.SKELETON_WALL_SKULL, Blocks.WITHER_SKELETON_SKULL,
			Blocks.WITHER_SKELETON_WALL_SKULL, Blocks.PISTON_HEAD,
			Blocks.MOVING_PISTON, Blocks.REDSTONE_WIRE,
			Blocks.PUMPKIN_STEM,
			Blocks.TRIPWIRE,
			Blocks.MELON_STEM,
			Blocks.REDSTONE_WIRE,
			Blocks.IRON_DOOR)
			.add(BlockSets.WALL_SIGN)
			.add(BlockSets.WOOL)
			.add(BlockSets.BED)
			.add(BlockSets.WOODEN_DOR).build().invert();

	public static final class MyBlockFilter extends BlockFilter {
		@Override
		public boolean matches(BlockState b) {
			return simpleBlocks.contains(b);
		}
	}

	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "craft", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.NUMBER, description = "Item count") int itemCount,
			@AICommandParameter(type = ParameterType.BLOCK_STATE, description = "Block", blockFilter = MyBlockFilter.class) BlockState itemType) {
		return new CraftStrategy(itemCount, itemType);
	}

	/* TODO
	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "craft", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.NUMBER, description = "Item count") int itemCount,
			@AICommandParameter(type = ParameterType.NUMBER, description = "Item type") int itemType,
			@AICommandParameter(type = ParameterType.NUMBER, description = "Item subtype", optional = true) Integer itemSubtype) {
		return new CraftStrategy(itemCount, itemType, itemSubtype == null ? 0 : itemSubtype);
	} */
}
