package net.famzangl.minecraft.minebot.ai.path;

import net.minecraft.block.Block;
import net.minecraft.util.EnumFacing;

public class MineSinglePathFinder extends MinePathfinder {

	private final Block block;

	public MineSinglePathFinder(Block block, EnumFacing preferedDirection,
			int preferedLayer) {
		super(preferedDirection, preferedLayer);
		this.block = block;
	}

	@Override
	protected ISettingsProvider getFactorProvider() {
		return new ISettingsProvider() {
			@Override
			public float getFloat(Block block2) {
				return block == block2 ? 1 : 0;
			}
		};
	}

	@Override
	protected ISettingsProvider getPointsProvider() {
		return new ISettingsProvider() {
			@Override
			public float getFloat(Block name) {
				return 0;
			}
		};
	}

}
