package net.famzangl.minecraft.minebot.ai.task;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.ItemFilter;
import net.minecraft.util.MovementInput;

public class UpwardsMoveTask extends JumpingPlaceBlockAtFloorTask {
	public UpwardsMoveTask(int x, int y, int z, ItemFilter filter) {
		super(x, y, z, filter);
	}

	@Override
	public void runTick(AIHelper h) {
		if (!h.isAirBlock(x, y + 1, z)) {
			if (!h.isStandingOn(x, y - 1, z)) {
				System.out.println("Not standing on the right block.");
				h.desync();
			}
			h.faceAndDestroy(x, y + 1, z);
		} else {
			super.runTick(h);
		}
	}

	@Override
	public String toString() {
		return "UpwardsMoveTask [x=" + x + ", y=" + y + ", z=" + z + "]";
	}
}
