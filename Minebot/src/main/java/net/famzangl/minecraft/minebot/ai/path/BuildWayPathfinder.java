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
package net.famzangl.minecraft.minebot.ai.path;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.BlockItemFilter;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSets;
import net.famzangl.minecraft.minebot.ai.path.world.WorldData;
import net.famzangl.minecraft.minebot.ai.task.*;
import net.famzangl.minecraft.minebot.ai.task.inventory.GetOnHotBarTask;
import net.famzangl.minecraft.minebot.ai.utils.AbstractFilteredArea;
import net.famzangl.minecraft.minebot.ai.utils.BlockCuboid;
import net.famzangl.minecraft.minebot.ai.utils.BlockFilteredArea;
import net.famzangl.minecraft.minebot.build.WalkTowardsTask;
import net.famzangl.minecraft.minebot.build.blockbuild.AbstractBuildTask;
import net.famzangl.minecraft.minebot.build.blockbuild.BlockBuildTask;
import net.famzangl.minecraft.minebot.build.blockbuild.SlabBuildTask;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SlabBlock;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Build a way. it is covered with cobblestone slabs (CSS). Bridge profile:
 * 
 * <pre>
 * (torch)            (torch) 
 * CSW   Air    Air    CSW    &lt;- Layer 0
 * CS    CSS    CSS    CS
 * </pre>
 * 
 * Tunnel profile:
 * 
 * <pre>
 * Something Sth Sth Something 
 * Something         Something 
 * Something         Something &lt;- Layer 0
 * Something CSS CSS Something
 * 
 * The lower left CSS is always where the player needs to stand to start building the way.
 * 
 * 
 * @author michael
 * 
 */
public class BuildWayPathfinder extends AlongTrackPathFinder {
	private static final Logger LOGGER = LogManager.getLogger(BuildWayPathfinder.class);

	/**
	 * The things to place on the floor
	 */
	private static final BlockState FLOOR = Blocks.COBBLESTONE_SLAB.getDefaultState().with(SlabBlock.TYPE, SlabType.BOTTOM);
	private static final BlockSet FLOOR_BLOCKS = BlockSet.builder().add(FLOOR).build();

	/**
	 * What to place on the side on a bride
	 */
	private static final Block BRIDGE_SIDE = Blocks.COBBLESTONE;
	/**
	 * Wall so that people do not fall of the bridge
	 */
	private static final Block BRIDGE_WALL = Blocks.COBBLESTONE_WALL;

	/**
	 * Width of the way to build. Not changeable at the moment, but could easily be.
	 */
	public static final int DEFAULT_WIDTH = 2;
	private final int width = DEFAULT_WIDTH;

	private BuildWayPathfinder(Direction dir, BlockPos pos) {
		super(dir.getXOffset(), dir.getZOffset(), pos.getX(), pos
				.getY(), pos.getZ(), -1);
	}

	/**
	 * Automatically detect the best position to start the path finding at
	 * @param inDirection Direction. This is fixed
	 * @param world The world we are in
	 * @return The pathfinder.
	 */
	public static BuildWayPathfinder findContinue(Direction inDirection, WorldData world) {
		int cx = world.getPlayerPosition().getX();
		int cy = world.getPlayerPosition().getY();
		int cz = world.getPlayerPosition().getZ();
		for (int dy = 0; dy > -2; dy--) {
			for (int dSide = 0; dSide < DEFAULT_WIDTH; dSide++) {
				BlockPos near = new BlockPos(cx + inDirection.getZOffset() * (-dSide), cy + dy, cz - inDirection.getXOffset() * (-dSide));
				BlockPos far = new BlockPos(cx + inDirection.getZOffset() * (-dSide + DEFAULT_WIDTH - 1), cy + dy, cz - inDirection.getXOffset() * (-dSide + DEFAULT_WIDTH - 1));
				LOGGER.debug("Checking if we can continue way with slabs at {}, {}", near, far);
				if (new BlockFilteredArea<>(new BlockCuboid<>(near, far), FLOOR_BLOCKS).getVolume(world) == DEFAULT_WIDTH) {
					// We found the slabs => start building there
					LOGGER.debug("Found a way to continue at {}, {} in direction {}", near, far, inDirection);
					return new BuildWayPathfinder(inDirection, near.add(0, 1, 0));
				}
			}
		}
		// Just start with the player position.
		LOGGER.debug("Starting new way with player standing at {}, going to {}", world.getPlayerPosition(), inDirection);
		return new BuildWayPathfinder(inDirection, world.getPlayerPosition().add(0, 1, 0));
	}

	/**
	 * Each way type needs to be able to be built from either the prevoious way
	 * piece or the current.
	 * 
	 * @author michael
	 * 
	 */
	private abstract class WayPiece {
		// private static final SlabFilter FLOOR = new
		// SlabFilter(SlabType.STONE);
		protected boolean placeTorch;
		private final int stepIndex;
		private boolean done;

		public WayPiece(int stepIndex) {
			this.stepIndex = stepIndex;
		}

		/**
		 * get a position for the current way build step
		 * @param u Sideways
		 * @param dy Upwards
		 * @return The block pos
		 */
		protected BlockPos getPos(int u, int dy) {
			return new BlockPos(cx + dx * stepIndex + dz * u, cy + dy, cz + dz
					* stepIndex - dx * u);
		}

		public boolean isDone() {
			return done;
		}

		/**
		 * Add construction tasks while standing on the base halfslab
		 * 
		 * @param currentPos left side of the road.
		 */
		public void addConstructionTasks(BlockPos currentPos) {
			// Open the menu, get the items we need on the hotbar
			addTask(new GetOnHotBarTask(new BlockItemFilter(FLOOR_BLOCKS)));
			if (placeTorch) {
				// TODO: Do not drop the floor blocks here
				addTask(new GetOnHotBarTask(new BlockItemFilter(Blocks.TORCH)));
			}

			// This is the first floor block. Place a slab there.
			// There are 3 places we can be:
			// * At the side of that slab (=> from previous task)
			// * At the bottom of that slab (=> from pathfiner)
			final BlockPos first = getPos(0, -1);
			final AITask placeTask = new SlabBuildTask(first, FLOOR).getPlaceBlockTask(currentPos
					.subtract(first));
			addTask(placeTask);

			// Now we are standing on the first slab. Clear everything else
			addTask(getClearAreaTask());

			// The tasks to build the floor slabs
			for (int i = 1; i < width; i++) {
				final BlockPos current = getPos(i, -1);
				addTask(new SlabBuildTask(current, FLOOR)
						.getPlaceBlockTask(getPos(i - 1, 0).subtract(current)));
			}

			// Tasks for building the wall on the far side, if needed
			addFarSideBuildTasks();

			// Walk back to the starting side
			addTask(new WalkTowardsTask(getPos(width - 1, 0), getPos(0, 0)));

			// Tasks for building the wall on this side
			addNearSideBuildTasks();

			done = true;
		}

		protected void addFarSideBuildTasks() {
			addTask(new WalkTowardsTask(getPos(width, 1), getPos(width - 1, 0)));
		}

		protected void addNearSideBuildTasks() {
			addTask(new WalkTowardsTask(getPos(-1, 1), getPos(0, 0)));
		}

		protected DestroyInRangeTask getClearAreaTask() {
			BlockCuboid<WorldData> areaToClear = new BlockCuboid<>(getPos(0, -1), getPos(width - 1, 1));
			return getClearAreaTask(areaToClear);
		}

		protected DestroyInRangeTask getClearAreaTask(BlockCuboid<WorldData> areaToClear) {
			AbstractFilteredArea<WorldData> allExceptCorrectFloor = new AreaWithoutFloor(areaToClear);
			return new DestroyInRangeTask(allExceptCorrectFloor) {
				@Override
				public void runTick(AIHelper aiHelper, TaskOperations taskOperations) {
					if (allExceptCorrectFloor.contains(aiHelper.getWorld(), getPos(0, -1))) {
						throw new IllegalStateException("Not standing on a half slab to clear the area.");
					}
					super.runTick(aiHelper, taskOperations);
				}
			};
		}

		public void addConstructionTasksFromInner() {
			BlockPos playerStartPosition = getPos(0, -1);
			LOGGER.debug("Scheduling construction standing on the previous way position at {}", playerStartPosition);
			// Clear the place we need to start at - if it will not be a slab.
			addTask(new DestroyInRangeTask(playerStartPosition, getPos(0, 1)));

			addConstructionTasks(playerStartPosition);
		}

		public void addConstructionTasksFromPrevious(BlockPos current) {
			LOGGER.debug("Scheduling construction standing on the previous way position at {}", current);
			final BlockPos floorPos = getPos(0, -1);

			addTask(new DestroyInRangeTask(floorPos, getPos(0, 1)));

			addConstructionTasks(current);
		}

		protected final void addNearSide() {
			final AbstractBuildTask task = new BlockBuildTask(getPos(-1, -1),
					BRIDGE_SIDE.getDefaultState());
			addTask(task.getPlaceBlockTask(getPos(1, 1).subtract(getPos(0, 0))));
		}

		protected final void addFarSide() {
			final AbstractBuildTask task = new BlockBuildTask(getPos(width, -1),
					BRIDGE_SIDE.getDefaultState());
			addTask(task
					.getPlaceBlockTask(getPos(-1, 1).subtract(getPos(0, 0))));
		}

		private class AreaWithoutFloor extends AbstractFilteredArea<WorldData> {
			public AreaWithoutFloor(BlockCuboid<WorldData> areaToClear) {
				super(areaToClear);
			}

			@Override
			protected boolean test(WorldData world, int x, int y, int z) {
				return y >= cy || !FLOOR_BLOCKS.isAt(world, x, y, z);
			}

			@Override
			public String toString() {
				return "AreaWithoutFloor{base = " + base + "}";
			}
		}
	}

	private class NormalWayType extends WayPiece {
		private final BlockSet airLike = BlockSet.builder().add(BlockSets.AIR)
				.add(BlockSets.FEET_CAN_WALK_THROUGH).build();
		private final BlockSet COVERED = BlockSet.builder().add(BlockSets.SIMPLE_CUBE)
				.add(BlockSets.FALLING).build();

		public NormalWayType(int stepIndex) {
			super(stepIndex);
		}

		public boolean needsTunnel() {
			int covered = 0;
			for (int y = 2; y <= 3; y++) {
				for (int u = -1; u <= width + 1; u++) {
					if (COVERED.isAt(world, getPos(u, y))) {
						covered++;
					}
				}

			}
			return covered >= 1 + width;
		}

		public boolean needsBridge() {
			return isAirlike(-1, 0) && isAirlike(-1, -1) && isAirlike(-1, -2)
					|| isAirlike(width, 0) && isAirlike(width, -1)
					&& isAirlike(width, -2);
		}

		private boolean isAirlike(int u, int dy) {
			final BlockState block = world.getBlockState(getPos(u, dy));
			return airLike.contains(block);
		}
	}

	private class BridgeWayType extends WayPiece {

		public BridgeWayType(int stepIndex) {
			super(stepIndex);
		}

		@Override
		public void addConstructionTasks(BlockPos currentPos) {
			addTask(new GetOnHotBarTask(new BlockItemFilter(BRIDGE_WALL)));
			addTask(new GetOnHotBarTask(new BlockItemFilter(BRIDGE_SIDE)));
			super.addConstructionTasks(currentPos);
		}

		@Override
		protected void addFarSideBuildTasks() {
			addFarSide();
			super.addFarSideBuildTasks();
			addTask(new PlaceBlockTask(getPos(width, -1), Direction.UP,
					BlockSet.builder().add(BRIDGE_WALL).build()));
			if (placeTorch) {
				addTask(new PlaceTorchSomewhereTask(
						Collections.singletonList(getPos(width, 1)),
						Direction.DOWN));
			}
		}

		@Override
		protected void addNearSideBuildTasks() {
			addNearSide();
			super.addNearSideBuildTasks();
			addTask(new PlaceBlockTask(getPos(-1, -1), Direction.UP,
					BlockSet.builder().add(BRIDGE_WALL).build()));
			if (placeTorch) {
				addTask(new PlaceTorchSomewhereTask(
						Collections.singletonList(getPos(-1, 1)),
						Direction.DOWN));
			}
		}

		@Override
		protected DestroyInRangeTask getClearAreaTask() {
			return getClearAreaTask(new BlockCuboid<>(getPos(-1, -1), getPos(width, 2)));
		}
	}

	private class TunnelWayType extends WayPiece {
		// TODO: Add tasks to create a ceiling/wall.

		public TunnelWayType(int stepIndex) {
			super(stepIndex);
		}

		@Override
		protected void addNearSideBuildTasks() {
			super.addNearSideBuildTasks();
			if (placeTorch) {
				addTask(new PlaceTorchSomewhereTask(Arrays.asList(getPos(0, 1),
						getPos(0, 0), getPos(-1, 1)), getForwardDirection()
						.rotateY(), Direction.DOWN));
			}
		}

		@Override
		protected void addFarSideBuildTasks() {
			super.addFarSideBuildTasks();
			if (placeTorch) {
				addTask(new PlaceTorchSomewhereTask(Arrays.asList(
						getPos(width - 1, 1), getPos(width - 1, 0),
						getPos(width, 1)), getForwardDirection().rotateYCCW(),
						Direction.DOWN));
			}
		}
	}

	private class FlatlandWayType extends WayPiece {

		public FlatlandWayType(int stepIndex) {
			super(stepIndex);
		}

		@Override
		public void addConstructionTasks(BlockPos currentPos) {
			if (placeTorch) {
				addTask(new GetOnHotBarTask(new BlockItemFilter(BRIDGE_SIDE)));
			}
			super.addConstructionTasks(currentPos);
		}

		@Override
		protected DestroyInRangeTask getClearAreaTask() {
			if (placeTorch) {
				return getClearAreaTask(new BlockCuboid<>(getPos(-1, -1), getPos(width, 2)));
			} else {
				return super.getClearAreaTask();
			}
		}

		@Override
		protected void addFarSideBuildTasks() {
			if (placeTorch) {
				addFarSide();
			}
			super.addFarSideBuildTasks();
			if (placeTorch) {
				addTask(new PlaceTorchSomewhereTask(
						Collections.singletonList(getPos(width, 0)),
						Direction.DOWN));
			}
		}

		@Override
		protected void addNearSideBuildTasks() {
			if (placeTorch) {
				addNearSide();
			}
			super.addNearSideBuildTasks();
			if (placeTorch) {
				addTask(new PlaceTorchSomewhereTask(
						Collections.singletonList(getPos(-1, 0)),
						Direction.DOWN));
			}
		}
	}

	private final ArrayList<WayPiece> wayTypes = new ArrayList<WayPiece>();

	private WayPiece getSuggestedWayType(int progress) {
		while (wayTypes.size() <= progress) {
			computeNextWayType();
		}
		return wayTypes.get(progress);
	}

	public Direction getForwardDirection() {
		return AIHelper.getDirectionForXZ(dx, dz);
	}

	@Override
	protected boolean runSearch(BlockPos playerPosition) {
		if (!addContinuingTask(playerPosition)) {
			return super.runSearch(playerPosition);
		} else {
			return true;
		}
	}

	private void computeNextWayType() {
		final int offset = wayTypes.size();
		final NormalWayType base = new NormalWayType(offset);
		WayPiece type = base;
		if (base.needsBridge()) {
			type = new BridgeWayType(offset);
		} else if (base.needsTunnel()) {
			type = new TunnelWayType(offset);
		} else {
			type = new FlatlandWayType(offset);
		}

		// This will create a global pattern => resume will preserve the spacing of torches
		type.placeTorch = ((cx * dx + cz * dz) + offset) % 8 == 0;
		wayTypes.add(type);
	}

	@Override
	protected void addTasksForTarget(BlockPos currentPos) {
		final int currentStep = getStepNumber(currentPos.getX(),
				currentPos.getZ());
		if (currentPos.getY() == cy - 1
				&& !getSuggestedWayType(currentStep).isDone()) {
			getSuggestedWayType(currentStep).addConstructionTasksFromInner();
		}
	}

	public boolean addContinuingTask(BlockPos playerPosition) {
		BlockPos floorPosition = playerPosition.down();
		LOGGER.debug("Search way at player=" + playerPosition + ", floor=" + floorPosition + ", on track: "
				+ isOnTrack(playerPosition.getX(), playerPosition.getZ())
				+ ", cy= " + cy + ", standing at right block: "
				+ FLOOR_BLOCKS.isAt(world, floorPosition));
		if (isOnTrack(playerPosition.getX(), playerPosition.getZ())
				&& playerPosition.getY() == cy
				&& FLOOR_BLOCKS.isAt(world, floorPosition)) {
			final int currentStep = getStepNumber(playerPosition.getX(),
					playerPosition.getZ());
			final WayPiece next = getSuggestedWayType(currentStep + 1);
			if (!next.isDone()) {
				next.addConstructionTasksFromPrevious(playerPosition);
				return true;
			} else {
				// Next bridge is done => walk one step on the bridge (we don't want pathfinding to destroy our bridge
				addTask(new WalkTowardsTask(playerPosition, playerPosition.add(getForwardDirection().getDirectionVec())));
				return true;
			}
		}
		return false;
	}

	@Override
	protected float rateDestination(int distance, int x, int y, int z) {
		if (!isOnTrack(x, z)) {
			return -1;
		}
		final int currentStep = getStepNumber(x, z);
		if (y == cy - 1 && !getSuggestedWayType(currentStep).isDone()) {
			return distance;
		} else {
			return -1;
		}
	}

	@Override
	public String toString() {
		return "BuildWayPathfinder [width=" + width + ", wayTypes=" + wayTypes
				+ ", dx=" + dx + ", dz=" + dz + ", cx=" + cx + ", cy=" + cy
				+ ", cz=" + cz + "]";
	}

}
