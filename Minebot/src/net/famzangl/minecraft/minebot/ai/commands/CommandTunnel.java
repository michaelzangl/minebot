package net.famzangl.minecraft.minebot.ai.commands;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.ai.command.AICommandInvocation;
import net.famzangl.minecraft.minebot.ai.command.AICommandParameter;
import net.famzangl.minecraft.minebot.ai.command.ParameterType;
import net.famzangl.minecraft.minebot.ai.command.SafeStrategyRule;
import net.famzangl.minecraft.minebot.ai.path.TunnelPathFinder;
import net.famzangl.minecraft.minebot.ai.path.TunnelPathFinder.TorchSide;
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.PathFinderStrategy;
import net.minecraft.util.EnumFacing;

@AICommand(helpText = "Build a tunnel with the given profile", name = "minebot")
public class CommandTunnel {
	@AICommandInvocation(safeRule = SafeStrategyRule.DEFEND_MINING)
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "tunnel", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.ENUM, description = "direction", optional = true) EnumFacing inDirection,
			@AICommandParameter(type = ParameterType.ENUM, description = "torch side", optional = true) TorchSide torches,
			@AICommandParameter(type = ParameterType.NUMBER, description = "max length", optional = true) int length) {
		return run(helper, nameArg, inDirection, 0, 0, torches, length);
	}

	@AICommandInvocation(safeRule = SafeStrategyRule.DEFEND_MINING)
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "tunnel", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.ENUM, description = "direction", optional = true) EnumFacing inDirection,
			@AICommandParameter(type = ParameterType.NUMBER, description = "add to side") int addToSide,
			@AICommandParameter(type = ParameterType.NUMBER, description = "add to top") int addToTop,
			@AICommandParameter(type = ParameterType.ENUM, description = "torch side", optional = true) TorchSide torches,
			@AICommandParameter(type = ParameterType.NUMBER, description = "max length", optional = true) Integer length) {
		if (inDirection == null) {
			inDirection = helper.getLookDirection();
		}

		if (torches == null) {
			torches = TorchSide.NONE;
		}

		if (length == null) {
			length = -1;
		}

		final Pos pos = helper.getPlayerPosition();
		return new PathFinderStrategy(new TunnelPathFinder(inDirection.getFrontOffsetX(),
				inDirection.getFrontOffsetZ(), pos.getX(), pos.getY(), pos.getZ(), addToSide, addToTop,
				torches, length), "Tunneling");
	}
}
