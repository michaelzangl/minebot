package net.famzangl.minecraft.minebot.ai.task;

import java.util.List;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.minecraft.util.BlockPos;

/**
 * An interface that is implemented by all {@link AITask}s that do destroy
 * blocks. That way, the bot can already start to destroy them while walking
 * there.
 * 
 * @author michael
 *
 */
public interface CanPrefaceAndDestroy {

	/**
	 * Return a list that this task faces and destroys that could already be
	 * mined before arriving at the target location.
	 * 
	 * @param helper
	 *            The AI helper.
	 * @return A list of block positions, preferably ordered the way the task
	 *         destroys them.
	 */
	List<BlockPos> getPredestroyPositions(AIHelper helper);

}
