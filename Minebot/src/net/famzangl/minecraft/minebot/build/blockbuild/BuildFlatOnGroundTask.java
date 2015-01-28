package net.famzangl.minecraft.minebot.build.blockbuild;

import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.famzangl.minecraft.minebot.ai.task.place.PlaceBlockAtFloorTask;
import net.minecraft.util.BlockPos;

/**
 * Build something that is just standing on the ground.
 * 
 * @author michael
 *
 */
public abstract class BuildFlatOnGroundTask extends BuildTask {

	protected BuildFlatOnGroundTask(BlockPos forPosition) {
		super(forPosition);
	}

	@Override
	public BlockPos[] getStandablePlaces() {
		return new BlockPos[] { forPosition };
	}

	@Override
	public AITask getPlaceBlockTask(BlockPos relativeFromPos) {
		return new PlaceBlockAtFloorTask(forPosition, this.getRequiredItem());
	}
}
