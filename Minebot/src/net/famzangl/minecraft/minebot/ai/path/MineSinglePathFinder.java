package net.famzangl.minecraft.minebot.ai.path;

import net.famzangl.minecraft.minebot.ai.AIHelper;

public class MineSinglePathFinder extends MinePathfinder {

	private final String blockName;

	public MineSinglePathFinder(AIHelper helper, String blockName) {
		super(helper);
		this.blockName = blockName;
	}

	@Override
	protected ISettingsProvider getFactorProvider() {
		return new ISettingsProvider() {
			@Override
			public float getFloat(String name) {
				return blockName.equalsIgnoreCase(name) ? 1 : 0;
			}
		};
	}

	@Override
	protected ISettingsProvider getPointsProvider() {
		return new ISettingsProvider() {
			@Override
			public float getFloat(String name) {
				return 0;
			}
		};
	}

}
