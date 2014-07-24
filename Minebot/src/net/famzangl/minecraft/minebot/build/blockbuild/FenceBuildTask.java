package net.famzangl.minecraft.minebot.build.blockbuild;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.BlockItemFilter;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

public class FenceBuildTask extends CubeBuildTask {

	public static final Pos[] STANDABLE = new Pos[] { new Pos(-1, 1, 0),
			new Pos(0, 1, -1), new Pos(1, 1, 0), new Pos(0, 1, 1), };

	public static final Block[] BLOCKS = new Block[] { Blocks.fence,
			Blocks.cobblestone_wall, Blocks.nether_brick_fence };

	public FenceBuildTask(Pos forPosition, Block blockToPlace) {
		super(forPosition, new BlockItemFilter(blockToPlace));
	}

	@Override
	public Pos[] getStandablePlaces() {
		return STANDABLE;
	}

	@Override
	protected double getBlockHeight() {
		return 1.5;
	}

}
