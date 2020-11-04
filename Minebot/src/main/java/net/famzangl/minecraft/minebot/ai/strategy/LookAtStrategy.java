package net.famzangl.minecraft.minebot.ai.strategy;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.minecraft.util.math.vector.Vector3d;

/**
 * Look at the given position.
 * @author michael
 *
 */
public class LookAtStrategy extends AIStrategy{

	private Vector3d lookAt;

	public LookAtStrategy(Vector3d lookAt) {
		this.lookAt = lookAt;
	}

	@Override
	protected TickResult onGameTick(AIHelper helper) {
		if (helper.isFacing(lookAt)) {
			return TickResult.NO_MORE_WORK;
		} else {
			helper.face(lookAt);
			return TickResult.TICK_HANDLED;
		}
	}

	@Override
	public String toString() {
		return "LookAtStrategy [lookAt=" + lookAt + "]";
	}
	
	
}
