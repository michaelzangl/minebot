package net.famzangl.minecraft.minebot.build.blockbuild;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.BlockItemFilter;
import net.famzangl.minecraft.minebot.build.LogItemFilter;
import net.famzangl.minecraft.minebot.build.WoodType;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.util.ForgeDirection;

public class LogBuildTask extends CubeBuildTask {

	public static final Block[] BLOCKS = new Block[] { Blocks.log, Blocks.log2 };
	public static final Pos[] UP_DOWN_POS = new Pos[] { new Pos(0, 0, 0) };
	public static final Pos[] NORTH_SOUTH_POS = new Pos[] { new Pos(0, 1, 1),
			new Pos(0, 1, -1) };
	public static final Pos[] EAST_WEST_POS = new Pos[] { new Pos(1, 1, 0),
			new Pos(-1, 1, 0) };
	private final ForgeDirection dir;

	public LogBuildTask(Pos forPosition, WoodType logType,
			ForgeDirection direction) {
		this(forPosition, new LogItemFilter(logType), direction);
	}

	private LogBuildTask(Pos forPosition, BlockItemFilter logItemFilter,
			ForgeDirection direction) {
		super(forPosition, logItemFilter);
		dir = direction;
	}

	@Override
	public Pos[] getStandablePlaces() {
		switch (dir) {
		case EAST:
		case WEST:
			return EAST_WEST_POS;
		case SOUTH:
		case NORTH:
			return NORTH_SOUTH_POS;
		default:
			return UP_DOWN_POS;
		}
	}

	@Override
	public BuildTask withPositionAndRotation(Pos add, int rotateSteps,
			MirrorDirection mirror) {
		ForgeDirection newDir = dir;
		for (int i = 0; i < rotateSteps; i++) {
			newDir = newDir.getRotation(ForgeDirection.UP);
		}

		return new LogBuildTask(add, blockFilter, newDir);
	}
}
