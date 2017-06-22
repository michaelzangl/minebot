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
package net.famzangl.minecraft.minebot.build.commands;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.command.AIChatController;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.ai.command.AICommandInvocation;
import net.famzangl.minecraft.minebot.ai.command.AICommandParameter;
import net.famzangl.minecraft.minebot.ai.command.BlockWithDataOrDontcare;
import net.famzangl.minecraft.minebot.ai.command.ParameterType;
import net.famzangl.minecraft.minebot.ai.command.SafeStrategyRule;
import net.famzangl.minecraft.minebot.ai.path.ClearAreaPathfinder;
import net.famzangl.minecraft.minebot.ai.path.ClearAreaPathfinder.ClearMode;
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.PathFinderStrategy;
import net.famzangl.minecraft.minebot.ai.utils.BlockCuboid;
import net.minecraft.util.math.BlockPos;

@AICommand(helpText = "Clears the selected area.", name = "minebuild")
public class CommandClearArea {

	private static final class ClearAreaStrategy extends PathFinderStrategy {
		private String progress = "?";
		private boolean done = false;
		private final ClearAreaPathfinder pathFinder;

		private ClearAreaStrategy(ClearAreaPathfinder pathFinder) {
			super(pathFinder, "");
			this.pathFinder = pathFinder;
		}

		@Override
		public void searchTasks(AIHelper helper) {
			final int max = pathFinder.getAreaSize();
			if (max <= 100000) {
				float toClearCount = pathFinder.getToClearCount(helper);
				progress = 100 - Math.round(100f * toClearCount / max) + "%";
				done = toClearCount == 0;
			}
			super.searchTasks(helper);
		}

		@Override
		public String getDescription(AIHelper helper) {
			return "Clear area: " + progress;
		}

		@Override
		public boolean hasFailed() {
			return !done;
		}
	}

	@AICommandInvocation(safeRule = SafeStrategyRule.DEFEND_MINING)
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "clear", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.BLOCK_NAME, description = "restrict to block", optional = true) BlockWithDataOrDontcare block,
			@AICommandParameter(type = ParameterType.ENUM, description = "clear mode", optional = true) ClearMode mode) {
		BlockCuboid area =  getArea(helper);
		if (area != null)  {
			return new ClearAreaStrategy(new ClearAreaPathfinder(area, block,
					mode == null ? ClearMode.VISIT_EVERY_POS : mode));
		} else {
			return null;
		}
	}
	
	public static BlockCuboid getArea(AIHelper helper) {
		final BlockPos pos1 = helper.getPos1();
		final BlockPos pos2 = helper.getPos2();
		if (pos1 == null || pos2 == null) {
			AIChatController.addChatLine("Set positions first.");
			return null;
		} else {
			return new BlockCuboid(pos1, pos2);
		}
	}
}
