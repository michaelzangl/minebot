package net.famzangl.minecraft.minebot.build.commands;

import java.util.ArrayList;
import java.util.List;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.ai.command.AICommandInvocation;
import net.famzangl.minecraft.minebot.ai.command.AICommandParameter;
import net.famzangl.minecraft.minebot.ai.command.ParameterType;
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy;
import net.famzangl.minecraft.minebot.build.blockbuild.BuildTask;
import net.famzangl.minecraft.minebot.build.blockbuild.MirrorDirection;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Move all scheduled builds by x.
 * 
 * @author michael
 * 
 */
@AICommand(helpText = "Move everything that is scheduled in the direction you are looking / given", name = "minebuild")
public class CommandMove {
	private static final class MoveStrategy extends AIStrategy {
		private final Pos relative;
		private final Rotate rotate;
		private final MirrorDirection mirror;
		private boolean moved;

		private MoveStrategy(Pos relative, Rotate rotate, MirrorDirection mirror) {
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
				final Pos center = getCenter(helper.buildManager.getScheduled());
				while (helper.buildManager.peekNextTask() != null) {
					final BuildTask next = helper.buildManager.popNextTask();
					Pos nextPos = next.getForPosition().add(relative);
					if (rotate != null) {
						nextPos = rotate.apply(nextPos.subtract(center)).add(
								center);
					}
					if (mirror == MirrorDirection.EAST_WEST) {
						// mirror on x axes
						nextPos = new Pos(center.x * 2 - nextPos.x, nextPos.y,
								nextPos.z);
					} else if (mirror == MirrorDirection.NORTH_SOUTH) {
						nextPos = new Pos(nextPos.x, nextPos.y, center.z * 2
								- nextPos.z);
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

		private Pos getCenter(List<BuildTask> scheduled) {
			double x = 0, y = 0, z = 0;
			for (final BuildTask s : scheduled) {
				final Pos pos = s.getForPosition();
				x += pos.x;
				y += pos.y;
				z += pos.z;
			}
			final int c = Math.max(1, scheduled.size());
			return new Pos((int) (x / c), (int) (y / c), (int) (z / c));
		}

		@Override
		public String getDescription(AIHelper helper) {
			return null;
		}
	}

	public enum Rotate {
		CLOCKWISE(1),
		COUNTERCLOCKWISE(3),
		HALF(2);
		public final int r;

		private Rotate(int r) {
			this.r = r;
		}

		public Pos apply(Pos current) {
			switch (this) {
			case CLOCKWISE:
				return new Pos(-current.z, current.y, current.x);
			case COUNTERCLOCKWISE:
				return new Pos(current.z, current.y, -current.x);
			case HALF:
				return new Pos(-current.x, current.y, -current.z);
			default:
				return current;
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
			@AICommandParameter(type = ParameterType.ENUM, fixedName = "", description = "Direction") final ForgeDirection dir,
			@AICommandParameter(type = ParameterType.NUMBER, fixedName = "", description = "How much") final int howMuch) {
		return new MoveStrategy(Pos.fromDir(dir).multiply(howMuch), null, null);
	}

	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "mirror", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.ENUM, fixedName = "", description = "Direction") final MirrorDirection dir) {
		return new MoveStrategy(new Pos(0, 0, 0), null, dir);
	}

	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "rotate", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.ENUM, fixedName = "", description = "Direction") final Rotate dir) {
		return new MoveStrategy(new Pos(0, 0, 0), dir, null);
	}
}
