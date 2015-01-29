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
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.item.EnumDyeColor;

import com.google.common.base.Predicate;

public final class ColorSelector implements Predicate<Entity> {
	private final EnumDyeColor color;

	public ColorSelector(EnumDyeColor color) {
		super();
		this.color = color;
	}

	@Override
	public boolean apply(Entity var1) {
		if (var1 instanceof EntityWolf) {
			return ((EntityWolf) var1).getCollarColor() == color;
		} else if (var1 instanceof EntitySheep) {
			return ((EntitySheep) var1).getFleeceColor() == color;
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return "ColorSelector [color=" + color + "]";
	}

}