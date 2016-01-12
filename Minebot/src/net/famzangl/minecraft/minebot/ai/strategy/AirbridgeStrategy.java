package net.famzangl.minecraft.minebot.ai.strategy;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.BlockItemFilter;
import net.famzangl.minecraft.minebot.ai.command.AIChatController;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSets;
import net.famzangl.minecraft.minebot.ai.path.world.WorldData;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.famzangl.minecraft.minebot.ai.task.BlockHalf;
import net.famzangl.minecraft.minebot.ai.task.RunOnceTask;
import net.famzangl.minecraft.minebot.ai.task.TaskOperations;
import net.famzangl.minecraft.minebot.ai.task.WaitTask;
import net.famzangl.minecraft.minebot.ai.task.inventory.GetOnHotBarTask;
import net.famzangl.minecraft.minebot.ai.task.move.WalkTowardsTask;
import net.famzangl.minecraft.minebot.ai.task.place.SneakAndPlaceAtHalfTask;
import net.famzangl.minecraft.minebot.ai.utils.BlockArea;
import net.famzangl.minecraft.minebot.ai.utils.BlockArea.AreaVisitor;
import net.famzangl.minecraft.minebot.ai.utils.BlockCuboid;
import net.famzangl.minecraft.minebot.ai.utils.BlockFilteredArea;
import net.famzangl.minecraft.minebot.ai.utils.ReverseAcceptingArea;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

/**
 * Build a bridge through the air.
 * 
 * @author Michael Zangl
 *
 */
public class AirbridgeStrategy extends TaskStrategy {
	private static final Marker MARKER_PROGRESS = MarkerManager
			.getMarker("progress");
	private static final Logger LOGGER = LogManager
			.getLogger(AirbridgeStrategy.class);

	private static final int LAG_TEST_DELAY = 15;
	private static final int BLOCK_PLACE_DELAY = 3;

	private static final BlockItemFilter SLABS_FILTER = new BlockItemFilter(
			BlockSets.LOWER_SLABS);

	public static SneakAndPlaceAtHalfTask getBuildHafslabTask(
			BlockPos buildPos, BlockPos walkToPos, BlockPos beforeBuild) {
		return new SneakAndPlaceAtHalfTask(walkToPos, SLABS_FILTER,
				beforeBuild, buildPos, buildPos.getY() + .5,
				BlockHalf.LOWER_HALF);
	}

	/**
	 * A fast sneak task.
	 * 
	 * @author michael
	 *
	 */
	private static class SneakToSideTask extends AITask {
		private BlockPos pos;
		private EnumFacing inDirection;
		private boolean arrived;

		public SneakToSideTask(BlockPos pos, EnumFacing inDirection) {
			super();
			this.pos = pos;
			this.inDirection = inDirection;
		}

		@Override
		public void runTick(AIHelper h, TaskOperations o) {
			arrived = h.sneakFrom(pos, inDirection, false);
		}

		@Override
		public boolean isFinished(AIHelper h) {
			return arrived;
		}
	}

	private static final class DynamicWaitTask extends AITask {
		private long startTime = 0;

		@Override
		public void runTick(AIHelper h, TaskOperations o) {
		}

		@Override
		public boolean isFinished(AIHelper h) {
			return h.getMinecraft().theWorld.getTotalWorldTime() >= startTime + LAG_TEST_DELAY;
		}

		public AITask getTrigger() {
			return new RunOnceTask() {
				@Override
				protected void runOnce(AIHelper h, TaskOperations o) {
					startTime = h.getMinecraft().theWorld.getTotalWorldTime();
				}
			};
		}
	}
	private class BuildHalfslabVisitor implements AreaVisitor {


		private final BlockPos buildPos;
		private final BlockPos beforeBuild;
		private final DynamicWaitTask waitTask = new DynamicWaitTask();

		public BuildHalfslabVisitor(BlockPos buildPos, BlockPos beforeBuild) {
			this.buildPos = buildPos;
			this.beforeBuild = beforeBuild;
		}

		@Override
		public void visit(WorldData world, int x, int y, int z) {
			BlockPos pos = new BlockPos(x, y, z);
			LOGGER.trace(MARKER_PROGRESS, "Schedule half slab for: " + pos);

			addTask(new GetOnHotBarTask(SLABS_FILTER));
			addTask(getBuildHafslabTask(pos, buildPos,
					beforeBuild));
			addTask(new WaitTask(BLOCK_PLACE_DELAY));
			
			if (buildPos.add(0, -1, 0).equals(pos)) {
				addTask(waitTask.getTrigger());
			}
		}

		public AITask getWaitTask() {
			return waitTask;
		}

	}

	private final BlockPos start;
	private final EnumFacing direction;
	private final int length;
	private final int toLeft;
	private final int toRight;

	public AirbridgeStrategy(BlockPos start, EnumFacing direction, int length,
			int toLeft, int toRight) {
		super();
		this.start = start;
		this.direction = direction;
		this.length = length;
		this.toLeft = toLeft;
		this.toRight = toRight;
		if (direction.getFrontOffsetY() != 0) {
			throw new IllegalArgumentException("Can only work horizontally.");
		}
	}

	@Override
	public String toString() {
		return "AirbridgeStrategy [start=" + start + ", direction=" + direction
				+ ", length=" + length + "]";
	}

	private BlockCuboid getSidewardsArea(BlockPos center) {
		return new BlockCuboid(center, center).extend(toLeft,
				direction.rotateY()).extend(toRight, direction.rotateYCCW());
	}

	@Override
	protected void searchTasks(AIHelper helper) {
		WorldData world = helper.getWorld();

		BlockPos playerPosition = helper.getPlayerPosition();
		if (!isInLength(playerPosition)) {
			// arrived.
			return;
		}

		boolean slabsFound = false;
		for (int i = 0; i < 9 * 4; ++i) {
			if (SLABS_FILTER.matches(helper.getMinecraft().thePlayer.inventory
					.getStackInSlot(i))) {
				slabsFound = true;
			}
		}
		if (!slabsFound) {
			AIChatController.addChatLine("I need slabs to build that bridge.");
			return;
		}

		BlockPos buildPos = playerPosition;
		BlockPos beforeBuild = null;
		int steps = 0;
		while (isHalfslabAt(world, buildPos) && isInLength(buildPos)
				&& steps < 16 * 3) {
			beforeBuild = buildPos;
			buildPos = buildPos.add(direction.getDirectionVec());
			steps++;
		}
		LOGGER.trace(MARKER_PROGRESS, "Suggesting next build position: "
				+ buildPos + " after " + steps + " steps");

		if (beforeBuild == null) {
			BlockPos before = buildPos.subtract(direction.getDirectionVec());
			if (isAirAt(world, buildPos) && isHalfslabAt(world, before)) {
				// special case: we stand on air and one before us is slab. Walk
				// back to the slab. Most times because of desync.
				addTask(new WalkTowardsTask(before.getX(), before.getZ(), null));
				LOGGER.trace(MARKER_PROGRESS, "Standing on a cliff. Walk back to: " + before);
			} else {
				//Kristopher Mueller -- The Following help texts added to address a user incorrectly calling /minebot airbridge 'width'
				if(this.toLeft == 1 || this.toRight == 1) {
					AIChatController.addChatLine("First Build a 1x3 half-slab platform and stand on the middle block.");
				} else if(this.toLeft == 2 || this.toRight == 2) {
					AIChatController.addChatLine("First Build a 1x5 half-slab platform and stand on the middle block.");
				} else if(this.toLeft == 3 || this.toRight == 3) {
					AIChatController.addChatLine("First Build a 1x7 half-slab platform and stand on the middle block.");	
				} else {
					// cannot handle this.
					AIChatController.addChatLine("Please stand on a half slab.");
				
				LOGGER.info(MARKER_PROGRESS,
						"No valid half slab found. Required area is: "
								+ getSidewardsArea(buildPos.add(0, -1, 0)));
				}
			}
			return;
		}

		if (isAirAtAndBelow(world, buildPos)) {
			LOGGER.trace(MARKER_PROGRESS, "Building bridge to: " + buildPos);
			addTask(new SneakToSideTask(beforeBuild, direction));

			BlockCuboid area = getSidewardsArea(buildPos.add(0, -1, 0));
			BlockArea toPlace = new BlockFilteredArea(area, BlockSets.AIR);
			if ((buildPos.getX() & 1) == (buildPos.getZ() & 1)) {
				toPlace = new ReverseAcceptingArea(toPlace);
			}
			BuildHalfslabVisitor buildHalfslabVisitor = new BuildHalfslabVisitor(buildPos, beforeBuild);
			toPlace.accept(buildHalfslabVisitor, world);

			// wait for the server to sync. If block disappears, we don't fall.
			addTask(buildHalfslabVisitor.getWaitTask());
		} else if (isHalfslabAt(world, buildPos)) {
			// walk
			addTask(new WalkTowardsTask(buildPos.getX(), buildPos.getZ(), null));
			LOGGER.trace(MARKER_PROGRESS, "Walk to: " + buildPos);
		} else {
			AIChatController
					.addChatLine("The way I should build is not cleared.");
			return;
		}

	}

	private boolean isInLength(BlockPos pos) {
		return length < 0 || start.distanceSq(pos) + .5 < length * length;		
	}

	private boolean isHalfslabAt(WorldData world, BlockPos buildPos) {
		return isAirAt(world, buildPos)
				&& BlockSets.LOWER_SLABS.isAt(world,
						getSidewardsArea(buildPos.add(0, -1, 0)));
	}

	private boolean isAirAtAndBelow(WorldData world, BlockPos buildPos) {
		if (!isAirAt(world, buildPos)) {
			return false;
		}
		BlockCuboid floor = getSidewardsArea(buildPos.add(0, -1, 0));
		if (!BlockSets.AIR.unionWith(BlockSets.LOWER_SLABS).isAt(world, floor)) {
			return false;
		} else {
			return new BlockFilteredArea(floor, BlockSets.AIR).getVolume(world) > 0;
		}
	}

	private boolean isAirAt(WorldData world, BlockPos buildPos) {
		return (buildPos.getY() > 255 || BlockSets.AIR.isAt(world,
				getSidewardsArea(buildPos)))
				&& (buildPos.getY() > 254 || BlockSets.AIR.isAt(world,
						getSidewardsArea(buildPos.add(0, 1, 0))));
	}

	@Override
	public String getDescription(AIHelper helper) {
		return "Build airbridge.";
	}
}
