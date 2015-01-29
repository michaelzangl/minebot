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

import java.lang.reflect.Method;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;

import com.google.common.base.Predicate;

public class OwnTameableSelector implements Predicate<Entity> {
	private final EntityLivingBase owner;

	public OwnTameableSelector(EntityLivingBase owner) {
		super();
		this.owner = owner;
	}

	@Override
	public boolean apply(Entity var1) {
		return var1 instanceof EntityTameable && isMine((EntityTameable) var1);
	}

	private boolean isMine(EntityTameable var1) {
		Method m;
		try {
			m = EntityTameable.class.getMethod("func_152113_b");

			if (m != null) {
				// 1.7.10. No fix so far...
				return var1.isTamed();
			} else {
				return var1.getOwner() == owner;
			}

		} catch (final NoSuchMethodException e1) {
			e1.printStackTrace();
		} catch (final SecurityException e1) {
			e1.printStackTrace();
		}
		return true;
	}
}