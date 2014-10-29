package net.famzangl.minecraft.minebot.build.blockbuild;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.BlockItemFilter;
import net.famzangl.minecraft.minebot.ai.BlockWhitelist;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

public class FenceBuildTask extends CubeBuildTask {

	public static final Pos[] STANDABLE = new Pos[] { new Pos(-1, 1, 0),
			new Pos(0, 1, -1), new Pos(1, 1, 0), new Pos(0, 1, 1), };

	public static final BlockWhitelist BLOCKS = new BlockWhitelist( Blocks.fence,
			Blocks.cobblestone_wall, Blocks.nether_brick_fence);

	public FenceBuildTask(Pos forPosition, Block blockToPlace) {
		this(forPosition, new BlockItemFilter(blockToPlace));
	}

	public FenceBuildTask(Pos forPosition, BlockItemFilter blockItemFilter) {
		super(forPosition, blockItemFilter);
	}

	@Override
	public Pos[] getStandablePlaces() {
		return STANDABLE;
	}

	@Override
	protected double getBlockHeight() {
		return 1.5;
	}

	@Override
	protected double getMinHeightToBuild() {
		return super.getMinHeightToBuild();
	}

	@Override
	public BuildTask withPositionAndRotation(Pos add, int rotateSteps,
			MirrorDirection mirror) {
		return new FenceBuildTask(add, blockFilter);
	}
}
