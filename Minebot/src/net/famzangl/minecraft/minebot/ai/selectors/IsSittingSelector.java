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
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityWolf;

public final class IsSittingSelector extends OwnTameableSelector {
	private final boolean sitting;

	public IsSittingSelector(boolean sitting, EntityLivingBase owner) {
		super(owner);
		this.sitting = sitting;
	}

	@Override
	public boolean apply(Entity var1) {
		return super.apply(var1)
				&& ((EntityWolf) var1).isSitting() == sitting;
	}
}