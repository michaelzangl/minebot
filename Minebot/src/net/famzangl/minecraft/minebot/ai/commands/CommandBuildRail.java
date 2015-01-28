package net.famzangl.minecraft.minebot.ai.commands;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.command.AICommandInvocation;
import net.famzangl.minecraft.minebot.ai.command.AICommandParameter;
import net.famzangl.minecraft.minebot.ai.command.ParameterType;
import net.famzangl.minecraft.minebot.ai.command.SafeStrategyRule;
import net.famzangl.minecraft.minebot.ai.path.LayRailPathFinder;
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.PathFinderStrategy;
import net.minecraft.util.EnumFacing;

public class CommandBuildRail {
	@AICommandInvocation(safeRule = SafeStrategyRule.DEFEND_MINING)
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "rails", description = "") String nameArg) {
		final EnumFacing horizontalLook = helper.getLookDirection();
		return run(helper, nameArg, horizontalLook);
	}
	
	@AICommandInvocation(safeRule = SafeStrategyRule.DEFEND_MINING)
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "rails", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.ENUM, description = "direction") EnumFacing inDirection) {

		final Pos p = helper.getPlayerPosition();
		return new PathFinderStrategy(
				new LayRailPathFinder(inDirection.getFrontOffsetX(),
						inDirection.getFrontOffsetZ(), p.getX(), p.getY(), p.getZ()),
				"Building a railway");
	}
}
