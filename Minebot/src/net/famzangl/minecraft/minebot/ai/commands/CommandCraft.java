package net.famzangl.minecraft.minebot.ai.commands;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.BlockWhitelist;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.ai.command.AICommandInvocation;
import net.famzangl.minecraft.minebot.ai.command.AICommandParameter;
import net.famzangl.minecraft.minebot.ai.command.AICommandParameter.BlockFilter;
import net.famzangl.minecraft.minebot.ai.command.ParameterType;
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.CraftStrategy;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

@AICommand(helpText = "Crafts items of the given type.", name = "minebot")
public class CommandCraft {

	private static final BlockWhitelist simpleBlocks = new BlockWhitelist(
			Blocks.air, Blocks.brewing_stand, Blocks.bed, Blocks.nether_wart,
			Blocks.cauldron, Blocks.flower_pot, Blocks.wheat, Blocks.reeds,
			Blocks.cake, Blocks.skull, Blocks.piston_head,
			Blocks.piston_extension, Blocks.lit_redstone_ore,
			Blocks.powered_repeater, Blocks.pumpkin_stem, Blocks.standing_sign,
			Blocks.powered_comparator, Blocks.tripwire,
			Blocks.lit_redstone_lamp, Blocks.melon_stem,
			Blocks.unlit_redstone_torch, Blocks.unpowered_comparator,
			Blocks.redstone_wire, Blocks.wall_sign, Blocks.unpowered_repeater,
			Blocks.iron_door, Blocks.wool).unionWith(AIHelper.woodenDoors).invert();

	public static final class MyBlockFilter extends BlockFilter {
		@Override
		public boolean matches(Block b) {
			return simpleBlocks.contains(b);
		}
	}

	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "craft", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.NUMBER, description = "Item count") int itemCount,
			@AICommandParameter(type = ParameterType.BLOCK_NAME, description = "Block", blockFilter = MyBlockFilter.class) Block itemType,
			@AICommandParameter(type = ParameterType.NUMBER, description = "Item subtype", optional = true) Integer itemSubtype) {
		return run(helper, nameArg, itemCount, Block.getIdFromBlock(itemType),
				itemSubtype);
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
