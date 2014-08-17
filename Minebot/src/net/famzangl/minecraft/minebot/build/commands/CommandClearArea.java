package net.famzangl.minecraft.minebot.build.commands;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.command.AIChatController;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.ai.command.AICommandInvocation;
import net.famzangl.minecraft.minebot.ai.command.AICommandParameter;
import net.famzangl.minecraft.minebot.ai.command.ParameterType;
import net.famzangl.minecraft.minebot.ai.path.ClearAreaPathfinder;
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.PathFinderStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.ValueActionStrategy;

@AICommand(helpText = "Clears the selected area.", name = "minebuild")
public class CommandClearArea {

	private static final class ClearAreaStrategy extends PathFinderStrategy {
		private String progress = "?";
		private final ClearAreaPathfinder pathFinder;

		private ClearAreaStrategy(ClearAreaPathfinder pathFinder) {
			super(pathFinder, "");
			this.pathFinder = pathFinder;
		}

		@Override
		public void searchTasks(AIHelper helper) {
			final int max = pathFinder.getAreaSize();
			if (max <= 100000) {
				progress = 100
						- Math.round(100f * pathFinder.getToClearCount(helper)
								/ max) + "%";
			}
			super.searchTasks(helper);
		}

		@Override
		public String getDescription() {
			return "Clear area: " + progress;
		}
	}

	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "clear", description = "") String nameArg) {
		final Pos pos1 = helper.getPos1();
		final Pos pos2 = helper.getPos2();
		if (pos1 == null || pos2 == null) {
			AIChatController.addChatLine("Set positions first.");
			return null;
		} else {
			return ValueActionStrategy.makeSafe(new ClearAreaStrategy(
					new ClearAreaPathfinder(pos1, pos2)));
		}
	}
}
