package net.famzangl.minecraft.minebot.ai.task;

import java.util.List;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;

public interface CanPrefaceAndDestroy {

	/**
	 * Return a list that this task faces and destroys that could already be
	 * mined before arriving at the target location.
	 * @param helper TODO
	 * 
	 * @return
	 */
	List<Pos> getPredestroyPositions(AIHelper helper);

}
