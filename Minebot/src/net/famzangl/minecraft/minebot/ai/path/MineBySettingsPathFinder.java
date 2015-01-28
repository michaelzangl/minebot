package net.famzangl.minecraft.minebot.ai.path;

import net.minecraft.block.Block;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class MineBySettingsPathFinder extends MinePathfinder {

	protected class FileSettingsProvider implements ISettingsProvider {
		private final String name_prefix;
		private final float defaultValue;
		private final float min;
		private final float max;

		public FileSettingsProvider(String name_prefix, float defaultValue,
				float min, float max) {
			this.name_prefix = name_prefix;
			this.defaultValue = defaultValue;
			this.min = min;
			this.max = max;
		}

		@Override
		public float getFloat(Block block) {
			final ResourceLocation name = ((ResourceLocation) Block.blockRegistry
					.getNameForObject(block));
			String domain = name.getResourceDomain().equals("minecraft") ? "" : name.getResourceDomain() + ":";
			final float val = settings.getFloat(name_prefix + domain + name.getResourcePath(),
					defaultValue, min, max);
			System.out.println(name_prefix + name + " -> " + val);
			return val;
		}
	}

	public MineBySettingsPathFinder(EnumFacing preferedDirection,
			int preferedLayer) {
		super(preferedDirection, preferedLayer);
		maxDistancePoints = 0;
		maxDistanceFactor = MIN_FACTOR;
		for (final String s : settings.getKeys()) {
			if (s.matches("mine_points_.*")) {
				final float p = settings.getFloat(s, 1, 0, MAX_POINTS);
				maxDistancePoints = Math.max(p, maxDistancePoints);
			} else if (s.matches("mine_factor_.*")) {
				final float p = settings.getFloat(s, 1, 0, MAX_FACTOR);
				if (p > 0) {
					maxDistanceFactor = Math.max(p, maxDistanceFactor);
				}
			}
		}
	}

	@Override
	protected ISettingsProvider getFactorProvider() {
		return new FileSettingsProvider("mine_factor_", 0, 0, MAX_FACTOR);
	}

	@Override
	protected ISettingsProvider getPointsProvider() {
		return new FileSettingsProvider("mine_points_", 1, 0, MAX_POINTS);
	}

}
