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

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.minecraft.block.Block;
import net.minecraft.util.EnumFacing;

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
			String blockName = AIHelper.getBlockName(block);
			final float val = settings.getFloat(name_prefix + blockName,
					defaultValue, min, max);
			System.out.println(name_prefix + blockName + " -> " + val);
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
