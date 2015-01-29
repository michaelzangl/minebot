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
package net.famzangl.minecraft.minebot.ai.strategy;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.minecraft.entity.Entity;

import com.google.common.base.Predicate;

/**
 * Do an action when a given entity approaches.
 * @author michael
 *
 */
public abstract class CloseEntityActionStrategy extends ValueActionStrategy {
	@Override
	protected double getValue(final AIHelper helper) {
		final Entity closest = helper.getClosestEntity(50,
				new Predicate<Entity>() {
					@Override
					public boolean apply(Entity player) {
						return matches(helper, player);
					}

				});
		return closest == null ? Double.MAX_VALUE : closest
				.getDistanceToEntity(helper.getMinecraft().thePlayer);
	}

	protected abstract boolean matches(AIHelper helper, Entity player);
}
