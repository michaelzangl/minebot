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
