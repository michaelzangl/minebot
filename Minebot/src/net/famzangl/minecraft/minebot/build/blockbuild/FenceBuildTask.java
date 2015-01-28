package net.famzangl.minecraft.minebot.build.blockbuild;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.BlockItemFilter;
import net.famzangl.minecraft.minebot.ai.BlockWhitelist;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

public class FenceBuildTask extends CubeBuildTask {

	public static final BlockPos[] STANDABLE = new BlockPos[] { new BlockPos(-1, 1, 0),
			new BlockPos(0, 1, -1), new BlockPos(1, 1, 0), new BlockPos(0, 1, 1), };

	public static final BlockWhitelist BLOCKS = new BlockWhitelist(
			Blocks.cobblestone_wall).unionWith(AIHelper.fences);

	public FenceBuildTask(BlockPos forPosition, Block blockToPlace) {
		this(forPosition, new BlockItemFilter(blockToPlace));
	}

	public FenceBuildTask(BlockPos forPosition, BlockItemFilter blockItemFilter) {
		super(forPosition, blockItemFilter);
	}

	@Override
	public BlockPos[] getStandablePlaces() {
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
	public BuildTask withPositionAndRotation(BlockPos add, int rotateSteps,
			MirrorDirection mirror) {
		return new FenceBuildTask(add, blockFilter);
	}
}
