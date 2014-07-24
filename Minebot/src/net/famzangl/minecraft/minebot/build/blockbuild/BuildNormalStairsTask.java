package net.famzangl.minecraft.minebot.build.blockbuild;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.BlockItemFilter;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.famzangl.minecraft.minebot.ai.task.BlockSide;
import net.famzangl.minecraft.minebot.ai.task.place.JumpingPlaceBlockAtSideTask;
import net.famzangl.minecraft.minebot.ai.task.place.SneakAndPlaceAtSideTask;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.util.ForgeDirection;

public class BuildNormalStairsTask extends CubeBuildTask {

	public static final Block[] BLOCKS = new Block[] { Blocks.acacia_stairs,
			Blocks.birch_stairs, Blocks.brick_stairs, Blocks.dark_oak_stairs,
			Blocks.jungle_stairs, Blocks.nether_brick_stairs,
			Blocks.oak_stairs, Blocks.sandstone_stairs, Blocks.spruce_stairs,
			Blocks.stone_brick_stairs, Blocks.stone_stairs,
			Blocks.quartz_stairs };
	private final ForgeDirection upwardsDirection;
	private final boolean inverted;
	
	public static enum Half {
		UPPER,
		LOWER
	}

	public BuildNormalStairsTask(Pos forPosition, Block stairs,
			ForgeDirection upwardsDirection, Half half) {
		super(forPosition, new BlockItemFilter(stairs));
		this.upwardsDirection = upwardsDirection;
		this.inverted = half == Half.UPPER;
		if (upwardsDirection != ForgeDirection.EAST
				&& upwardsDirection != ForgeDirection.WEST
				&& upwardsDirection != ForgeDirection.NORTH
				&& upwardsDirection != ForgeDirection.SOUTH) {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public AITask getPlaceBlockTask(Pos relativeFromPos) {
		final BlockSide side = inverted ? BlockSide.UPPER_HALF
				: BlockSide.LOWER_HALF;
		if (!isStandablePlace(relativeFromPos)) {
			return null;
		} else if (relativeFromPos.equals(FROM_GROUND)) {
			return new JumpingPlaceBlockAtSideTask(forPosition.x,
					forPosition.y + 1, forPosition.z, blockFilter,
					upwardsDirection.getOpposite(), side);
		} else {
			return new SneakAndPlaceAtSideTask(forPosition.x,
					forPosition.y + 1, forPosition.z, blockFilter,
					relativeFromPos, getMinHeightToBuild(),
					upwardsDirection.getOpposite(), side);
		}
	}

	@Override
	public String toString() {
		return "BuildNormalStairsTask [upwardsDirection=" + upwardsDirection
				+ ", inverted=" + inverted + ", blockFilter=" + blockFilter
				+ ", forPosition=" + forPosition + "]";
	}

}
