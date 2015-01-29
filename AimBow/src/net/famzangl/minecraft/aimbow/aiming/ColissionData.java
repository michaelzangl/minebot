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
package net.famzangl.minecraft.aimbow.aiming;

import net.minecraft.entity.Entity;

public class ColissionData {
	public double x, y, z;
	public Entity hitEntity;
	public int hitStep;

	public ColissionData(double x, double y, double z, Entity hitEntity,
			int hitStep) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
		this.hitEntity = hitEntity;
		this.hitStep = hitStep;
	}

	@Override
	public String toString() {
		return "ColissionData [x=" + x + ", y=" + y + ", z=" + z
				+ ", hitEntity=" + hitEntity + ", hitStep=" + hitStep + "]";
	}

}
