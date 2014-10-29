package net.famzangl.minecraft.minebot.build.blockbuild;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.famzangl.minecraft.minebot.ai.task.place.PlaceBlockAtFloorTask;

/**
 * Build something that is just standing on the ground.
 * 
 * @author michael
 *
 */
public abstract class BuildFlatOnGroundTask extends BuildTask {

	protected BuildFlatOnGroundTask(Pos forPosition) {
		super(forPosition);
	}

	@Override
	public Pos[] getStandablePlaces() {
		return new Pos[] { forPosition };
	}

	@Override
	public AITask getPlaceBlockTask(Pos relativeFromPos) {
		return new PlaceBlockAtFloorTask(forPosition.x, forPosition.y,
				forPosition.z, this.getRequiredItem());
	}
}
