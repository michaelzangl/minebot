package net.famzangl.minecraft.minebot;

import net.minecraftforge.common.util.ForgeDirection;

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
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pos other = (Pos) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		if (z != other.z)
			return false;
		return true;
	}

	public static Pos fromDir(ForgeDirection dir) {
		return new Pos(dir.offsetX, dir.offsetY, dir.offsetZ);
	}

	public Pos add(int x, int y, int z) {
		return new Pos(this.x + x, this.y + y, this.z + z);
	}

	public static Pos[] fromDir(ForgeDirection[] standable) {
		Pos[] res = new Pos[standable.length];
		for (int i = 0; i < res.length; i++) {
			res[i] = fromDir(standable[i]);
		}
		return res;
	}

}