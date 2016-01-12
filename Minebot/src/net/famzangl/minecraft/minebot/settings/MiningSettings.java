package net.famzangl.minecraft.minebot.settings;

import net.famzangl.minecraft.minebot.ai.path.world.BlockFloatMap;
import net.minecraft.init.Blocks;

@MinebotSettingObject
public class MiningSettings {
	@ClampedFloat(min = 0, max = 1)
	private float randomness = 0.05f;

	@ClampedFloat(min = 0, max = 10)
	private float doubleBonus = 2;

	@ConstrainedBlockFloat(defaultValue = 1, min = 0, max = 10)
	private BlockFloatMap factorMap = new BlockFloatMap();

	@ConstrainedBlockFloat(defaultValue = 1, min = 0, max = 50)
	private BlockFloatMap pointsMap = new BlockFloatMap();

	public MiningSettings() {
		factorMap.setDefault(0);
		pointsMap.setDefault(0);
		
		factorMap.setBlock(Blocks.coal_ore, 1);
		pointsMap.setBlock(Blocks.coal_ore, 0);

		factorMap.setBlock(Blocks.iron_ore, 1);
		pointsMap.setBlock(Blocks.iron_ore, 1);

		factorMap.setBlock(Blocks.redstone_ore, 1);
		pointsMap.setBlock(Blocks.redstone_ore, 1);

		factorMap.setBlock(Blocks.gold_ore, 3);
		pointsMap.setBlock(Blocks.gold_ore, 2);

		factorMap.setBlock(Blocks.lapis_ore, 2);
		pointsMap.setBlock(Blocks.lapis_ore, 2);

		factorMap.setBlock(Blocks.diamond_ore, 5);
		pointsMap.setBlock(Blocks.diamond_ore, 5);

		factorMap.setBlock(Blocks.emerald_ore, 5);
		pointsMap.setBlock(Blocks.emerald_ore, 5);

		factorMap.setBlock(Blocks.quartz_ore, 1);
		pointsMap.setBlock(Blocks.quartz_ore, 0);

		factorMap.setBlock(Blocks.glowstone, 2);
		pointsMap.setBlock(Blocks.glowstone, 0);
	}

	public float getDoubleBonus() {
		return doubleBonus;
	}

	public float getRandomness() {
		return randomness;
	}

	public BlockFloatMap getFactorMap() {
		return factorMap;
	}

	public BlockFloatMap getPointsMap() {
		return pointsMap;
	}
}
