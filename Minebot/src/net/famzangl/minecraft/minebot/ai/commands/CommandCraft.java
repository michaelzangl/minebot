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
			Blocks.air, Blocks.brewing_stand, Blocks.bed, Blocks.nether_wart,
			Blocks.cauldron, Blocks.flower_pot, Blocks.wheat, Blocks.reeds,
			Blocks.cake, Blocks.skull, Blocks.piston_head,
			Blocks.piston_extension, Blocks.lit_redstone_ore,
			Blocks.powered_repeater, Blocks.pumpkin_stem, Blocks.standing_sign,
			Blocks.powered_comparator, Blocks.tripwire,
			Blocks.lit_redstone_lamp, Blocks.melon_stem,
			Blocks.unlit_redstone_torch, Blocks.unpowered_comparator,
			Blocks.redstone_wire, Blocks.wall_sign, Blocks.unpowered_repeater,
			Blocks.iron_door, Blocks.wool).unionWith(BlockSets.WOODEN_DOR).invert();

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
