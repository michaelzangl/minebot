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

import java.util.ArrayList;
import java.util.List;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.ai.command.AICommandInvocation;
import net.famzangl.minecraft.minebot.ai.command.AICommandParameter;
import net.famzangl.minecraft.minebot.ai.command.ParameterType;
import net.famzangl.minecraft.minebot.ai.path.world.Pos;
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy;
import net.famzangl.minecraft.minebot.build.blockbuild.BuildTask;
import net.famzangl.minecraft.minebot.build.blockbuild.MirrorDirection;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

/**
 * Move all scheduled builds by x.
 * 
 * @author michael
 * 
 */
@AICommand(helpText = "Move everything that is scheduled in the direction you are looking / given", name = "minebuild")
public class CommandMove {
	private static final class MoveStrategy extends AIStrategy {
		private final BlockPos relative;
		private final Rotate rotate;
		private final MirrorDirection mirror;
		private boolean moved;

		private MoveStrategy(BlockPos relative, Rotate rotate,
				MirrorDirection mirror) {
			this.relative = relative;
			this.rotate = rotate;
			this.mirror = mirror;
		}

		@Override
		public boolean checkShouldTakeOver(AIHelper helper) {
			return !moved;
		}

		@Override
		protected TickResult onGameTick(AIHelper helper) {
			if (!moved) {
				final ArrayList<BuildTask> tasks = new ArrayList<BuildTask>();
				final BlockPos center = getCenter(helper.buildManager
						.getScheduled());
				while (helper.buildManager.peekNextTask() != null) {
					final BuildTask next = helper.buildManager.popNextTask();
					BlockPos nextPos = next.getForPosition().add(relative);
					if (rotate != null) {
						nextPos = rotate.apply(nextPos.subtract(center)).add(
								center);
					}
					if (mirror == MirrorDirection.EAST_WEST) {
						// mirror on x axes
						nextPos = new BlockPos(center.getX() * 2
								- nextPos.getX(), nextPos.getY(),
								nextPos.getZ());
					} else if (mirror == MirrorDirection.NORTH_SOUTH) {
						nextPos = new BlockPos(nextPos.getX(), nextPos.getY(),
								center.getZ() * 2 - nextPos.getZ());
					}

					final BuildTask task = next.withPositionAndRotation(
							nextPos, rotate == null ? 0 : rotate.r, mirror);
					tasks.add(task);
				}

				for (final BuildTask task : tasks) {
					helper.buildManager.addTask(task);
				}
				moved = true;
			}
			return TickResult.NO_MORE_WORK;
		}

		private BlockPos getCenter(List<BuildTask> scheduled) {
			double x = 0, y = 0, z = 0;
			for (final BuildTask s : scheduled) {
				final BlockPos pos = s.getForPosition();
				x += pos.getX();
				y += pos.getY();
				z += pos.getZ();
			}
			final int c = Math.max(1, scheduled.size());
			return new BlockPos((int) (x / c), (int) (y / c), (int) (z / c));
		}

		@Override
		public String getDescription(AIHelper helper) {
			return null;
		}
	}

	public enum Rotate {
		CLOCKWISE(1), COUNTERCLOCKWISE(3), HALF(2);
		public final int r;

		private Rotate(int r) {
			this.r = r;
		}

		public BlockPos apply(BlockPos blockPos) {
			switch (this) {
			case CLOCKWISE:
				return new BlockPos(-blockPos.getZ(), blockPos.getY(),
						blockPos.getX());
			case COUNTERCLOCKWISE:
				return new BlockPos(blockPos.getZ(), blockPos.getY(),
						-blockPos.getX());
			case HALF:
				return new BlockPos(-blockPos.getX(), blockPos.getY(),
						-blockPos.getZ());
			default:
				return blockPos;
			}
		}
	}

	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "move", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.NUMBER, fixedName = "", description = "How much") int howMuch) {
		return run(helper, nameArg, helper.getLookDirection(), howMuch);
	}

	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "move", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.ENUM, fixedName = "", description = "Direction") final EnumFacing dir,
			@AICommandParameter(type = ParameterType.NUMBER, fixedName = "", description = "How much") final int howMuch) {
		return new MoveStrategy(Pos.fromDir(dir).multiply(howMuch), null, null);
	}

	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "mirror", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.ENUM, fixedName = "", description = "Direction") final MirrorDirection dir) {
		return new MoveStrategy(Pos.ZERO, null, dir);
	}

	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "rotate", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.ENUM, fixedName = "", description = "Direction") final Rotate dir) {
		return new MoveStrategy(Pos.ZERO, dir, null);
	}
}
