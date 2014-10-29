package net.famzangl.minecraft.minebot.ai.scanner;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.path.MovePathFinder;

public class BlockRangeFinder extends MovePathFinder {
	protected BlockRangeScanner rangeScanner;
	
	public BlockRangeFinder() {
		allowedGroundForUpwardsBlocks = allowedGroundBlocks;
		footAllowedBlocks = AIHelper.walkableBlocks;
		headAllowedBlocks = AIHelper.headWalkableBlocks;
		footAllowedBlocks = footAllowedBlocks.intersectWith(forbiddenBlocks.invert());
		headAllowedBlocks = headAllowedBlocks.intersectWith(forbiddenBlocks.invert());
	}

	@Override
	protected boolean runSearch(Pos playerPosition) {
		if (rangeScanner == null) {
			rangeScanner = constructScanner(playerPosition);
			rangeScanner.startAsync(helper);
			return false;
		} else if (!rangeScanner.isScaningFinished()) {
			return false;
		} else {
			return super.runSearch(playerPosition);
		}
	}

	protected BlockRangeScanner constructScanner(Pos playerPosition) {
		return new BlockRangeScanner(playerPosition);
	}
}