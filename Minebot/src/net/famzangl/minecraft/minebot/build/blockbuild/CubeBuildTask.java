package net.famzangl.minecraft.minebot.build.blockbuild;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.BlockItemFilter;
import net.famzangl.minecraft.minebot.ai.ItemFilter;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.famzangl.minecraft.minebot.ai.task.move.UpwardsMoveTask;
import net.famzangl.minecraft.minebot.ai.task.place.SneakAndPlaceTask;

public abstract class CubeBuildTask extends BuildTask {

	protected final BlockItemFilter blockFilter;

	protected CubeBuildTask(Pos forPosition, BlockItemFilter blockFilter) {
		super(forPosition);
		this.blockFilter = blockFilter;
	}

	protected static final Pos FROM_GROUND = new Pos(0, 0, 0);
	public static final Pos[] STANDABLE = new Pos[] { new Pos(-1, 1, 0),
			new Pos(0, 1, -1), new Pos(1, 1, 0), new Pos(0, 1, 1), FROM_GROUND, };

	@Override
	public AITask getPlaceBlockTask(Pos relativeFromPos) {
		if (!isStandablePlace(relativeFromPos)) {
			return null;
		} else if (relativeFromPos.equals(FROM_GROUND)) {
			return new UpwardsMoveTask(forPosition.x, forPosition.y + 1,
					forPosition.z, blockFilter);
		} else {
			return new SneakAndPlaceTask(forPosition.x, forPosition.y + 1,
					forPosition.z, blockFilter, relativeFromPos,
					getMinHeightToBuild());
		}
	}

	protected double getMinHeightToBuild() {
		return forPosition.y + getBlockHeight();
	}

	protected double getBlockHeight() {
		return 1;
	}

	@Override
	public Pos[] getStandablePlaces() {
		return STANDABLE;
	}

	@Override
	public boolean couldBuildFrom(AIHelper helper, int x, int y, int z) {
		if (!super.couldBuildFrom(helper, x, y, z)) {
			return false;
		} else {
			return !helper.isAirBlock(x, y - 1, z);
		}
	}

	@Override
	public ItemFilter getRequiredItem() {
		return blockFilter;
	}
}
