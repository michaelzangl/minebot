package net.famzangl.minecraft.minebot.ai.path;

import java.util.LinkedList;

import net.famzangl.minecraft.minebot.MinebotSettings;
import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.BlockItemFilter;
import net.famzangl.minecraft.minebot.ai.BlockWhitelist;
import net.famzangl.minecraft.minebot.ai.PathFinderField;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.famzangl.minecraft.minebot.ai.task.move.AlignToGridTask;
import net.famzangl.minecraft.minebot.ai.task.move.DownwardsMoveTask;
import net.famzangl.minecraft.minebot.ai.task.move.HorizontalMoveTask;
import net.famzangl.minecraft.minebot.ai.task.move.JumpMoveTask;
import net.famzangl.minecraft.minebot.ai.task.move.UpwardsMoveTask;
import net.famzangl.minecraft.minebot.ai.task.move.WalkTowardsTask;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * A pathfinder that lets you move around a minecraft world.
 * 
 * @author michael
 * 
 */
public class MovePathFinder extends PathFinderField {
	protected BlockWhitelist upwardsBuildBlocks;

	protected AIHelper helper;
	protected MinebotSettings settings;

	protected float torchLightLevel;

	/**
	 * Blocks we should not dig through, e.g. because we cannot handle them
	 * correctly.
	 */
	protected static final BlockWhitelist defaultForbiddenBlocks = new BlockWhitelist(
			Blocks.bedrock, Blocks.cactus, Blocks.obsidian,
			Blocks.piston_extension, Blocks.piston_head);

	/**
	 * This list can be changed. Only blocks of this type are walked to.
	 */
	protected BlockWhitelist allowedGroundBlocks = AIHelper.safeStandableBlocks;
	protected BlockWhitelist allowedGroundForUpwardsBlocks = AIHelper.safeStandableBlocks
			.unionWith(AIHelper.walkableBlocks);

	protected BlockWhitelist footAllowedBlocks = AIHelper.safeDestructableBlocks
			.unionWith(AIHelper.safeCeilingBlocks).unionWith(
					AIHelper.fallingBlocks);
	protected BlockWhitelist headAllowedBlocks = footAllowedBlocks;

	protected BlockWhitelist shortFootBlocks = AIHelper.walkableBlocks;
	protected BlockWhitelist shortHeadBlocks = AIHelper.headWalkableBlocks;

	protected final static BlockWhitelist fastDestructableBlocks = new BlockWhitelist(
			Blocks.dirt, Blocks.gravel, Blocks.sand, Blocks.sandstone);

	private static final BlockWhitelist defaultUpwardsBlocks = new BlockWhitelist(
			Blocks.dirt, Blocks.stone, Blocks.cobblestone, Blocks.sand);

	/**
	 * Current forbidden block settings. Just FYI, never used by this
	 * pathfinder.
	 */
	protected final BlockWhitelist forbiddenBlocks;
	private TaskReceiver receiver;

	private Pos currentTarget;

	public MovePathFinder() {
		super();
		settings = new MinebotSettings();

		upwardsBuildBlocks = settings.getBlocks("upwards_place_block",
				defaultUpwardsBlocks);
		forbiddenBlocks = settings.getBlocks("blacklisted_blocks",
				defaultForbiddenBlocks);

		torchLightLevel = settings.getFloat("place_torches_at", 1.0f, -1, 15);
		footAllowedBlocks = footAllowedBlocks.intersectWith(forbiddenBlocks.invert());
		headAllowedBlocks = headAllowedBlocks.intersectWith(forbiddenBlocks.invert());
	}

	@Override
	protected final boolean searchSomethingAround(int cx, int cy, int cz) {
		throw new UnsupportedOperationException("Direct call not supported.");
	}

	protected void addTask(AITask task) {
		receiver.addTask(task);
	}

	public final boolean searchSomethingAround(Pos playerPosition,
			AIHelper helper, TaskReceiver receiver) {
		currentTarget = null;
		this.helper = helper;
		this.receiver = receiver;
		return runSearch(playerPosition);
	}

	/**
	 * 
	 * @param playerPosition
	 * @return <code>false</code> When pathfinding should be given more time.
	 */
	protected boolean runSearch(Pos playerPosition) {
		return super.searchSomethingAround(playerPosition.x, playerPosition.y,
				playerPosition.z);
	}

	@Override
	protected int getNeighbour(int currentNode, int cx, int cy, int cz) {
		final int res = super.getNeighbour(currentNode, cx, cy, cz);
		if (res > 0 && !isSafeToTravel(currentNode, cx, cy, cz)) {
			return -1;
		}
		return res;
	}

	protected boolean isSafeToTravel(int currentNode, int cx, int cy, int cz) {
		return helper.hasSafeSides(cx, cy + 1, cz)
				&& isAllowedPosition(cx, cy, cz)
				&& helper.hasSafeSides(cx, cy, cz)
				&& checkHeadBlock(currentNode, cx, cy, cz)
				&& checkGroundBlock(currentNode, cx, cy, cz);
	}

	/**
	 * Are we allowed to travel there, only looking at the blocks that we need.
	 * 
	 * @param cx
	 * @param cy
	 * @param cz
	 * @return
	 */
	private boolean isAllowedPosition(int cx, int cy, int cz) {
		return footAllowedBlocks.contains(helper.getBlockId(cx, cy, cz))
				&& headAllowedBlocks
						.contains(helper.getBlockId(cx, cy + 1, cz));
	}

	protected boolean checkGroundBlock(int currentNode, int cx, int cy, int cz) {
		if (getY(currentNode) < cy) {
			return allowedGroundForUpwardsBlocks.contains(helper.getBlockId(cx,
					cy - 1, cz));
		} else {
			return allowedGroundBlocks.contains(helper.getBlockId(cx, cy - 1,
					cz));
		}
	}

	private boolean checkHeadBlock(int currentNode, int cx, int cy, int cz) {
		if (getY(currentNode) > cy) {
			if (getX(currentNode) != cx || getZ(currentNode) != cz) {
				return helper.isSafeHeadBlock(cx, cy + 3, cz)
						&& AIHelper.headWalkableBlocks.contains(helper
								.getBlockId(cx, cy + 2, cz));
			} else if (helper.isFallingBlock(cx, cy + 2, cz)) {
				// moving down, so ignoring sand, gravel.
				return true;
			}
		}
		return helper.isSafeHeadBlock(cx, cy + 2, cz);
	}

	@Override
	protected void noPathFound() {
		super.noPathFound();
	}

	@Override
	protected void foundPath(LinkedList<Pos> path) {
		super.foundPath(path);
		Pos currentPos = path.removeFirst();
		addTask(new AlignToGridTask(currentPos.x, currentPos.y, currentPos.z));
		while (!path.isEmpty()) {
			Pos nextPos = path.removeFirst();
			ForgeDirection moveDirection;
			moveDirection = direction(currentPos, nextPos);
			if (torchLightLevel >= 0 && moveDirection != ForgeDirection.UP) {
				ForgeDirection direction;
				if (moveDirection == ForgeDirection.UP) {
					direction = ForgeDirection.DOWN;
				} else {
					direction = moveDirection;
				}
				addTask(new PlaceTorchIfLightBelowTask(currentPos, direction,
						torchLightLevel));
			}
			int stepsAdded = 0;
			while (path.peekFirst() != null
					&& isAreaClear(currentPos, path.peekFirst())
					&& stepsAdded < 20) {
				nextPos = path.removeFirst();
				stepsAdded++;
			}
			final Pos peeked = path.peekFirst();
			if (stepsAdded > 0) {
				System.out.println("Shortcur from " + currentPos + " to "
						+ nextPos);
				addTask(new WalkTowardsTask(nextPos.x, nextPos.z, currentPos));
			} else if (moveDirection == ForgeDirection.UP && peeked != null
					&& nextPos.subtract(peeked).y == 0) {
				// Combine upwards-sidewards.
				// System.out.println("Next direction is: "
				// + direction(nextPos, peeked));
				addTask(new JumpMoveTask(peeked.x, peeked.y, peeked.z,
						nextPos.x, nextPos.z));
				nextPos = peeked;
				path.removeFirst();
			} else if (nextPos.y > currentPos.y) {
				addTask(new UpwardsMoveTask(nextPos.x, nextPos.y, nextPos.z,
						new BlockItemFilter(upwardsBuildBlocks)));
			} else if (nextPos.y < currentPos.y && nextPos.x == currentPos.x
					&& nextPos.z == currentPos.z) {
				addTask(new DownwardsMoveTask(nextPos.x, nextPos.y, nextPos.z));
			} else {
				addTask(new HorizontalMoveTask(nextPos.x, nextPos.y, nextPos.z));
			}
			currentPos = nextPos;
		}
		currentTarget = currentPos;
		addTasksForTarget(currentPos);
	}

	/**
	 * Could we take a shortcut without any objects in the way.
	 * 
	 * @param pos1
	 * @param pos2
	 * @return
	 */
	private boolean isAreaClear(Pos pos1, Pos pos2) {
		if (pos1.y != pos2.y) {
			return false;
		}
		Pos min = Pos.minPos(pos1, pos2);
		Pos max = Pos.maxPos(pos1, pos2);
		int y = pos1.y;
		for (int x = min.x; x <= max.x; x++) {
			for (int z = min.z; z <= max.z; z++) {
				if (!helper.isSafeGroundBlock(x, y - 1, z)
						|| !helper.isSafeHeadBlock(x, y + 2, z)
						|| !AIHelper.walkableBlocks.contains(helper.getBlockId(
								x, y, z))
						|| !AIHelper.headWalkableBlocks.contains(helper
								.getBlockId(x, y + 1, z))) {
					return false;
				}
			}
		}
		return true;
	}

	private ForgeDirection direction(Pos currentPos, final Pos nextPos) {
		Pos delta = nextPos.subtract(currentPos);
		if (delta.y != 0 && (delta.x != 0 || delta.z != 0)) {
			delta = new Pos(delta.x, 0, delta.z);
		}
		return AIHelper.getDirectionFor(delta);
	}

	protected void addTasksForTarget(Pos currentPos) {
	}

	@Override
	protected int distanceFor(int from, int to) {
		int distance = 0;
		// if (getY(from) != getY(to)) {
		// up or down
		distance += 1;
		// } else {
		// distance += 3;
		// }
		if (getY(from) >= getY(to)) {
			distance += materialDistance(getX(to), getY(to), getZ(to), true);
		}
		if (getY(from) <= getY(to)) {
			distance += materialDistance(getX(to), getY(to) + 1, getZ(to),
					false);
		}
		return distance;
	}

	protected int materialDistance(int x, int y, int z, boolean asFloor) {
		final int block = helper.getBlockId(x, y, z);
		if (asFloor && shortFootBlocks.contains(block) || !asFloor
				&& shortHeadBlocks.contains(block)) {
			return 0;
		} else if (fastDestructableBlocks.contains(block)) {
			// fast breaking gives bonus.
			return 1;
		} else {
			return 2;
		}
	}

	public Pos getCurrentTarget() {
		return currentTarget;
	}
	
}
