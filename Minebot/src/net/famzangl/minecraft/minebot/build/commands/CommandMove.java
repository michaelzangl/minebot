package net.famzangl.minecraft.minebot.build.commands;

import java.util.ArrayList;
import java.util.List;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.AIStrategy;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.ai.command.AICommandInvocation;
import net.famzangl.minecraft.minebot.ai.command.AICommandParameter;
import net.famzangl.minecraft.minebot.ai.command.ParameterType;
import net.famzangl.minecraft.minebot.ai.task.AITask;
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
	private static final class MoveStrategy implements AIStrategy {
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
		public void searchTasks(AIHelper helper) {
			if (!moved) {
				helper.addTask(new MoveTask(relative, rotate, mirror));
				moved = true;
			}
		}

		@Override
		public AITask getOverrideTask(AIHelper helper) {
			return null;
		}

		@Override
		public String getDescription() {
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

	private static final class MoveTask extends AITask {
		private final Pos relative;
		private final Rotate rotate;
		private final MirrorDirection mirror;
		private boolean moved;

		private MoveTask(Pos relative, Rotate rotate, MirrorDirection mirror) {
			this.relative = relative;
			this.rotate = rotate;
			this.mirror = mirror;
		}

		@Override
		public void runTick(AIHelper h) {
			ArrayList<BuildTask> tasks = new ArrayList<BuildTask>();
			Pos center = getCenter(h.buildManager.getScheduled());
			while (h.buildManager.peekNextTask() != null) {
				BuildTask next = h.buildManager.popNextTask();
				Pos nextPos = next.getForPosition().add(relative);
				if (rotate != null) {
					nextPos = rotate.apply(nextPos.subtract(center))
							.add(center);
				}
				if (mirror == MirrorDirection.EAST_WEST) {
					// mirror on x axes
					nextPos = new Pos(center.x * 2 - nextPos.x, nextPos.y,
							nextPos.z);
				} else if (mirror == MirrorDirection.NORTH_SOUTH) {
					nextPos = new Pos(nextPos.x, nextPos.y, center.z * 2
							- nextPos.z);
				}

				BuildTask task = next.withPositionAndRotation(nextPos,
						rotate == null ? 0 : rotate.r, mirror);
				tasks.add(task);
			}

			for (BuildTask task : tasks) {
				h.buildManager.addTask(task);
			}
			moved = true;
		}

		private Pos getCenter(List<BuildTask> scheduled) {
			double x = 0, y = 0, z = 0;
			for (BuildTask s : scheduled) {
				Pos pos = s.getForPosition();
				x += pos.x;
				y += pos.y;
				z += pos.z;
			}
			int c = Math.max(1, scheduled.size());
			return new Pos((int) (x / c), (int) (y / c), (int) (z / c));
		}

		@Override
		public boolean isFinished(AIHelper h) {
			return moved;
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
