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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.BlockItemFilter;
import net.famzangl.minecraft.minebot.ai.command.BlockWithDontcare;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSets;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.famzangl.minecraft.minebot.ai.task.DestroyInRangeTask;
import net.famzangl.minecraft.minebot.ai.task.PlaceBlockTask;
import net.famzangl.minecraft.minebot.ai.task.PlaceTorchSomewhereTask;
import net.famzangl.minecraft.minebot.ai.task.inventory.GetOnHotBarTask;
import net.famzangl.minecraft.minebot.build.WalkTowardsTask;
import net.famzangl.minecraft.minebot.build.block.SlabFilter;
import net.famzangl.minecraft.minebot.build.block.SlabType;
import net.famzangl.minecraft.minebot.build.blockbuild.AbstractBuildTask;
import net.famzangl.minecraft.minebot.build.blockbuild.BlockBuildTask;
import net.famzangl.minecraft.minebot.build.blockbuild.SlabBuildTask;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

/**
 * Build a way. it is covered with cobblestone slabs. Bridge profile:
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
 * The lower left CSS is always the root way.
 * 
 * 
 * @author michael
 * 
 */
public class BuildWayPathfinder extends AlongTrackPathFinder {

	private static final SlabType FLOOR = SlabType.COBBLESTONE;

	private static final Block BRIDGE_SIDE = Blocks.cobblestone;
	private static final Block BRIDGE_WALL = Blocks.cobblestone_wall;
	private final int width = 2;

	private BuildWayPathfinder(int dx, int dz, int cx, int cy, int cz) {
		super(dx, dz, cx, cy, cz, -1);
	}

	public BuildWayPathfinder(EnumFacing dir, BlockPos pos) {
		this(dir.getFrontOffsetX(), dir.getFrontOffsetZ(), pos.getX(), pos
				.getY() + 1, pos.getZ());
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
		 * @param currentPos
		 */
		public void addConstructionTasks(BlockPos currentPos) {
			addTask(new GetOnHotBarTask(new SlabFilter(FLOOR)));
			if (placeTorch) {
				addTask(new GetOnHotBarTask(new BlockItemFilter(Blocks.torch)));
			}

			final BlockPos first = getPos(0, -1);
			final AITask placeTask = new SlabBuildTask(first, FLOOR.getBlock()).getPlaceBlockTask(currentPos
					.subtract(first));
			addTask(placeTask);

			final DestroyInRangeTask clearTask = getClearAreaTask();
			//FIXME: clearTask.blacklist(first);
			addTask(clearTask);

			for (int i = 1; i < width; i++) {
				final BlockPos current = getPos(i, -1);
				addTask(new SlabBuildTask(current, FLOOR.getBlock())
						.getPlaceBlockTask(getPos(i - 1, 0).subtract(current)));
			}

			addFarSideBuildTasks();

			addTask(new WalkTowardsTask(getPos(width - 1, 0), getPos(0, 0)));
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
			return new DestroyInRangeTask(getPos(0, -1), getPos(width - 1, 2));
		}

		public void addConstructionTasksFromInner() {
			addTask(new DestroyInRangeTask(getPos(0, -1), getPos(0, 1)));

			addConstructionTasks(getPos(0, -1));
		}

		public void addConstructionTasksFromPrevoius(BlockPos current) {
			final BlockPos floorPos = getPos(0, -1);
			final Block floor = helper.getBlock(floorPos);
			addTask(new DestroyInRangeTask(floorPos, getPos(0, 1)));
			// helper.addTask(new SneakAndPlaceAtHalfTask(floorPos.x,
			// floorPos.y,
			// floorPos.z, FLOOR, current, floorPos.y + .5,
			// BlockSide.LOWER_HALF));

			addConstructionTasks(current);
		}

		protected final void addNearSide() {
			final AbstractBuildTask task = new BlockBuildTask(getPos(-1, -1),
					new BlockWithDontcare(BRIDGE_SIDE));
			addTask(task.getPlaceBlockTask(getPos(1, 1).subtract(getPos(0, 0))));
		}

		protected final void addFarSide() {
			final AbstractBuildTask task = new BlockBuildTask(getPos(width, -1),
					new BlockWithDontcare(BRIDGE_SIDE));
			addTask(task
					.getPlaceBlockTask(getPos(-1, 1).subtract(getPos(0, 0))));
		}
	}

	private class NormalWayType extends WayPiece {
		private final BlockSet airLike = BlockSets.AIR
				.unionWith(BlockSets.FEET_CAN_WALK_THROUGH);;
		private final BlockSet COVERED = BlockSets.SIMPLE_CUBE
				.unionWith(BlockSets.FALLING);

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
			final Block block = helper.getBlock(getPos(u, dy));
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
			addTask(new PlaceBlockTask(getPos(width, -1), EnumFacing.UP,
					BRIDGE_WALL));
			if (placeTorch) {
				addTask(new PlaceTorchSomewhereTask(
						Collections.singletonList(getPos(width, 1)),
						EnumFacing.DOWN));
			}
		}

		@Override
		protected void addNearSideBuildTasks() {
			addNearSide();
			super.addNearSideBuildTasks();
			addTask(new PlaceBlockTask(getPos(-1, -1), EnumFacing.UP,
					BRIDGE_WALL));
			if (placeTorch) {
				addTask(new PlaceTorchSomewhereTask(
						Collections.singletonList(getPos(-1, 1)),
						EnumFacing.DOWN));
			}
		}

		@Override
		protected DestroyInRangeTask getClearAreaTask() {
			return new DestroyInRangeTask(getPos(-1, -1), getPos(width, 2));
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
						.rotateY(), EnumFacing.DOWN));
			}
		}

		@Override
		protected void addFarSideBuildTasks() {
			super.addFarSideBuildTasks();
			if (placeTorch) {
				addTask(new PlaceTorchSomewhereTask(Arrays.asList(
						getPos(width - 1, 1), getPos(width - 1, 0),
						getPos(width, 1)), getForwardDirection().rotateYCCW(),
						EnumFacing.DOWN));
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
				return new DestroyInRangeTask(getPos(-1, -1), getPos(width, 2));
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
						EnumFacing.DOWN));
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
						EnumFacing.DOWN));
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

	public EnumFacing getForwardDirection() {
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
		final int i = wayTypes.size();
		final NormalWayType base = new NormalWayType(i);
		WayPiece type = base;
		if (base.needsBridge()) {
			type = new BridgeWayType(i);
		} else if (base.needsTunnel()) {
			type = new TunnelWayType(i);
		} else {
			type = new FlatlandWayType(i);
		}

		type.placeTorch = i % 8 == 0;
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
		System.out.println("Seatch at " + playerPosition + ", on track: "
				+ isOnTrack(playerPosition.getX(), playerPosition.getZ())
				+ ", cy= " + (cy - 1) + " right block: "
				+ new BlockSet(FLOOR.slabBlock).isAt(world, playerPosition));
		if (isOnTrack(playerPosition.getX(), playerPosition.getZ())
				&& playerPosition.getY() == cy - 1
				&& new BlockSet(FLOOR.slabBlock).isAt(world, playerPosition)) {
			final int currentStep = getStepNumber(playerPosition.getX(),
					playerPosition.getZ());
			final WayPiece next = getSuggestedWayType(currentStep + 1);
			if (!next.isDone()) {
				next.addConstructionTasksFromPrevoius(playerPosition.add(0, 1,
						0));
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
