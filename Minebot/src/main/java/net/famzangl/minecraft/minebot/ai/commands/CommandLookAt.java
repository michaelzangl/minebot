package net.famzangl.minecraft.minebot.ai.commands;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.ai.command.AICommandInvocation;
import net.famzangl.minecraft.minebot.ai.command.AICommandParameter;
import net.famzangl.minecraft.minebot.ai.command.ParameterType;
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.LookAtStrategy;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;

@AICommand(name = "minebot", helpText = "Look at a given position")
public class CommandLookAt {
	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "look", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.DOUBLE, description = "x") Double x,
			@AICommandParameter(type = ParameterType.DOUBLE, description = "y") Double y,
			@AICommandParameter(type = ParameterType.DOUBLE, description = "z") Double z) {
		return run(helper, new Vec3d(x, y, z));
	}

	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "look", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.ENUM, description = "direction") EnumFacing direction) {
		Vec3d offset = new Vec3d(direction.getFrontOffsetX(),
				direction.getFrontOffsetY()
						+ helper.getMinecraft().player.getEyeHeight(),
				direction.getFrontOffsetZ());
		return run(helper,
				helper.getWorld().getExactPlayerPosition().add(offset));
	}

	private static LookAtStrategy run(AIHelper helper, Vec3d vec3) {
		return new LookAtStrategy(vec3);
	}
}
