package net.famzangl.minecraft.minebot.ai.task.place;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.ItemFilter;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovementInput;

/**
 * For blocks we collide with.
 * 
 * @author michael
 * 
 */
public class JumpingPlaceBlockAtFloorTask extends PlaceBlockAtFloorTask {
	public JumpingPlaceBlockAtFloorTask(BlockPos pos, ItemFilter filter) {
		super(pos, filter);
	}

	@Override
	protected int getRelativePlaceAtY() {
		return -1;
	}

	@Override
	public boolean isFinished(AIHelper h) {
		return h.isStandingOn(pos) && super.isFinished(h);
	}

	@Override
	protected void tryPlaceBlock(AIHelper h) {
		super.tryPlaceBlock(h);
		final MovementInput i = new MovementInput();
		i.jump = true;
		h.overrideMovement(i);
	}

	@Override
	public String toString() {
		return "JumpingPlaceBlockAtFloorTask [pos=" + pos + "]";
	}

}
