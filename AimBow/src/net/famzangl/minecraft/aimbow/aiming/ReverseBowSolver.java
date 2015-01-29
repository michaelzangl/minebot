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

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
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
	private static final int MAX_STEPS = 120;
	private float gravity;
	private float velocity;
	
	public ReverseBowSolver(float gravity, float velocity) {
		this.gravity = gravity;
		this.velocity = velocity;
	}
	
	public Vec3 getLookForTarget(Entity target) {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		AxisAlignedBB boundingBox = target.getEntityBoundingBox();
		double targetX = (boundingBox.maxX + boundingBox.minX) / 2;
		double targetY = (boundingBox.maxY + boundingBox.minY) / 2;
		double targetZ = (boundingBox.maxZ + boundingBox.minZ) / 2;
		
		double dx = targetX - player.posX;
		double dz = targetZ - player.posZ;
		float dHor = (float) Math.sqrt(dx * dx + dz * dz );
		float dVert = (float) (targetY - (player.getEyeHeight() + player.posY));
	
		float y = getYForTarget(dHor, dVert);
		float xz = (float) Math.sqrt(1 - y * y);
		double x = dx / dHor * xz;
		double z = dz / dHor * xz;
		return new Vec3(x, y, z);
	}
	
	private float getYForTarget(float dHor, float dVert) {
		float maxVert = 0.9f, minVert = -0.9f;
		for (int attempts = 0; attempts < 50; attempts++) {
			float vert = (maxVert + minVert) / 2;
			float hor = (float) Math.sqrt(1 - vert * vert);
			float newY = getYAtDistance(hor * velocity, vert * velocity, dHor);
			if (Float.isNaN(newY)) {
				return 0;
			} else if (newY > dVert) {
				maxVert = vert;
			} else {
				minVert = vert;
			}
		}
		
		float res = (maxVert + minVert) / 2;
		//System.out.println("Got new vertical vector: " + res + " for d= " + dHor + "," + dVert);
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
		float f1 = gravity;
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
