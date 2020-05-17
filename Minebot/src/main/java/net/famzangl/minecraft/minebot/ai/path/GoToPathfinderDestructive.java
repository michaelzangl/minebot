package net.famzangl.minecraft.minebot.ai.path;

import net.famzangl.minecraft.minebot.ai.command.AIChatController;
import net.famzangl.minecraft.minebot.ai.path.world.WorldData;
import net.famzangl.minecraft.minebot.ai.utils.BlockArea;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Same as the {@link GoToPathfinder}, digging instead of walking
 */
public class GoToPathfinderDestructive extends MovePathFinder {
    private static final Logger LOGGER = LogManager.getLogger(GoToPathfinder.class);

    private final BlockPos destination;
    private BlockArea<WorldData> targetArea;

    public GoToPathfinderDestructive(BlockPos destination) {
        this.destination = destination;
    }

    @Override
    protected boolean runSearch(BlockPos playerPosition) {
        if (playerPosition.equals(destination)) {
            return true;
        }

        targetArea = GoToPathfinder.computeTargetArea(playerPosition, destination);
        LOGGER.debug("Destructive pathfind target area is: {}", targetArea);
        return super.runSearch(playerPosition);
    }

    @Override
    protected float rateDestination(int distance, int x, int y, int z) {
        return targetArea.contains(world, x, y, z) ? distance : -1;
    }

    @Override
    protected void noPathFound() {
        if (statsVisited < 50) {
            AIChatController.addChatLine("Cannot reach destination. Cannot go far from start position.");
        }
    }

    @Override
    public String toString() {
        return "GoToPathfinderDestructive{" +
                "destination=" + destination +
                ", targetArea=" + targetArea +
                '}';
    }
}
