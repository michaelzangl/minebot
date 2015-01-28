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
