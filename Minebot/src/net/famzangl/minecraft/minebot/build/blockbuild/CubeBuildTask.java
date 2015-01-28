package net.famzangl.minecraft.minebot.build.blockbuild;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.BlockItemFilter;
import net.famzangl.minecraft.minebot.ai.ItemFilter;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.famzangl.minecraft.minebot.ai.task.move.UpwardsMoveTask;
import net.famzangl.minecraft.minebot.ai.task.place.SneakAndPlaceTask;
import net.minecraft.util.BlockPos;

public abstract class CubeBuildTask extends BuildTask {

	protected final BlockItemFilter blockFilter;

	protected CubeBuildTask(BlockPos forPosition, BlockItemFilter blockFilter) {
		super(forPosition);
		this.blockFilter = blockFilter;
	}

	protected static final BlockPos FROM_GROUND = Pos.ZERO;
	public static final BlockPos[] STANDABLE = new BlockPos[] {
			new BlockPos(-1, 1, 0), new BlockPos(0, 1, -1),
			new BlockPos(1, 1, 0), new BlockPos(0, 1, 1), FROM_GROUND, };

	@Override
	public AITask getPlaceBlockTask(BlockPos relativeFromPos) {
		if (!isStandablePlace(relativeFromPos)) {
			return null;
		} else if (relativeFromPos.equals(FROM_GROUND)) {
			return new UpwardsMoveTask(forPosition.add(0, 1, 0), blockFilter);
		} else {
			return new SneakAndPlaceTask(forPosition.add(0, 1, 0), blockFilter,
					relativeFromPos, getMinHeightToBuild());
		}
	}

	protected double getMinHeightToBuild() {
		return forPosition.getY() + getBlockHeight();
	}

	protected double getBlockHeight() {
		return 1;
	}

	@Override
	public BlockPos[] getStandablePlaces() {
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
