package net.famzangl.minecraft.minebot.ai.task;

import net.famzangl.minecraft.minebot.ai.AIHelper;

public class DownwardsMoveTask implements AITask {
	private int x;
	private int y;
	private int z;

	public DownwardsMoveTask(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public boolean isFinished(AIHelper h) {
		return h.isStandingOn(x, y, z);
	}

	@Override
	public void runTick(AIHelper h) {
		if (!h.isAirBlock(x, y + 1, z)) {
			// grass, ...
			h.faceAndDestroy(x, y + 1, z);
		} else if (!h.isAirBlock(x, y, z)) {
			if (!h.isStandingOn(x, y + 1, z)) {
				System.out.println("Not standing on the right block.");
				h.desync();
			}
			h.faceAndDestroy(x, y, z);
		}
	}

	@Override
	public String toString() {
		return "DownwardsMoveTask [x=" + x + ", y=" + y + ", z=" + z + "]";
	}
}