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
package net.famzangl.minecraft.minebot.ai.scripting;

import net.famzangl.minecraft.minebot.ai.path.world.Pos;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;

/**
 * An entity state.
 * @author michael
 *
 */
public class EntityPos {
	public final double x;
	public final double y;
	public final double z;
	public final double motionX;
	public final double motionY;
	public final double motionZ;
	public final double yaw;
	public final double pitch;
	public final double speed;
	
	public EntityPos(Entity e) {
		this(e.posX, e.posY, e.posZ, e.motionX, e.motionY, e.motionZ, e.rotationYaw, e.rotationPitch);
	}

	public EntityPos(double x, double y, double z, double motionX,
			double motionY, double motionZ, double yaw, double pitch) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.motionX = motionX;
		this.motionY = motionY;
		this.motionZ = motionZ;
		this.yaw = yaw;
		this.pitch = pitch;
		this.speed = Pos.length(motionX, motionY, motionZ);
	}

	public double distance(BlockPos other) {
		return Pos.length(other.getX() - x, other.getY() - y, other.getZ() - z);
	}

	public double distance(EntityPos other) {
		return Pos.length(other.x - x, other.y - y, other.z - z);
	}
	
	public double distance(double x, double y, double z) {
		return Pos.length(this.x - x, this.y - y, this.z - z);
	}
}
