package net.famzangl.minecraft.minebot.build.commands;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.command.AIChatController;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.ai.command.AICommandInvocation;
import net.famzangl.minecraft.minebot.ai.command.AICommandParameter;
import net.famzangl.minecraft.minebot.ai.command.ParameterType;
import net.famzangl.minecraft.minebot.ai.path.world.Pos;
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.RunOnceStrategy;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3i;

@AICommand(helpText = "Expand the selected region.", name = "minebuild")
public class CommandExpand {
	private static final class ExpandStrategy extends RunOnceStrategy {
		private final EnumFacing direction;
		private final int amount;

		public ExpandStrategy(int amount, EnumFacing direction) {
			this.amount = amount;
			this.direction = direction;
		}

		@Override
		protected void singleRun(AIHelper helper) {
			BlockPos pos1 = helper.getPos1();
			BlockPos pos2 = helper.getPos2();
			if (pos1 == null && pos2 == null) {
				AIChatController.addChatLine("Please set positions first.");
				return;
			}
			if (pos1 == null) {
				pos1 = pos2;
			} else if (pos2 == null) {
				pos2 = pos1;
			}

			Vec3i dir = direction.getDirectionVec();

			boolean usePos2;
			if (dir.getX() != 0) {
				usePos2 = (pos1.getX() < pos2.getX() == dir.getX() > 0);
			} else if (dir.getY() != 0) {
				usePos2 = (pos1.getY() < pos2.getY() == dir.getY() > 0);
			} else {
				usePos2 = (pos1.getZ() < pos2.getZ() == dir.getZ() > 0);
			}
			BlockPos old;
			if (usePos2) {
				old = pos2;
			} else {
				old = pos1;
			}
			BlockPos newPos = old.offset(direction, amount);
			helper.setPosition(newPos, usePos2);
			AIChatController.addChatLine("Set position " + (usePos2 ? 2 : 1)
					+ " from " + Pos.niceString(old) + " to " + Pos.niceString(newPos));
		}
	}

	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "expand", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.NUMBER, description = "How much", optional = true) Integer amount,
			@AICommandParameter(type = ParameterType.ENUM, description = "Direction", optional = true) EnumFacing direction) {
		if (direction == null) {
			System.out.println("Pitch: "
					+ helper.getMinecraft().thePlayer.rotationPitch);
			direction = helper.getLookDirection();
		}

		return new ExpandStrategy(amount == null ? 1 : amount, direction);
	}

}
