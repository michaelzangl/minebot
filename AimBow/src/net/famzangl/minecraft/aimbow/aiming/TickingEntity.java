package net.famzangl.minecraft.aimbow.aiming;

import net.minecraft.util.AxisAlignedBB;

public class TickingEntity {
	/**
	 * Entity position X
	 */
	public double posX;
	/**
	 * Entity position Y
	 */
	public double posY;
	/**
	 * Entity position Z
	 */
	public double posZ;
	/**
	 * Entity motion X
	 */
	public double motionX;
	/**
	 * Entity motion Y
	 */
	public double motionY;
	/**
	 * Entity motion Z
	 */
	public double motionZ;
	/**
	 * Entity rotation Yaw
	 */
	public float rotationYaw;
	/**
	 * Entity rotation Pitch
	 */
	public float rotationPitch;
	AxisAlignedBB boundingBox = new AxisAlignedBB(0, 0, 0, 0, 0, 0);
	public double prevPosX;
	public double prevPosY;
	public double prevPosZ;

	/**
	 * Sets the x,y,z of the entity from the given parameters. Also seems to set
	 * up a bounding box.
	 */
	public void setPosition(double par1, double par3, double par5) {
		this.posX = par1;
		this.posY = par3;
		this.posZ = par5;
		float f = getWidth() / 2.0F;
		float f1 = getHeight();
		this.boundingBox = new AxisAlignedBB(par1 - f, par3, par5 - f, par1 + f, par3
				+ f1, par5 + f);
	}

	private float getHeight() {
		return 0.5f;
	}

	private float getWidth() {
		return 0.5f;
	}

	public void moveTick() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;

		this.posX += this.motionX;
		this.posY += this.motionY;
		this.posZ += this.motionZ;
	}

}
