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

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

/**
 * A shooting ray.
 * 
 * @author michael
 *
 */
public class BowRayData extends RayData {
	private static final boolean USE_RANDOM = false;
	private int force;
	
	public BowRayData(int force) {
		this.force = force;
	}

	public void shoot() {
		this.posX -= MathHelper
				.cos(this.rotationYaw / 180.0F * (float) Math.PI) * 0.16F;
		this.posY -= 0.10000000149011612D;
		this.posZ -= MathHelper
				.sin(this.rotationYaw / 180.0F * (float) Math.PI) * 0.16F;
		this.setPosition(this.posX, this.posY, this.posZ);
		double motionX = -MathHelper.sin(this.rotationYaw / 180.0F
				* (float) Math.PI)
				* MathHelper.cos(this.rotationPitch / 180.0F * (float) Math.PI);
		double motionZ = MathHelper.cos(this.rotationYaw / 180.0F
				* (float) Math.PI)
				* MathHelper.cos(this.rotationPitch / 180.0F * (float) Math.PI);
		double motionY = (-MathHelper.sin(this.rotationPitch / 180.0F
				* (float) Math.PI));
		this.setThrowableHeading(motionX, motionY, motionZ, force * 1.5F,
				USE_RANDOM ? 1.0F : 0);
	}

	protected float getGravity() {
		return 0.05f;
	}

}
