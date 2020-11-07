package net.famzangl.minecraft.minebot.ai.path.world;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Records how long (in game ticks) the changes to the world will take. Used to determine the timeout.
 */
public class RecordingWorld extends WorldWithDelta {
	private static final Logger LOGGER = LogManager.getLogger(RecordingWorld.class);

	private static final int TIME_TO_PLACE = 5;
	private static final int TIME_TO_FACE = 3;
	private int timeInTicks = 0;
	private final PlayerEntity blockBreaker;

	public RecordingWorld(WorldData currentWorld, PlayerEntity blockBreaker) {
		super(currentWorld);
		this.blockBreaker = blockBreaker;
	}

	@Override
	public void setBlock(BlockPos pos, BlockState block) {
		BlockState currentBlock = getBlockState(pos);
		// Destroy the old one
		if (!BlockSets.AIR.contains(currentBlock)) {
			timeInTicks += getTimeToDestroy(pos, currentBlock);
		}

		// Place the new one
		if (!BlockSets.AIR.contains(block)) {
			LOGGER.debug("Determined time to place block {} at {}: {}", block, pos, TIME_TO_PLACE);
			timeInTicks += TIME_TO_PLACE;
		}
		super.setBlock(pos, block);
	}

	private int getTimeToDestroy(BlockPos blockPos, BlockState blockState) {
		//how much damage to give the block per tick. The block is destroyed on damage >= 1;
		float hardness = blockState.getPlayerRelativeBlockHardness(
				blockBreaker, getBackingWorld(), blockPos
		);
		LOGGER.debug("Determined time to destroy block {} at {}: {}", blockState, blockPos, 1 / hardness);
		return Math.round(1 / hardness) + TIME_TO_FACE;
	}

	@Override
	public void setPlayerPosition(BlockPos playerPosition) {
		int walkingTime = timeToWalk(playerPosition, getPlayerPosition());
		LOGGER.debug("Determined time walk from {} to {}: {}", getPlayerPosition(), playerPosition, walkingTime);
		this.timeInTicks += walkingTime;

		super.setPlayerPosition(playerPosition);
	}

	/**
	 * Time to get from p1 to p2. Just a rough estimate.
	 * @param p1 Start
	 * @param p2 Destination
	 * @return The maximal time in ticks
	 */
	public static int timeToWalk(BlockPos p1, BlockPos p2) {
		if (p1.equals(p2)) {
			// 5 Ticks
			return 5;
		} else {
			double hDist = Math.hypot(p1.getX() - p2.getX(), p1.getZ() - p2.getZ());
			double time = 1 + hDist / 4;
			int horizontalMovementTime = (int) Math.ceil(time * 20);
			int verticalMovementTime = p1.getY() > p2.getY()
					// Falling
					? (p1.getY() - p2.getY()) * 10
					// Need to jump up => this takes some time
					// especially if we need to place blocks and first placement fails
					: (p2.getY() - p1.getY()) * 40;
			return horizontalMovementTime + verticalMovementTime;
		}
	}
	
	public int getTimeInTicks() {
		return timeInTicks;
	}
}
