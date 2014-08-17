package net.famzangl.minecraft.minebot.build.commands;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.ai.command.AICommandInvocation;
import net.famzangl.minecraft.minebot.ai.command.AICommandParameter;
import net.famzangl.minecraft.minebot.ai.command.CommandEvaluationException;
import net.famzangl.minecraft.minebot.ai.command.ParameterType;
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy;
import net.famzangl.minecraft.minebot.ai.task.BlockSide;
import net.famzangl.minecraft.minebot.build.WoodType;
import net.famzangl.minecraft.minebot.build.blockbuild.BlockBuildTask;
import net.famzangl.minecraft.minebot.build.blockbuild.BuildHalfslabTask;
import net.famzangl.minecraft.minebot.build.blockbuild.BuildNormalStairsTask;
import net.famzangl.minecraft.minebot.build.blockbuild.BuildNormalStairsTask.Half;
import net.famzangl.minecraft.minebot.build.blockbuild.BuildTask;
import net.famzangl.minecraft.minebot.build.blockbuild.ColoredCubeBuildTask;
import net.famzangl.minecraft.minebot.build.blockbuild.FenceBuildTask;
import net.famzangl.minecraft.minebot.build.blockbuild.LogBuildTask;
import net.famzangl.minecraft.minebot.build.blockbuild.SlabType;
import net.famzangl.minecraft.minebot.build.blockbuild.WoodBuildTask;
import net.minecraft.block.Block;
import net.minecraftforge.common.util.ForgeDirection;

@AICommand(helpText = "Schedules a build task.", name = "minebuild")
public class CommandScheduleBuild {

	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "schedule", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.POSITION, description = "Where to place it (relative is to your current pos)") Pos forPosition,
			@AICommandParameter(type = ParameterType.BLOCK_NAME, description = "The block") Block blockToPlace) {

		if (AIHelper.blockIsOneOf(blockToPlace, BlockBuildTask.BLOCKS)) {
			addTask(helper, new BlockBuildTask(forPosition, blockToPlace));
		} else if (AIHelper.blockIsOneOf(blockToPlace, FenceBuildTask.BLOCKS)) {
			addTask(helper, new FenceBuildTask(forPosition, blockToPlace));
		} else {
			throw new CommandEvaluationException("Cannot build " + blockToPlace);
		}
		return null;
	}

	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "schedule", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.POSITION, description = "Where to place it (relative is to your current pos)") Pos forPosition,
			@AICommandParameter(type = ParameterType.BLOCK_NAME, description = "The block") Block blockToPlace,
			@AICommandParameter(type = ParameterType.COLOR, description = "The color") int color) {
		if (AIHelper.blockIsOneOf(blockToPlace, ColoredCubeBuildTask.BLOCKS)) {
			addTask(helper, new ColoredCubeBuildTask(forPosition, blockToPlace,
					color));
		} else {
			throw new CommandEvaluationException("Cannot build " + blockToPlace);
		}
		return null;
	}

	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "schedule", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.POSITION, description = "Where to place it (relative is to your current pos)") Pos forPosition,
			@AICommandParameter(type = ParameterType.BLOCK_NAME, description = "The block type to place") Block blockToPlace,
			@AICommandParameter(type = ParameterType.ENUM, description = "The wood subtype to place") WoodType woodType) {
		if (AIHelper.blockIsOneOf(blockToPlace, WoodBuildTask.BLOCK)) {
			addTask(helper, new WoodBuildTask(forPosition, woodType));
		} else {
			throw new CommandEvaluationException("Cannot build " + blockToPlace);
		}
		return null;
	}

	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "schedule", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.POSITION, description = "Where to place it (relative is to your current pos)") Pos forPosition,
			@AICommandParameter(type = ParameterType.BLOCK_NAME, description = "The block") Block blockToPlace,
			@AICommandParameter(type = ParameterType.ENUM, description = "The type of wood logs") WoodType woodType,
			@AICommandParameter(type = ParameterType.ENUM, description = "The direction the log is facing") ForgeDirection direction) {
		if (AIHelper.blockIsOneOf(blockToPlace, LogBuildTask.BLOCKS)) {
			addTask(helper, new LogBuildTask(forPosition, woodType, direction));
		} else {
			throw new CommandEvaluationException("Cannot build " + blockToPlace);
		}
		return null;
	}

	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "schedule", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.POSITION, description = "Where to place it (relative is to your current pos)") Pos forPosition,
			@AICommandParameter(type = ParameterType.BLOCK_NAME, description = "The block") Block blockToPlace,
			@AICommandParameter(type = ParameterType.ENUM, description = "The direction the stairs face") ForgeDirection direction,
			@AICommandParameter(type = ParameterType.ENUM, description = "Upper for inverted stairs") Half half) {
		if (AIHelper.blockIsOneOf(blockToPlace, BuildNormalStairsTask.BLOCKS)) {
			addTask(helper, new BuildNormalStairsTask(forPosition,
					blockToPlace, direction, half));
		} else {
			throw new CommandEvaluationException("Cannot build " + blockToPlace);
		}
		return null;
	}

	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "schedule", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.POSITION, description = "Where to place it (relative is to your current pos)") Pos forPosition,
			@AICommandParameter(type = ParameterType.BLOCK_NAME, description = "The block") Block blockToPlace,
			@AICommandParameter(type = ParameterType.ENUM, description = "The subtype of slabs to place") SlabType type,
			@AICommandParameter(type = ParameterType.ENUM, description = "If a upper or lower half should be placed") BlockSide side) {
		if (AIHelper.blockIsOneOf(blockToPlace, BuildHalfslabTask.BLOCKS)) {
			addTask(helper, new BuildHalfslabTask(forPosition, type, side));
		} else {
			throw new CommandEvaluationException("Cannot build " + blockToPlace);
		}
		return null;
	}

	private static void addTask(AIHelper helper, BuildTask blockBuildTask) {
		helper.buildManager.addTask(blockBuildTask);
	}
}
