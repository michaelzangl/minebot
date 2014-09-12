package net.famzangl.minecraft.minebot.ai.strategy;

import net.famzangl.minecraft.minebot.ai.AIHelper;

public abstract class TimeStrategy extends AIStrategy {

	private long startTime = -1;

	/**
	 * Gets how many ticks happend since the first time this was called.
	 * 
	 * @return 0 or more.
	 */
	protected long getTimeElapsed(AIHelper helper) {
		long time = helper.getMinecraft().theWorld.getTotalWorldTime();
		if (startTime < 0) {
			startTime = time;
			return 0;
		} else {
			return time - startTime;
		}

	}

}
