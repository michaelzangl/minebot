package net.famzangl.minecraft.minebot.ai.path;

import net.famzangl.minecraft.minebot.ai.ClassItemFilter;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSets;
import net.famzangl.minecraft.minebot.ai.task.place.DestroyBlockTask;
import net.famzangl.minecraft.minebot.ai.task.place.PlaceBlockAtFloorTask;
import net.famzangl.minecraft.minebot.settings.MinebotSettingsRoot;
import net.famzangl.minecraft.minebot.settings.PathfindingSetting;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemReed;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class SugarCanePathFinder extends WalkingPathfinder {
	private static final BlockSet SUGAR_CANE_GROUND = new BlockSet(Blocks.sand, Blocks.grass, Blocks.dirt);
	private static final BlockSet SUGAR_CANE = new BlockSet(Blocks.reeds);

	@Override
	protected float rateDestination(int distance, int x, int y, int z) {
		if (SUGAR_CANE.isAt(world, x, y, z)
				&& SUGAR_CANE.isAt(world, x, y + 1, z)) {
			return super.rateDestination(distance, x, y, z);
		} else if (isSugarCanePlantPlace(x, y, z)) {
			return super.rateDestination(distance, x, y, z) + 2;
		} else {
			return -1;
		}
	}

	private boolean isSugarCanePlantPlace(int x, int y, int z) {
		if (!BlockSets.AIR.isAt(world, x, y, z)
				|| !BlockSets.AIR.isAt(world, x, y + 1, z)
				|| !SUGAR_CANE_GROUND.isAt(world, x, y - 1, z)) {
			// not on sand
			return false;
		}
		for (EnumFacing d : EnumFacing.values()) {
			if (d.getFrontOffsetY() == 0) {
				if (BlockSets.WATER.isAt(world, x + d.getFrontOffsetX(), y - 1,
						z + d.getFrontOffsetZ())) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	protected void addTasksForTarget(BlockPos currentPos) {
		super.addTasksForTarget(currentPos);
		BlockPos top = currentPos.add(0, 1, 0);
		if (SUGAR_CANE.isAt(world, top)) {
			addTask(new DestroyBlockTask(top));
		} else if (BlockSets.AIR.isAt(world, currentPos)) {
			addTask(new PlaceBlockAtFloorTask(currentPos, new ClassItemFilter(ItemReed.class)));
		}
	}
}
