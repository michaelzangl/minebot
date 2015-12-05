package net.famzangl.minecraft.minebot.ai.strategy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.BlockItemFilter;
import net.famzangl.minecraft.minebot.ai.command.AIChatController;
import net.famzangl.minecraft.minebot.ai.path.world.BlockMetaSet;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSets;
import net.famzangl.minecraft.minebot.ai.path.world.WorldData;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.famzangl.minecraft.minebot.ai.task.BlockHalf;
import net.famzangl.minecraft.minebot.ai.task.TaskOperations;
import net.famzangl.minecraft.minebot.ai.task.WaitTask;
import net.famzangl.minecraft.minebot.ai.task.inventory.GetOnHotBarTask;
import net.famzangl.minecraft.minebot.ai.task.move.AlignToGridTask;
import net.famzangl.minecraft.minebot.ai.task.move.WalkTowardsTask;
import net.famzangl.minecraft.minebot.ai.task.place.SneakAndPlaceAtHalfTask;
import net.famzangl.minecraft.minebot.ai.task.place.SneakAndPlaceAtSideTask;
import net.famzangl.minecraft.minebot.ai.utils.BlockArea.AreaVisitor;
import net.famzangl.minecraft.minebot.ai.utils.BlockCuboid;
import net.famzangl.minecraft.minebot.build.block.SlabFilter;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

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
	private static final int BLOCK_PLACE_DELAY = 4;

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

	private class BuildHalfslabVisitor implements AreaVisitor {

		private final BlockPos buildPos;
		private final BlockPos beforeBuild;

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
				+ "]";
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
		for (int i = 0; i < 9; ++i) {
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
				+ buildPos + " ater " + steps + " steps");

		if (beforeBuild == null) {
			BlockPos before = buildPos.subtract(direction.getDirectionVec());
			if (isAirAt(world, buildPos) && isHalfslabAt(world, before)) {
				// special case: we stand on air and one before us is slab. Walk
				// back to the slab. Most times because of desync.
				addTask(new WalkTowardsTask(before.getX(), before.getZ(), null));
				LOGGER.trace(MARKER_PROGRESS, "Standing on a cliff. Walk back to: " + before);
			} else {
				// cannot handle this.
				AIChatController.addChatLine("Please stand on a half slab.");
				LOGGER.info(MARKER_PROGRESS,
						"No valid half slab found. Required area is: "
								+ getSidewardsArea(buildPos.add(0, -1, 0)));
			}
			return;
		}

		if (isAirAtAndBelow(world, buildPos)) {
			LOGGER.trace(MARKER_PROGRESS, "Building bridge to: " + buildPos);
			addTask(new SneakToSideTask(beforeBuild, direction));

			BlockCuboid area = getSidewardsArea(buildPos.add(0, -1, 0));
			area.accept(new BuildHalfslabVisitor(buildPos, beforeBuild), world);

			// wait for the server to sync. If block disappears, we don't fall.
			addTask(new WaitTask(LAG_TEST_DELAY - BLOCK_PLACE_DELAY));
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
		return isAirAt(world, buildPos)
				&& BlockSets.AIR.isAt(world,
						getSidewardsArea(buildPos.add(0, -1, 0)));
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
