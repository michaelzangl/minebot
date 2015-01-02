package net.famzangl.minecraft.aimbow.aiming;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;

/**
 * Gets a yaw/pitch for a given entity.
 * <p>
 * Bow position after n ticks:
 * <p>
 * X, Z: direction * (.99) ^ (1-n)
 * <p>
 * Y: direction * (.99) ^ (1-n) + 5 ^ (1-2*n)*(99/4)^n - 5
 * 
 * @author michael
 *
 */
public class ReverseBowSolver {
	private static final float FORCE = 3;
	private static final int MAX_STEPS = 120;
	
	public Vec3 getLookForTarget(Entity target) {
		EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
		double targetX = (target.boundingBox.maxX + target.boundingBox.minX) / 2;
		double targetY = (target.boundingBox.maxY + target.boundingBox.minY) / 2;
		double targetZ = (target.boundingBox.maxZ + target.boundingBox.minZ) / 2;
		
		double dx = targetX - player.posX;
		double dz = targetZ - player.posZ;
		float dHor = (float) Math.sqrt(dx * dx + dz * dz );
		float dVert = (float) (targetY - player.posY);
	
		float y = getYForTarget(dHor, dVert);
		float xz = (float) Math.sqrt(1 - y * y);
		double x = dx / dHor * xz;
		double z = dz / dHor * xz;
		return Vec3.createVectorHelper(x, y, z);
	}
	
	private float getYForTarget(float dHor, float dVert) {
		float maxVert = 0.9f, minVert = -0.9f;
		for (int attempts = 0; attempts < 50; attempts++) {
			float vert = (maxVert + minVert) / 2;
			float hor = (float) Math.sqrt(1 - vert * vert);
			float newY = getYAtDistance(hor * FORCE, vert * FORCE, dHor);
			if (Float.isNaN(newY)) {
				return 0;
			} else if (newY > dVert) {
				maxVert = vert;
			} else {
				minVert = vert;
			}
		}
		
		float res = (maxVert + minVert) / 2;
//		System.out.println("Got new vertical vector: " + res + " for d= " + dHor + "," + dVert);
		return res;
	}

	/**
	 * Shoot at (0,0) with a given direction.
	 * @param shootHor
	 * @param shootVert
	 * @param dHor
	 * @return 
	 */
	private float getYAtDistance(float motionX, float motionY, float dHor) {
		float f3 = 0.99F;
		float f1 = 0.05F;
		float posX = 0, posY = 0;

		for (int i = 0; i < MAX_STEPS; i++) {
			if (posX + motionX >= dHor) {
				float step = (dHor - posX) / motionX;
				return posY + step * motionY;
			}
			

	        posX += motionX;
	        posY += motionY;
	        
			motionX *= f3;
			motionY *= f3;
			motionY -= f1;
		}
		return posY;
	}
}
