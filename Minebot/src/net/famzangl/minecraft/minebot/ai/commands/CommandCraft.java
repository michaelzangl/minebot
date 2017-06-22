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
import net.famzangl.minecraft.minebot.ai.command.BlockWithDataOrDontcare;
import net.famzangl.minecraft.minebot.ai.command.ParameterType;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSets;
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.CraftStrategy;
import net.minecraft.init.Blocks;

@AICommand(helpText = "Crafts items of the given type.", name = "minebot")
public class CommandCraft {

	/**
	 * Blocks that can not be crafted.
	 */
	private static final BlockSet simpleBlocks = new BlockSet(
			Blocks.AIR, Blocks.BREWING_STAND, Blocks.BED, Blocks.NETHER_WART,
			Blocks.CAULDRON, Blocks.FLOWER_POT, Blocks.WHEAT, Blocks.REEDS,
			Blocks.CAKE, Blocks.SKULL, Blocks.PISTON_HEAD,
			Blocks.PISTON_EXTENSION, Blocks.LIT_REDSTONE_ORE,
			Blocks.POWERED_REPEATER, Blocks.PUMPKIN_STEM, Blocks.STANDING_SIGN,
			Blocks.POWERED_COMPARATOR, Blocks.TRIPWIRE,
			Blocks.LIT_REDSTONE_LAMP, Blocks.MELON_STEM,
			Blocks.UNLIT_REDSTONE_TORCH, Blocks.UNPOWERED_COMPARATOR,
			Blocks.REDSTONE_WIRE, Blocks.WALL_SIGN, Blocks.UNPOWERED_REPEATER,
			Blocks.IRON_DOOR, Blocks.WOOL).unionWith(BlockSets.WOODEN_DOR).invert();

	public static final class MyBlockFilter extends BlockFilter {
		@Override
		public boolean matches(BlockWithDataOrDontcare b) {
			return simpleBlocks.contains(b);
		}
	}

	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "craft", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.NUMBER, description = "Item count") int itemCount,
			@AICommandParameter(type = ParameterType.BLOCK_NAME, description = "Block", blockFilter = MyBlockFilter.class) BlockWithDataOrDontcare itemType) {
		return new CraftStrategy(itemCount, itemType);
	}

	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "craft", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.NUMBER, description = "Item count") int itemCount,
			@AICommandParameter(type = ParameterType.NUMBER, description = "Item type") int itemType,
			@AICommandParameter(type = ParameterType.NUMBER, description = "Item subtype", optional = true) Integer itemSubtype) {
		return new CraftStrategy(itemCount, itemType, itemSubtype == null ? 0 : itemSubtype);
	}
}
