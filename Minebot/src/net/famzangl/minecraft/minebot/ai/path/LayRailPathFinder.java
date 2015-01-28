package net.famzangl.minecraft.minebot.ai.path;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.BlockItemFilter;
import net.famzangl.minecraft.minebot.ai.task.MineBlockTask;
import net.famzangl.minecraft.minebot.ai.task.move.DownwardsMoveTask;
import net.famzangl.minecraft.minebot.ai.task.move.UpwardsMoveTask;
import net.famzangl.minecraft.minebot.ai.task.place.PlaceBlockAtFloorTask;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

public class LayRailPathFinder extends AlongTrackPathFinder {

	public LayRailPathFinder(int dx, int dz, int cx, int cy, int cz) {
		super(dx, dz, cx, cy, cz, -1);
	}

	@Override
	protected int getNeighbour(int currentNode, int cx, int cy, int cz) {
		final int res = super.getNeighbour(currentNode, cx, cy, cz);
		if (res > 0 && AIHelper.railBlocks.contains(helper.getBlock(cx, cy + 1, cz))) {
			return -1;
		}
		return res;
	}

	@Override
	protected float rateDestination(int distance, int x, int y, int z) {
		if (isRedstoneBlockPosition(x, y, z)
				&& !Block.isEqualTo(helper.getBlock(x, y, z),
						Blocks.redstone_block)) {
			return distance + 2;
		} else if (isOnTrack(x, z) && y == cy
				&& !AIHelper.railBlocks.contains(helper.getBlock(x, y, z))) {
			return distance + 5;
		} else {
			return -1;
		}
	}

	private boolean isRedstoneBlockPosition(int x, int y, int z) {
		return y == cy - 1 && isOnTrack(x, z) && placeAccRail(x, z)
				&& helper.isAirBlock(x, y + 2, z);
	}

	private boolean placeAccRail(int x, int z) {
		return getStepNumber(x, z) % 8 == 0;
	}

	@Override
	protected void addTasksForTarget(BlockPos currentPos) {
		if (isRedstoneBlockPosition(currentPos.getX(), currentPos.getY(), currentPos.getZ())) {
			// For those server lags
			addTask(new UpwardsMoveTask(currentPos.add(0,1,0), new BlockItemFilter(Blocks.redstone_block)));
		} else if (placeAccRail(currentPos.getX(), currentPos.getZ())) {
			if (!Block.isEqualTo(helper.getBlock(currentPos.getX(),
					currentPos.getY() - 1, currentPos.getZ()), Blocks.redstone_block)
					&& helper.hasSafeSides(currentPos.getX(), currentPos.getY() - 1,
							currentPos.getZ())
					&& helper.isSafeGroundBlock(currentPos.getX(), currentPos.getY() - 2,
							currentPos.getZ())) {
				addTask(new DownwardsMoveTask(currentPos.add(0,-1,0)));
				addTask(new UpwardsMoveTask(currentPos,
						new BlockItemFilter(Blocks.redstone_block)));
			}
			placeRail(currentPos, Blocks.golden_rail);
		} else {
			placeRail(currentPos, Blocks.rail);
		}
	}

	private void placeRail(BlockPos currentPos, Block rail) {
		if (!helper.isAirBlock(currentPos.getX(), currentPos.getY(), currentPos.getZ())) {
			addTask(new MineBlockTask(currentPos));
		}
		addTask(new PlaceBlockAtFloorTask(currentPos, new BlockItemFilter(rail)));
	}

	@Override
	public String toString() {
		return "LayRailPathFinder [dx=" + dx + ", dz=" + dz + ", cx=" + cx
				+ ", cy=" + cy + ", cz=" + cz + "]";
	}

}