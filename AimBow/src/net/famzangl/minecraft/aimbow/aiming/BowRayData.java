package net.famzangl.minecraft.aimbow.aiming;

import java.util.Random;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

/**
 * A shooting ray.
 * 
 * @author michael
 *
 */
public class BowRayData extends TickingEntity {
	private static final boolean USE_RANDOM = false;
	float force;
	private boolean dead;
	private float prevRotationYaw;
	private float prevRotationPitch;

	public void setLocationAndAngles(double posX, double posY, double posZ,
			float rotationYaw, float rotationPitch) {
		this.rotationYaw = rotationYaw;
		this.rotationPitch = rotationPitch;
		this.setPosition(posX, posY, posZ);
	}

	public void shootFromTowards(EntityLivingBase shootingEntity, double force, Vec3 lookVec) {
		final float yaw = (float) (Math.atan2(lookVec.zCoord, lookVec.xCoord) * 180.0D / Math.PI) - 90.0F;
		final float pitch = (float) -(Math.atan2(lookVec.yCoord,
				Math.sqrt(lookVec.xCoord * lookVec.xCoord + lookVec.zCoord * lookVec.zCoord)) * 180.0D / Math.PI);
		setLocationAndAngles(shootingEntity.posX, shootingEntity.posY
				+ shootingEntity.getEyeHeight(), shootingEntity.posZ,
				yaw, pitch);
		shoot(force);
//		System.out.println("Yaw, pitch" + yaw + "," + pitch + "," + shootingEntity.rotationYaw+","+shootingEntity.rotationPitch);
	}

	public void shootFrom(EntityLivingBase shootingEntity, double force) {
		setLocationAndAngles(shootingEntity.posX, shootingEntity.posY
				+ shootingEntity.getEyeHeight(), shootingEntity.posZ,
				shootingEntity.rotationYaw, shootingEntity.rotationPitch);
		shoot(force);
	}

	private void shoot(double force) {
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
		float f1 = 0.05F;

		this.motionX *= f3;
		this.motionY *= f3;
		this.motionZ *= f3;
		this.motionY -= f1;
        this.setPosition(this.posX, this.posY, this.posZ);
	}

	public boolean isDead() {
		return dead;
	}

	public void setDead(boolean dead) {
		this.dead = dead;
	}

}
