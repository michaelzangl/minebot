package net.famzangl.minecraft.minebot.ai.task;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.ItemFilter;
import net.minecraft.util.MovementInput;

/**
 * For blocks we collide with.
 * 
 * @author michael
 * 
 */
public class JumpingPlaceBlockAtFloorTask extends PlaceBlockAtFloorTask {
	public JumpingPlaceBlockAtFloorTask(int x, int y, int z, ItemFilter filter) {
		super(x, y, z, filter);
	}

	@Override
	protected int getPlaceAtY() {
		return y - 1;
	}

	@Override
	public boolean isFinished(AIHelper h) {
		return h.isStandingOn(x, y, z) && super.isFinished(h);
	}

	@Override
	protected void tryPlaceBlock(AIHelper h) {
		super.tryPlaceBlock(h);
		MovementInput i = new MovementInput();
		i.jump = true;
		h.overrideMovement(i);
	}

	@Override
	public String toString() {
		return "JumpingPlaceBlockAtFloorTask [x=" + x + ", y=" + y + ", z=" + z
				+ "]";
	}
}
