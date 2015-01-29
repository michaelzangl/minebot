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

import java.util.List;

import net.minecraft.entity.Entity;

import com.google.common.base.Predicate;

public class OneOfListSelector implements Predicate<Entity> {

	private final List<Entity> list;

	public OneOfListSelector(List<Entity> list) {
		this.list = list;
	}

	@Override
	public boolean apply(Entity var1) {
		return list.contains(var1);
	}

}
