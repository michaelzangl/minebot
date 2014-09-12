package net.famzangl.minecraft.minebot.ai.scanner;

import java.util.BitSet;

import net.famzangl.minecraft.minebot.Pos;

public class BlockFlagTable {
	private final BitSet bits = new BitSet();
	private final Pos max;
	private final Pos min;

	public BlockFlagTable(Pos pos1, Pos pos2) {
		this.max = Pos.maxPos(pos1, pos2);
		this.min = Pos.minPos(pos1, pos2);
	}

	public void setBit(int x,int y,int z, boolean value) {
		int index = getIndex(x, y, z);
		if (index >= 0) {
			bits.set(index, value);
		}
	}
	
	public boolean getBit(int x, int y, int z) {
		int index = getIndex(x, y, z);
		if (index >= 0) {
			return bits.get(index);
		} else {
			return false;
		}
	}

	private int getIndex(int x, int y, int z) {
		if (x < min.x || x > max.x || y < min.y || y > max.y || z < min.z || z > max.z) {
			return -1;
		}
		int v = z - min.z;
		v *= (max.y - min.y + 1);
		v += y - min.y;
		v *= (max.x - min.x + 1);
		v += x - min.x;
		return v;
	}
}
