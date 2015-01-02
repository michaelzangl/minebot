package net.famzangl.minecraft.minebot.ai.path;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.BlockWhitelist;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.util.ForgeDirection;

public class OrebfuscatedMinePathFinder extends MineBySettingsPathFinder {
	public OrebfuscatedMinePathFinder(ForgeDirection preferedDirection,
			int preferedLayer) {
		super(preferedDirection, preferedLayer);
	}

	private Pos searchCenter;
	// private static final BlockWhitelist targetBlocks = new BlockWhitelist(
	// Blocks.stone, Blocks.coal_ore, Blocks.diamond_ore, Blocks.iron_ore,
	// Blocks.emerald_ore, Blocks.redstone_ore);
	// See
	// https://github.com/lishid/Orebfuscator/blob/master/src/com/lishid/orebfuscator/OrebfuscatorConfig.java
	private static final BlockWhitelist targetBlocks = new BlockWhitelist(1, 4,
			5, 14, 15, 16, 21, 46, 48, 49, 56, 73, 82, 129, 13, 87, 88, 112,
			153);
	private static final BlockWhitelist visibleMakingBlocks = new BlockWhitelist(
			Blocks.gravel, Blocks.dirt).unionWith(targetBlocks).invert();

	@Override
	protected boolean runSearch(Pos playerPosition) {
		this.searchCenter = playerPosition;
		return super.runSearch(playerPosition);
	}

	@Override
	protected float rateDestination(int distance, int x, int y, int z) {
		if (y == preferedLayer && isGoodForOrebufscator(x, y, z)) {
			int d = ignoredAbs(x - searchCenter.x, preferedDirection.offsetX) + ignoredAbs(z - searchCenter.z, preferedDirection.offsetZ);
			return distance + maxDistancePoints + 10 + d * 5;
		} else if (/*searchCenter.distance(new Pos(x, y, z)) < 10
				&& */isVisible(x, y, z) || isVisible(x, y + 1, z)) {
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
		return targetBlocks.contains(helper.getBlockId(x, y, z))
				&& !isVisible(x, y, z);
	}

	private boolean isVisible(int x, int y, int z) {
		return visibleMakingBlocks.contains(helper.getBlockId(x, y, z + 1))
				|| visibleMakingBlocks.contains(helper.getBlockId(x, y, z - 1))
				|| visibleMakingBlocks.contains(helper.getBlockId(x + 1, y, z))
				|| visibleMakingBlocks.contains(helper.getBlockId(x - 1, y, z))
				|| visibleMakingBlocks.contains(helper.getBlockId(x, y + 1, z))
				|| visibleMakingBlocks.contains(helper.getBlockId(x, y - 1, z));
	}
}
