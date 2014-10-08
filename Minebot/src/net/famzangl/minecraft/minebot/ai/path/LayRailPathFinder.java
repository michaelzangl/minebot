package net.famzangl.minecraft.minebot.ai.path;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.BlockItemFilter;
import net.famzangl.minecraft.minebot.ai.task.MineBlockTask;
import net.famzangl.minecraft.minebot.ai.task.move.DownwardsMoveTask;
import net.famzangl.minecraft.minebot.ai.task.move.UpwardsMoveTask;
import net.famzangl.minecraft.minebot.ai.task.place.PlaceBlockAtFloorTask;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

public class LayRailPathFinder extends AlongTrackPathFinder {

	public LayRailPathFinder(int dx, int dz, int cx, int cy, int cz) {
		super(dx, dz, cx, cy, cz, -1);
	}

	@Override
	protected int getNeighbour(int currentNode, int cx, int cy, int cz) {
		final int res = super.getNeighbour(currentNode, cx, cy, cz);
		if (res > 0 && helper.isRailBlock(helper.getBlock(cx, cy + 1, cz))) {
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
				&& !helper.isRailBlock(helper.getBlock(x, y, z))) {
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
	protected void addTasksForTarget(Pos currentPos) {
		if (isRedstoneBlockPosition(currentPos.x, currentPos.y, currentPos.z)) {
			// For those server lags
			addTask(new UpwardsMoveTask(currentPos.x, currentPos.y + 1,
					currentPos.z, new BlockItemFilter(Blocks.redstone_block)));
		} else if (placeAccRail(currentPos.x, currentPos.z)) {
			if (!Block.isEqualTo(helper.getBlock(currentPos.x,
					currentPos.y - 1, currentPos.z), Blocks.redstone_block)
					&& helper.hasSafeSides(currentPos.x, currentPos.y - 1,
							currentPos.z)
					&& helper.isSafeGroundBlock(currentPos.x, currentPos.y - 2,
							currentPos.z)) {
				addTask(new DownwardsMoveTask(currentPos.x, currentPos.y - 1,
						currentPos.z));
				addTask(new UpwardsMoveTask(currentPos.x, currentPos.y,
						currentPos.z,
						new BlockItemFilter(Blocks.redstone_block)));
			}
			placeRail(currentPos, Blocks.golden_rail);
		} else {
			placeRail(currentPos, Blocks.rail);
		}
	}

	private void placeRail(Pos currentPos, Block rail) {
		if (!helper.isAirBlock(currentPos.x, currentPos.y, currentPos.z)) {
			addTask(new MineBlockTask(currentPos.x, currentPos.y, currentPos.z));
		}
		addTask(new PlaceBlockAtFloorTask(currentPos.x, currentPos.y,
				currentPos.z, new BlockItemFilter(rail)));
	}

	@Override
	public String toString() {
		return "LayRailPathFinder [dx=" + dx + ", dz=" + dz + ", cx=" + cx
				+ ", cy=" + cy + ", cz=" + cz + "]";
	}

}