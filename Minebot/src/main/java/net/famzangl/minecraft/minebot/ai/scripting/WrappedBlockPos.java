package net.famzangl.minecraft.minebot.ai.scripting;

import net.minecraft.util.math.BlockPos;

public class WrappedBlockPos {
	public final int x;
	public final int y;
	public final int z;

	public WrappedBlockPos(int x, int y, int z) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public WrappedBlockPos(BlockPos playerPosition) {
		this (playerPosition.getX(), playerPosition.getY(), playerPosition.getZ());
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
		WrappedBlockPos other = (WrappedBlockPos) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		if (z != other.z)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "WrappedBlockPos [x=" + x + ", y=" + y + ", z=" + z + "]";
	}

}
