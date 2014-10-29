package net.famzangl.minecraft.minebot.build.commands;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.BlockWhitelist;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.ai.command.AICommandInvocation;
import net.famzangl.minecraft.minebot.ai.command.AICommandParameter;
import net.famzangl.minecraft.minebot.ai.command.AICommandParameter.BlockFilter;
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
import net.famzangl.minecraft.minebot.build.blockbuild.StandingSignBuildTask;
import net.famzangl.minecraft.minebot.build.blockbuild.StandingSignBuildTask.SignDirection;
import net.famzangl.minecraft.minebot.build.blockbuild.WoodBuildTask;
import net.minecraft.block.Block;
import net.minecraftforge.common.util.ForgeDirection;

@AICommand(helpText = "Schedules a build task.", name = "minebuild")
public class CommandScheduleBuild {

	public static final BlockWhitelist SIMPLE_WHITELIST = BlockBuildTask.BLOCKS
			.unionWith(FenceBuildTask.BLOCKS);

	public static final class RunSimpleFilter extends BlockFilter {
		@Override
		public boolean matches(Block b) {
			return SIMPLE_WHITELIST.contains(b);
		}
	}

	@AICommandInvocation()
	public static AIStrategy runSimple(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "schedule", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.POSITION, description = "Where to place it (relative is to your current pos)") Pos forPosition,
			@AICommandParameter(type = ParameterType.BLOCK_NAME, description = "The block", blockFilter = RunSimpleFilter.class) Block blockToPlace) {

		if (BlockBuildTask.BLOCKS.contains(blockToPlace)) {
			addTask(helper, new BlockBuildTask(forPosition, blockToPlace));
		} else if (FenceBuildTask.BLOCKS.contains(blockToPlace)) {
			addTask(helper, new FenceBuildTask(forPosition, blockToPlace));
		} else {
			throw new CommandEvaluationException("Cannot build " + blockToPlace);
		}
		return null;
	}

	public static final class RunColoredFilter extends BlockFilter {
		@Override
		public boolean matches(Block b) {
			return ColoredCubeBuildTask.BLOCKS.contains(b);
		}
	}

	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "schedule", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.POSITION, description = "Where to place it (relative is to your current pos)") Pos forPosition,
			@AICommandParameter(type = ParameterType.BLOCK_NAME, description = "The block", blockFilter = RunColoredFilter.class) Block blockToPlace,
			@AICommandParameter(type = ParameterType.COLOR, description = "The color") int color) {
		if (ColoredCubeBuildTask.BLOCKS.contains(blockToPlace)) {
			addTask(helper, new ColoredCubeBuildTask(forPosition, blockToPlace,
					color));
		} else {
			throw new CommandEvaluationException("Cannot build " + blockToPlace);
		}
		return null;
	}

	public static final class WoodBlockFilter extends BlockFilter {
		@Override
		public boolean matches(Block b) {
			return WoodBuildTask.BLOCKS.contains(b);
		}
	}

	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "schedule", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.POSITION, description = "Where to place it (relative is to your current pos)") Pos forPosition,
			@AICommandParameter(type = ParameterType.BLOCK_NAME, description = "The block type to place", blockFilter = WoodBlockFilter.class) Block blockToPlace,
			@AICommandParameter(type = ParameterType.ENUM, description = "The wood subtype to place") WoodType woodType) {
		if (WoodBuildTask.BLOCKS.contains(blockToPlace)) {
			addTask(helper, new WoodBuildTask(forPosition, woodType));
		} else {
			throw new CommandEvaluationException("Cannot build " + blockToPlace);
		}
		return null;
	}

	public static final class LogBlockFilter extends BlockFilter {
		@Override
		public boolean matches(Block b) {
			return LogBuildTask.BLOCKS.contains(b);
		}
	}

	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "schedule", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.POSITION, description = "Where to place it (relative is to your current pos)") Pos forPosition,
			@AICommandParameter(type = ParameterType.BLOCK_NAME, description = "The block", blockFilter = LogBlockFilter.class) Block blockToPlace,
			@AICommandParameter(type = ParameterType.ENUM, description = "The type of wood logs") WoodType woodType,
			@AICommandParameter(type = ParameterType.ENUM, description = "The direction the log is facing") ForgeDirection direction) {
		if (LogBuildTask.BLOCKS.contains(blockToPlace)) {
			addTask(helper, new LogBuildTask(forPosition, woodType, direction));
		} else {
			throw new CommandEvaluationException("Cannot build " + blockToPlace);
		}
		return null;
	}

	public static final class StairsBlockFilter extends BlockFilter {
		@Override
		public boolean matches(Block b) {
			return BuildNormalStairsTask.BLOCKS.contains(b);
		}
	}

	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "schedule", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.POSITION, description = "Where to place it (relative is to your current pos)") Pos forPosition,
			@AICommandParameter(type = ParameterType.BLOCK_NAME, description = "The block", blockFilter = StairsBlockFilter.class) Block blockToPlace,
			@AICommandParameter(type = ParameterType.ENUM, description = "The direction the stairs face") ForgeDirection direction,
			@AICommandParameter(type = ParameterType.ENUM, description = "Upper for inverted stairs", optional = true) Half half) {
		if (BuildNormalStairsTask.BLOCKS.contains(blockToPlace)) {
			addTask(helper, new BuildNormalStairsTask(forPosition,
					blockToPlace, direction, half == null ? Half.LOWER : half));
		} else {
			throw new CommandEvaluationException("Cannot build " + blockToPlace);
		}
		return null;
	}

	public static final class SlabBlockFilter extends BlockFilter {
		@Override
		public boolean matches(Block b) {
			return BuildHalfslabTask.BLOCKS.contains(b);
		}
	}

	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "schedule", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.POSITION, description = "Where to place it (relative is to your current pos)") Pos forPosition,
			@AICommandParameter(type = ParameterType.BLOCK_NAME, description = "The block", blockFilter = SlabBlockFilter.class) Block blockToPlace,
			@AICommandParameter(type = ParameterType.ENUM, description = "The subtype of slabs to place") SlabType type,
			@AICommandParameter(type = ParameterType.ENUM, description = "If a upper or lower half should be placed") BlockSide side) {
		if (BuildHalfslabTask.BLOCKS.contains(blockToPlace)) {
			addTask(helper, new BuildHalfslabTask(forPosition, type, side));
		} else {
			throw new CommandEvaluationException("Cannot build " + blockToPlace);
		}
		return null;
	}

	public static final class SignBlockFilter extends BlockFilter {
		@Override
		public boolean matches(Block b) {
			return StandingSignBuildTask.BLOCKS.contains(b);
		}
	}

	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "schedule", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.POSITION, description = "Where to place it (relative is to your current pos)") Pos forPosition,
			@AICommandParameter(type = ParameterType.BLOCK_NAME, description = "The block", blockFilter = SignBlockFilter.class) Block blockToPlace,
			@AICommandParameter(type = ParameterType.ENUM, description = "The direction of the sign") SignDirection direction,
			@AICommandParameter(type = ParameterType.STRING, description = "The first line. Replace space with §.", optional = true) String text1,
			@AICommandParameter(type = ParameterType.STRING, description = "The first line. Replace space with §.", optional = true) String text2,
			@AICommandParameter(type = ParameterType.STRING, description = "The first line. Replace space with §.", optional = true) String text3,
			@AICommandParameter(type = ParameterType.STRING, description = "The first line. Replace space with §.", optional = true) String text4) {
		if (StandingSignBuildTask.BLOCKS.contains(blockToPlace)) {
			addTask(helper, new StandingSignBuildTask(forPosition, direction,
					new String[] { text1, text2, text3, text4 }));
		} else {
			throw new CommandEvaluationException("Cannot build " + blockToPlace);
		}
		return null;
	}

	private static void addTask(AIHelper helper, BuildTask blockBuildTask) {
		helper.buildManager.addTask(blockBuildTask);
	}
}
