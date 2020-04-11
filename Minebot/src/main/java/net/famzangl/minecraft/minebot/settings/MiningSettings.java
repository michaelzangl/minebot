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
		
		factorMap.setBlock(Blocks.COAL_ORE, 1);
		pointsMap.setBlock(Blocks.COAL_ORE, 0);

		factorMap.setBlock(Blocks.IRON_ORE, 1);
		pointsMap.setBlock(Blocks.IRON_ORE, 1);

		factorMap.setBlock(Blocks.REDSTONE_ORE, 1);
		pointsMap.setBlock(Blocks.REDSTONE_ORE, 1);

		factorMap.setBlock(Blocks.GOLD_ORE, 3);
		pointsMap.setBlock(Blocks.GOLD_ORE, 2);

		factorMap.setBlock(Blocks.LAPIS_ORE, 2);
		pointsMap.setBlock(Blocks.LAPIS_ORE, 2);

		factorMap.setBlock(Blocks.DIAMOND_ORE, 5);
		pointsMap.setBlock(Blocks.DIAMOND_ORE, 5);

		factorMap.setBlock(Blocks.EMERALD_ORE, 5);
		pointsMap.setBlock(Blocks.EMERALD_ORE, 5);

		factorMap.setBlock(Blocks.QUARTZ_ORE, 1);
		pointsMap.setBlock(Blocks.QUARTZ_ORE, 0);

		factorMap.setBlock(Blocks.GLOWSTONE, 2);
		pointsMap.setBlock(Blocks.GLOWSTONE, 0);
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
