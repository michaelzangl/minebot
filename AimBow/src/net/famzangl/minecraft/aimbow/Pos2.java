package net.famzangl.minecraft.aimbow;

public class Pos2 {
	public final int x;
	public final int y;

	public Pos2(int x, int y) {
		super();
		this.x = x;
		this.y = y;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
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
		Pos2 other = (Pos2) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Pos2 [x=" + x + ", y=" + y + "]";
	}

	public double distanceTo(Pos2 onScreen) {
		return Math.sqrt((x - onScreen.x) * (x - onScreen.x) + (y - onScreen.y)
				* (y - onScreen.y));
	}

}
