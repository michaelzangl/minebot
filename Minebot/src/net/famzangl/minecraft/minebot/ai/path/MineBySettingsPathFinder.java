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

import net.famzangl.minecraft.minebot.ai.path.world.BlockFloatMap;
import net.minecraft.util.EnumFacing;

public class MineBySettingsPathFinder extends MinePathfinder {
//
//	protected class FileSettingsProvider implements ISettingsProvider {
//		private final String name_prefix;
//		private final float defaultValue;
//		private final float min;
//		private final float max;
//
//		public FileSettingsProvider(String name_prefix, float defaultValue,
//				float min, float max) {
//			this.name_prefix = name_prefix;
//			this.defaultValue = defaultValue;
//			this.min = min;
//			this.max = max;
//		}
//
//		@Override
//		public float getFloat(Block block) {
//			String blockName = AIHelper.getBlockName(block);
//			final float val = settings.getFloat(name_prefix + blockName,
//					defaultValue, min, max);
//			System.out.println(name_prefix + blockName + " -> " + val);
//			return val;
//		}
//
//	}

	public MineBySettingsPathFinder(EnumFacing preferedDirection,
			int preferedLayer) {
		super(preferedDirection, preferedLayer);
		maxDistancePoints = settings.getMining().getPointsMap().getMax();
		maxDistanceFactor = Math.max(MIN_FACTOR, settings.getMining().getFactorMap().getMax());
	}

	@Override
	protected BlockFloatMap getFactorProvider() {
		return settings.getMining().getFactorMap(); //new FileSettingsProvider("mine_factor_", 0, 0, MAX_FACTOR);
	}

	@Override
	protected BlockFloatMap getPointsProvider() {
		return settings.getMining().getPointsMap(); //new FileSettingsProvider("mine_points_", 1, 0, MAX_POINTS);
	}

}
