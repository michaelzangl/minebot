package net.famzangl.minecraft.minebot.ai.strategy;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.BlockItemFilter;
import net.famzangl.minecraft.minebot.ai.command.AIChatController;
import net.famzangl.minecraft.minebot.ai.path.world.BlockMetaSet;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSets;
import net.famzangl.minecraft.minebot.ai.path.world.WorldData;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.famzangl.minecraft.minebot.ai.task.BlockSide;
import net.famzangl.minecraft.minebot.ai.task.TaskOperations;
import net.famzangl.minecraft.minebot.ai.task.WaitTask;
import net.famzangl.minecraft.minebot.ai.task.inventory.GetOnHotBarTask;
import net.famzangl.minecraft.minebot.ai.task.move.AlignToGridTask;
import net.famzangl.minecraft.minebot.ai.task.move.WalkTowardsTask;
import net.famzangl.minecraft.minebot.ai.task.place.SneakAndPlaceAtHalfTask;
import net.famzangl.minecraft.minebot.ai.task.place.SneakAndPlaceAtSideTask;
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

	private static final int LAG_TEST_DELAY = 15;
	private static final BlockSet FLOOR_HALF_SLABS;
	static {
		BlockSet fhs = BlockSets.EMPTY;
		for (int i = 0; i < 8; i++) {
			fhs = fhs.unionWith(new BlockMetaSet(Blocks.stone_slab, i));
			fhs = fhs.unionWith(new BlockMetaSet(Blocks.wooden_slab, i));
		}
		fhs = fhs.unionWith(new BlockMetaSet(Blocks.stone_slab2, 0));
		FLOOR_HALF_SLABS = fhs;
	}

	private static final BlockItemFilter SLABS_FILTER = new BlockItemFilter(
			FLOOR_HALF_SLABS);

	private static class BuildHalfslabBridgeTask extends
			SneakAndPlaceAtHalfTask {
		public BuildHalfslabBridgeTask(BlockPos buildPos, BlockPos beforeBuild) {
			super(buildPos, SLABS_FILTER, beforeBuild.subtract(buildPos.add(0,
					-1, 0)), buildPos.getY() - .5, BlockSide.LOWER_HALF);
		}

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

	private final BlockPos start;
	private final EnumFacing direction;
	private final int length;

	public AirbridgeStrategy(BlockPos start, EnumFacing direction, int length) {
		super();
		this.start = start;
		this.direction = direction;
		this.length = length;
		if (direction.getFrontOffsetY() != 0) {
			throw new IllegalArgumentException("Can only work horizontally.");
		}
	}

	@Override
	public String toString() {
		return "AirbridgeStrategy [start=" + start + ", direction=" + direction
				+ "]";
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
			if (SLABS_FILTER.matches(helper.getMinecraft().thePlayer.inventory.getStackInSlot(i))) {
				slabsFound  = true;
			}
		}
		if (!slabsFound) {
			AIChatController.addChatLine("I need slabs to build that bridge.");
			return;
		}

		BlockPos buildPos = playerPosition;
		BlockPos beforeBuild = null;
		int steps = 0;
		while (isHalfslabAt(world, buildPos) && isInLength(buildPos) && steps < 16 * 3) {
			beforeBuild = buildPos;
			buildPos = buildPos.add(direction.getDirectionVec());
			steps++;
		}

		if (beforeBuild == null) {
			BlockPos before = buildPos.subtract(direction.getDirectionVec());
			if (isAirAt(world, buildPos) && isHalfslabAt(world, before)) {
				// special case: we stand on air and one before us is slab. Walk back to the slab. Most times because of desync.
				addTask(new WalkTowardsTask(before.getX(), before.getZ(), null));
			} else {
				// cannot handle this.
				AIChatController.addChatLine("Please stand on a half slab.");
			}
			return;
		}
		
		if (isAirAtAndBelow(world, buildPos)) {
			addTask(new SneakToSideTask(beforeBuild, direction));

			addTask(new GetOnHotBarTask(SLABS_FILTER));

			addTask(new BuildHalfslabBridgeTask(buildPos, beforeBuild));

			// wait for the server to sync. If block disappears, we don't fall.
			addTask(new WaitTask(LAG_TEST_DELAY));
		} else if (isHalfslabAt(world, buildPos)) {
			// walk
			addTask(new WalkTowardsTask(buildPos.getX(), buildPos.getZ(), null));
		} else {
			AIChatController
					.addChatLine("The way i should build is not cleared.");
			return;
		}

	}

	private boolean isInLength(BlockPos pos) {
		return length < 0 || start.distanceSq(pos) + .5 < length * length;
	}

	private boolean isHalfslabAt(WorldData world, BlockPos buildPos) {
		return isAirAt(world, buildPos)
				&& FLOOR_HALF_SLABS.isAt(world, buildPos.add(0, -1, 0));
	}

	private boolean isAirAtAndBelow(WorldData world, BlockPos buildPos) {
		return isAirAt(world, buildPos)
				&&BlockSets.AIR.isAt(world, buildPos.add(0, -1, 0));
	}

	private boolean isAirAt(WorldData world, BlockPos buildPos) {

		return (buildPos.getY() > 255 || BlockSets.AIR.isAt(world, buildPos))
				&& (buildPos.getY() > 254 || BlockSets.AIR.isAt(world,
						buildPos.add(0, 1, 0)));
	}

	@Override
	public String getDescription(AIHelper helper) {
		return "Build airbridge.";
	}
}
