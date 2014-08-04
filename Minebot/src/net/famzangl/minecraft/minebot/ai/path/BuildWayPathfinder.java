package net.famzangl.minecraft.minebot.ai.path;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.famzangl.minecraft.minebot.ai.task.BlockSide;
import net.famzangl.minecraft.minebot.ai.task.DestroyInRangeTask;
import net.famzangl.minecraft.minebot.ai.task.PlaceBlockTask;
import net.famzangl.minecraft.minebot.ai.task.PlaceTorchSomewhereTask;
import net.famzangl.minecraft.minebot.build.WalkTowardsTask;
import net.famzangl.minecraft.minebot.build.blockbuild.BlockBuildTask;
import net.famzangl.minecraft.minebot.build.blockbuild.BuildHalfslabTask;
import net.famzangl.minecraft.minebot.build.blockbuild.CubeBuildTask;
import net.famzangl.minecraft.minebot.build.blockbuild.SlabType;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.util.ForgeDirection;

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

	public BuildWayPathfinder(AIHelper helper, int dx, int dz, int cx, int cy,
			int cz) {
		super(helper, dx, dz, cx, cy, cz);
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

		protected Pos getPos(int u, int dy) {
			return new Pos(cx + dx * stepIndex + dz * u, cy + dy, cz + dz
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
		public void addConstructionTasks(Pos currentPos) {
			Pos first = getPos(0, -1);
			AITask placeTask = new BuildHalfslabTask(first, FLOOR,
					BlockSide.LOWER_HALF).getPlaceBlockTask(currentPos
					.subtract(first));
			helper.addTask(placeTask);

			DestroyInRangeTask clearTask = getClearAreaTask();
			clearTask.blacklist(first);
			helper.addTask(clearTask);

			for (int i = 1; i < width; i++) {
				Pos current = getPos(i, -1);
				helper.addTask(new BuildHalfslabTask(current, FLOOR,
						BlockSide.LOWER_HALF)
						.getPlaceBlockTask(getPos(i - 1, 0).subtract(current)));
			}

			addFarSideBuildTasks();

			helper.addTask(new WalkTowardsTask(getPos(width - 1, 0), getPos(0,
					0)));
			addNearSideBuildTasks();

			done = true;
		}

		protected void addFarSideBuildTasks() {
			helper.addTask(new WalkTowardsTask(getPos(width, 1), getPos(
					width - 1, 0)));
		}

		protected void addNearSideBuildTasks() {
			helper.addTask(new WalkTowardsTask(getPos(-1, 1), getPos(0, 0)));
		}

		protected DestroyInRangeTask getClearAreaTask() {
			return new DestroyInRangeTask(getPos(0, -1), getPos(width - 1, 2));
		}

		public void addConstructionTasksFromInner() {
			helper.addTask(new DestroyInRangeTask(getPos(0, -1), getPos(0, 1)));

			addConstructionTasks(getPos(0, -1));
		}

		public void addConstructionTasksFromPrevoius(Pos current) {
			Pos floorPos = getPos(0, -1);
			Block floor = helper.getBlock(floorPos);
			helper.addTask(new DestroyInRangeTask(floorPos, getPos(0, 1)));
			// helper.addTask(new SneakAndPlaceAtHalfTask(floorPos.x,
			// floorPos.y,
			// floorPos.z, FLOOR, current, floorPos.y + .5,
			// BlockSide.LOWER_HALF));

			addConstructionTasks(current);
		}

		protected final void addNearSide() {
			CubeBuildTask task = new BlockBuildTask(getPos(-1, -1), BRIDGE_SIDE);
			helper.addTask(task.getPlaceBlockTask(getPos(1, 1).subtract(
					getPos(0, 0))));
		}

		protected final void addFarSide() {
			CubeBuildTask task = new BlockBuildTask(getPos(width, -1),
					BRIDGE_SIDE);
			helper.addTask(task.getPlaceBlockTask(getPos(-1, 1).subtract(
					getPos(0, 0))));
		}
	}

	private class NormalWayType extends WayPiece {
		public NormalWayType(int stepIndex) {
			super(stepIndex);
		}

		public boolean needsTunnel() {
			int covered = 0;
			for (int y = 2; y <= 3; y++) {
				for (int u = -1; u <= width + 1; u++) {
					Block block = helper.getBlock(getPos(u, y));
					if (AIHelper.blockIsOneOf(block, AIHelper.normalBlocks)
							|| AIHelper.blockIsOneOf(block,
									AIHelper.fallingBlocks)) {
						covered++;
					}
				}

			}
			return covered >= 1 + width;
		}

		public boolean needsBridge() {
			return (isAirlike(-1, 0) && isAirlike(-1, -1) && isAirlike(-1, -2))
					|| (isAirlike(width, 0) && isAirlike(width, -1) && isAirlike(
							width, -2));
		}

		private boolean isAirlike(int u, int dy) {
			Block block = helper.getBlock(getPos(u, dy));
			return AIHelper.blockIsOneOf(block, Blocks.air)
					|| AIHelper.blockIsOneOf(block, AIHelper.walkableBlocks);
		}
	}

	private class BridgeWayType extends WayPiece {

		public BridgeWayType(int stepIndex) {
			super(stepIndex);
		}

		@Override
		protected void addFarSideBuildTasks() {
			addFarSide();
			super.addFarSideBuildTasks();
			helper.addTask(new PlaceBlockTask(getPos(width, -1),
					ForgeDirection.UP, BRIDGE_WALL));
			if (placeTorch) {
				helper.addTask(new PlaceTorchSomewhereTask(Collections
						.singletonList(getPos(width, 1)), ForgeDirection.DOWN));
			}
		}

		@Override
		protected void addNearSideBuildTasks() {
			addNearSide();
			super.addNearSideBuildTasks();
			helper.addTask(new PlaceBlockTask(getPos(-1, -1),
					ForgeDirection.UP, BRIDGE_WALL));
			if (placeTorch) {
				helper.addTask(new PlaceTorchSomewhereTask(Collections
						.singletonList(getPos(-1, 1)), ForgeDirection.DOWN));
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
				helper.addTask(new PlaceTorchSomewhereTask(Arrays.asList(
						getPos(0, 1), getPos(0, 0), getPos(-1, 1)),
						getForwardDirection().getRotation(ForgeDirection.UP),
						ForgeDirection.DOWN));
			}
		}

		@Override
		protected void addFarSideBuildTasks() {
			super.addFarSideBuildTasks();
			if (placeTorch) {
				helper.addTask(new PlaceTorchSomewhereTask(Arrays.asList(
						getPos(width - 1, 1), getPos(width - 1, 0),
						getPos(width, 1)), getForwardDirection().getRotation(
						ForgeDirection.DOWN), ForgeDirection.DOWN));
			}
		}
	}

	private class FlatlandWayType extends WayPiece {

		public FlatlandWayType(int stepIndex) {
			super(stepIndex);
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
				helper.addTask(new PlaceTorchSomewhereTask(Collections
						.singletonList(getPos(width, 0)), ForgeDirection.DOWN));
			}
		}

		@Override
		protected void addNearSideBuildTasks() {
			if (placeTorch) {
				addNearSide();
			}
			super.addNearSideBuildTasks();
			if (placeTorch) {
				helper.addTask(new PlaceTorchSomewhereTask(Collections
						.singletonList(getPos(-1, 0)), ForgeDirection.DOWN));
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

	public ForgeDirection getForwardDirection() {
		return AIHelper.getDirectionForXZ(dx, dz);
	}

	private void computeNextWayType() {
		int i = wayTypes.size();
		NormalWayType base = new NormalWayType(i);
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
	protected void addTasksForTarget(Pos currentPos) {
		int currentStep = getStepNumber(currentPos.x, currentPos.z);
		if (currentPos.y == cy - 1
				&& !getSuggestedWayType(currentStep).isDone()) {
			getSuggestedWayType(currentStep).addConstructionTasksFromInner();
		}
	}

	public boolean addContinuingTask(Pos playerPosition) {
		System.out.println("Seatch at "
				+ playerPosition
				+ ", on track: "
				+ isOnTrack(playerPosition.x, playerPosition.z)
				+ ", cy= "
				+ (cy - 1)
				+ " right block: "
				+ AIHelper.blockIsOneOf(helper.getBlock(playerPosition),
						FLOOR.slabBlock));
		if (isOnTrack(playerPosition.x, playerPosition.z)
				&& playerPosition.y == cy - 1
				&& AIHelper.blockIsOneOf(helper.getBlock(playerPosition),
						FLOOR.slabBlock)) {
			int currentStep = getStepNumber(playerPosition.x, playerPosition.z);
			WayPiece next = getSuggestedWayType(currentStep + 1);
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
		int currentStep = getStepNumber(x, z);
		if ((y == cy - 1 && !getSuggestedWayType(currentStep).isDone())) {
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
