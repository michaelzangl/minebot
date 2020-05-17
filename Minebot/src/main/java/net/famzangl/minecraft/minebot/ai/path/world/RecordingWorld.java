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

	public static int timeToWalk(BlockPos p1, BlockPos p2) {
		if (p1.equals(p2)) {
			return 5;
		} else {
			double hDist = Math.hypot(p1.getX() - p2.getX(), p1.getZ() - p2.getZ());
			double time = 1 + hDist / 4;
			return (int) Math.ceil(time * 20);
		}
	}
	
	public int getTimeInTicks() {
		return timeInTicks;
	}
}
