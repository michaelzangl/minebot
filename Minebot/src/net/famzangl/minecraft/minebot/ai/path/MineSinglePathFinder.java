package net.famzangl.minecraft.minebot.ai.path;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.minecraft.block.Block;
import net.minecraftforge.common.util.ForgeDirection;

public class MineSinglePathFinder extends MinePathfinder {

	private final Block block;

	public MineSinglePathFinder(AIHelper helper, Block block, ForgeDirection preferedDirection, int preferedLayer) {
		super(helper, preferedDirection, preferedLayer);
		this.block = block;
	}

	@Override
	protected ISettingsProvider getFactorProvider() {
		return new ISettingsProvider() {
			@Override
			public float getFloat(Block block2) {
				return block.equals(block2) ? 1 : 0;
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
