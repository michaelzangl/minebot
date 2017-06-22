/*******************************************************************************
 * This file is part of Minebot.
 *
 * Minebot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Minebot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Minebot.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package net.famzangl.minecraft.minebot.ai.path;

import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;

public class OrebfuscatedMinePathFinder extends MineBySettingsPathFinder {
	public OrebfuscatedMinePathFinder(EnumFacing preferedDirection,
			int preferedLayer) {
		super(preferedDirection, preferedLayer);
	}

	private BlockPos searchCenter;
	// private static final BlockWhitelist targetBlocks = new BlockWhitelist(
	// Blocks.stone, Blocks.coal_ore, Blocks.diamond_ore, Blocks.iron_ore,
	// Blocks.emerald_ore, Blocks.redstone_ore);
	// See
	// https://github.com/lishid/Orebfuscator/blob/master/src/com/lishid/orebfuscator/OrebfuscatorConfig.java
	private static final BlockSet targetBlocks = new BlockSet(1, 4, 5, 14, 15,
			16, 21, 46, 48, 49, 56, 73, 82, 129, 13, 87, 88, 112, 153);
	private static final BlockSet visibleMakingBlocks = new BlockSet(
			Blocks.GRAVEL, Blocks.DIRT).unionWith(targetBlocks).invert();
	
	@Override
	protected void onPreRunSearch(BlockPos playerPosition) {
		this.searchCenter = playerPosition;
		super.onPreRunSearch(playerPosition);
	}

	@Override
	protected float rateDestination(int distance, int x, int y, int z) {
		if (y == preferedLayer && isGoodForOrebufscator(x, y, z)) {
			int d = ignoredAbs(x - searchCenter.getX(),
					preferedDirection.getFrontOffsetX())
					+ ignoredAbs(z - searchCenter.getZ(),
							preferedDirection.getFrontOffsetZ());
			return distance + maxDistancePoints + 10 + d * 5;
		} else if (/*
					 * searchCenter.distance(new Pos(x, y, z)) < 10 &&
					 */isVisible(x, y, z) || isVisible(x, y + 1, z)) {
			System.out.println("Parent is rating: " + x + ", " + y + ", " + z);
			return super.rateDestination(distance, x, y, z);
		} else {
			System.out.println("Skip (invisible) " + x + ", " + y + ", " + z);
			return -1;
		}
	}

	private int ignoredAbs(int diff, int offsetZ) {
		if ((diff & 0x8000000) == (offsetZ & 0x8000000)) {
			return 0;
		} else {
			return Math.abs(diff);
		}
	}

	private boolean isGoodForOrebufscator(int x, int y, int z) {
		return isInvisibleTarget(x, y, z) && isInvisibleTarget(x, y + 1, z);
	}

	private boolean isInvisibleTarget(int x, int y, int z) {
		return targetBlocks.isAt(world, x, y, z) && !isVisible(x, y, z);
	}

	private boolean isVisible(int x, int y, int z) {
		return visibleMakingBlocks.isAt(world, x, y, z + 1)
				|| visibleMakingBlocks.isAt(world, x, y, z - 1)
				|| visibleMakingBlocks.isAt(world, x + 1, y, z)
				|| visibleMakingBlocks.isAt(world, x - 1, y, z)
				|| visibleMakingBlocks.isAt(world, x, y + 1, z)
				|| visibleMakingBlocks.isAt(world, x, y - 1, z);
	}
}
