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
package net.famzangl.minecraft.minebot.ai.selectors;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;

import java.lang.reflect.Method;
import java.util.function.Predicate;

public class OwnTameableSelector implements Predicate<Entity> {
	private final LivingEntity owner;

	public OwnTameableSelector(LivingEntity owner) {
		super();
		this.owner = owner;
	}

	@Override
	public boolean test(Entity entity) {
		return entity instanceof TameableEntity && isMine((TameableEntity) entity);
	}

	private boolean isMine(TameableEntity entity) {
		Method method;
		try {
			method = TameableEntity.class.getMethod("func_152113_b");
			//Unlucky mapping, real function unknown
			if (method != null) {
				// 1.7.10. No fix so far...
				return entity.isTamed();
			} else {
				return entity.getOwner() == owner;
			}

		} catch (final NoSuchMethodException e) {
			e.printStackTrace();
		} catch (final SecurityException e) {
			e.printStackTrace();
		}
		return true;
	}
}