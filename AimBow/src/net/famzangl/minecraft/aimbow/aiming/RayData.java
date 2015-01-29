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
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public abstract class RayData extends TickingEntity {
	protected boolean dead;
	protected float prevRotationYaw;
	protected float prevRotationPitch;


	public void setLocationAndAngles(double posX, double posY, double posZ,
			float rotationYaw, float rotationPitch) {
		this.rotationYaw = rotationYaw;
		this.rotationPitch = rotationPitch;
		this.setPosition(posX, posY, posZ);
	}

	/**
	 * 
	 * @param shootingEntity
	 * @param force
	 * @param lookVec
	 * @see EntityArrow#setThrowableHeading(double, double, double, float, float)
	 */
	public void shootFromTowards(Entity shootingEntity, Vec3 lookVec) {
		final float yaw = (float) (Math.atan2(lookVec.zCoord, lookVec.xCoord) * 180.0D / Math.PI) - 90.0F;
		final float pitch = (float) -(Math.atan2(lookVec.yCoord,
				Math.sqrt(lookVec.xCoord * lookVec.xCoord + lookVec.zCoord * lookVec.zCoord)) * 180.0D / Math.PI);
		setLocationAndAngles(shootingEntity.posX, shootingEntity.posY
				+ shootingEntity.getEyeHeight(), shootingEntity.posZ,
				yaw, pitch);
		shoot();
	}
	
	/**
	 * 
	 * @param entity
	 * @param force
	 * @see EntityArrow#EntityArrow(net.minecraft.world.World, EntityLivingBase, float)
	 */
	public void shootFrom(Entity entity) {
		setLocationAndAngles(entity.posX, entity.posY
				+ entity.getEyeHeight(), entity.posZ,
				entity.rotationYaw, entity.rotationPitch);
		shoot();
	}

	/**
	 * Compute the movement for the next tick.
	 */
	@Override
	public void moveTick() {
		super.moveTick();

		float f2 = MathHelper.sqrt_double(this.motionX * this.motionX
				+ this.motionZ * this.motionZ);
		this.rotationYaw = (float) (Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);

		for (this.rotationPitch = (float) (Math.atan2(this.motionY, f2) * 180.0D / Math.PI); this.rotationPitch
				- this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F) {
			;
		}

		while (this.rotationPitch - this.prevRotationPitch >= 180.0F) {
			this.prevRotationPitch += 360.0F;
		}

		while (this.rotationYaw - this.prevRotationYaw < -180.0F) {
			this.prevRotationYaw -= 360.0F;
		}

		while (this.rotationYaw - this.prevRotationYaw >= 180.0F) {
			this.prevRotationYaw += 360.0F;
		}

		this.rotationPitch = this.prevRotationPitch
				+ (this.rotationPitch - this.prevRotationPitch) * 0.2F;
		this.rotationYaw = this.prevRotationYaw
				+ (this.rotationYaw - this.prevRotationYaw) * 0.2F;
		float f3 = 0.99F;
		float f1 = getGravity();

		this.motionX *= f3;
		this.motionY *= f3;
		this.motionZ *= f3;
		this.motionY -= f1;
		this.setPosition(this.posX, this.posY, this.posZ);
	}

	protected abstract float getGravity();

	public abstract void shoot();

	public void setThrowableHeading(double motionX, double motionY,
			double motionZ, double force, float randomInfluence) {
		float f2 = MathHelper.sqrt_double(motionX * motionX + motionY * motionY
				+ motionZ * motionZ);
		motionX /= f2;
		motionY /= f2;
		motionZ /= f2;
		Random rand = new Random();
		motionX += rand.nextGaussian() * (rand.nextBoolean() ? -1 : 1)
				* 0.007499999832361937D * randomInfluence;
		motionY += rand.nextGaussian() * (rand.nextBoolean() ? -1 : 1)
				* 0.007499999832361937D * randomInfluence;
		motionZ += rand.nextGaussian() * (rand.nextBoolean() ? -1 : 1)
				* 0.007499999832361937D * randomInfluence;
		motionX *= force;
		motionY *= force;
		motionZ *= force;
		this.motionX = motionX;
		this.motionY = motionY;
		this.motionZ = motionZ;
		float f3 = MathHelper
				.sqrt_double(motionX * motionX + motionZ * motionZ);
		this.prevRotationYaw = this.rotationYaw = (float) (Math.atan2(motionX,
				motionZ) * 180.0D / Math.PI);
		this.prevRotationPitch = this.rotationPitch = (float) (Math.atan2(
				motionY, f3) * 180.0D / Math.PI);
	}
	
	public boolean isDead() {
		return dead;
	}

	public void setDead(boolean dead) {
		this.dead = dead;
	}

}
