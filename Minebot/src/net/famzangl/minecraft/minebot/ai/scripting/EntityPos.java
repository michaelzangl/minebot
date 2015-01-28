package net.famzangl.minecraft.minebot.ai.scripting;

import net.famzangl.minecraft.minebot.Pos;
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
}
