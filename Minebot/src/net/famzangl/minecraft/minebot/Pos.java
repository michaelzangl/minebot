package net.famzangl.minecraft.minebot;

import net.minecraftforge.common.util.ForgeDirection;

/**
 * A position consisting of integer x y and z coordinates. Uses mainly for block
 * positions.
 * 
 * @author michael
 * 
 */
public class Pos {
	public int x;
	public int y;
	public int z;

	public Pos(int x, int y, int z) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public String toString() {
		return "Pos [x=" + x + ", y=" + y + ", z=" + z + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		result = prime * result + z;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Pos other = (Pos) obj;
		if (x != other.x) {
			return false;
		}
		if (y != other.y) {
			return false;
		}
		if (z != other.z) {
			return false;
		}
		return true;
	}

	public static Pos fromDir(ForgeDirection dir) {
		return new Pos(dir.offsetX, dir.offsetY, dir.offsetZ);
	}

	public Pos add(int x, int y, int z) {
		return new Pos(this.x + x, this.y + y, this.z + z);
	}

	public static Pos[] fromDir(ForgeDirection[] standable) {
		final Pos[] res = new Pos[standable.length];
		for (int i = 0; i < res.length; i++) {
			res[i] = fromDir(standable[i]);
		}
		return res;
	}

	public static Pos minPos(Pos p1, Pos p2) {
		return new Pos(Math.min(p1.x, p2.x), Math.min(p1.y, p2.y), Math.min(
				p1.z, p2.z));
	}

	public static Pos maxPos(Pos p1, Pos p2) {
		return new Pos(Math.max(p1.x, p2.x), Math.max(p1.y, p2.y), Math.max(
				p1.z, p2.z));
	}

	public Pos add(Pos pos) {
		return add(pos.x, pos.y, pos.z);
	}

	public Pos subtract(Pos pos) {
		return new Pos(x - pos.x, y - pos.y, z - pos.z);
	}

	public Pos multiply(int howMuch) {
		return new Pos(x * howMuch, y * howMuch, z * howMuch);
	}

}