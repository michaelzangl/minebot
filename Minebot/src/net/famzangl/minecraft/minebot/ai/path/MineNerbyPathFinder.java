package net.famzangl.minecraft.minebot.ai.path;

import java.util.BitSet;

import net.famzangl.minecraft.minebot.ai.path.world.BlockSets;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;

/**
 * Mine some visible ores that are close to us.
 * 
 * @author michael
 *
 */
public class MineNerbyPathFinder extends MineBySettingsPathFinder {

	private static final int RADIUS_HORIZONTAL = 10;
	private static final int RADIUS_VERTICAL = 6;
	private static final int ACTIVATE_RADIUS_HORIZONTAL = 30;
	private static final int ACTIVATE_RADIUS_VERTICAL = 15;
	private static final int WIDTH = RADIUS_HORIZONTAL * 2 + 1;

	private int playerX, playerY, playerZ;

	private final BitSet possiblePositions = new BitSet();
	private final BitSet alreadyScanned = new BitSet();
	private BlockPos activationPosition;

	public MineNerbyPathFinder(EnumFacing preferedDirection) {
		super(preferedDirection, 0);
	}

	@Override
	protected void onPreRunSearch(BlockPos playerPosition) {
		playerX = playerPosition.getX();
		playerY = playerPosition.getY();
		playerZ = playerPosition.getZ();
		possiblePositions.clear();
		alreadyScanned.clear();

		findPossiblePositionsAround(playerPosition.add(0, 1, 0));
		System.out.println("Scanned " + alreadyScanned.cardinality()
				+ " blocks, found " + possiblePositions.cardinality()
				+ " ores.");
		super.onPreRunSearch(playerPosition);
	}

	private void findPossiblePositionsAround(BlockPos add) {
		if (!isNearbyArea(add.getX(), add.getY(), add.getZ())
				|| alreadyScanned.get(getIndex(add))) {
			return;
		}
		alreadyScanned.set(getIndex(add));

		if (super.isOreBlock(add.getX(), add.getY(), add.getZ())) {
			possiblePositions.set(getIndex(add));
		}

		// TODO: Use some sort of visible block list.
		// We need to use the current server state now.
		if (BlockSets.FEET_CAN_WALK_THROUGH.isAt(world.getCurrentState(), add)) {
			for (EnumFacing d : EnumFacing.values()) {
				findPossiblePositionsAround(add.offset(d));
			}
		}
	}

	@Override
	protected int materialDistance(int x, int y, int z, boolean onFloor) {
		if (onFloor && shortFootBlocks.isAt(world, x, y, z) || !onFloor
				&& shortHeadBlocks.isAt(world, x, y, z)) {
			return 0;
		} else {
			return 1;
		}
	}

	@Override
	protected boolean isSafeToTravel(int currentNode, int cx, int cy, int cz) {
		return super.isSafeToTravel(currentNode, cx, cy, cz)
				&& isNearbyArea(cx, cy, cz);
	}

	private boolean isNearbyArea(int cx, int cy, int cz) {
		return Math.abs(playerX - cx) <= RADIUS_HORIZONTAL
				&& Math.abs(playerY - cy) <= RADIUS_VERTICAL
				&& Math.abs(playerZ - cz) <= RADIUS_HORIZONTAL
				&& (activationPosition == null || (Math.abs(activationPosition
						.getX() - cx) <= ACTIVATE_RADIUS_HORIZONTAL
						&& Math.abs(activationPosition.getY() - cy) <= ACTIVATE_RADIUS_VERTICAL && Math
						.abs(activationPosition.getZ() - cz) <= ACTIVATE_RADIUS_HORIZONTAL
				));
	}

	private final int getIndex(BlockPos p) {
		return getIndex(p.getX(), p.getY(), p.getZ());
	}

	private final int getIndex(int x, int y, int z) {
		assert isNearbyArea(x, y, z);

		return (y - playerY + RADIUS_VERTICAL) * WIDTH * WIDTH
				+ (x - playerX + RADIUS_HORIZONTAL) * WIDTH
				+ (z - playerZ + RADIUS_HORIZONTAL);
	}

	@Override
	protected boolean isOreBlock(int x, int y, int z) {
		return possiblePositions.get(getIndex(x, y, z));
	}

	@Override
	protected float rateDestination(int distance, int x, int y, int z) {
		if (possiblePositions.get(getIndex(x, y, z))
				|| possiblePositions.get(getIndex(x, y + 1, z))) {
			// TODO: Do not rate hidden blocks.
			return super.rateDestination(distance, x, y, z);
		} else {
			return -1;
		}
	}

	public void setActivationPoint(BlockPos activationPosition) {
		this.activationPosition = activationPosition;
	}
}
