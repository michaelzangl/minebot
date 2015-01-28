package net.famzangl.minecraft.minebot;

import net.famzangl.minecraft.minebot.ai.scripting.DoublePos;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

/**
 * A position consisting of integer x y and z coordinates. Uses mainly for block
 * positions.
 * 
 * @author michael
 * 
 */
public class Pos extends BlockPos {
	
	public static BlockPos ZERO = new BlockPos(0,0,0);

	public Pos(int x, int y, int z) {
		super(x, y, z);
	}

	public static BlockPos fromDir(EnumFacing dir) {
		return ZERO.offset(dir);
	}

	public Pos add(int x, int y, int z) {
		return new Pos(this.getX() + x, this.getY() + y, this.getZ() + z);
	}

	public static BlockPos[] fromDir(EnumFacing[] standable) {
		final BlockPos[] res = new BlockPos[standable.length];
		for (int i = 0; i < res.length; i++) {
			res[i] = fromDir(standable[i]);
		}
		return res;
	}

	public static Pos minPos(Pos p1, Pos p2) {
		return new Pos(Math.min(p1.getX(), p2.getX()), Math.min(p1.getY(), p2.getY()), Math.min(
				p1.getZ(), p2.getZ()));
	}

	public static Pos maxPos(Pos p1, Pos p2) {
		return new Pos(Math.max(p1.getX(), p2.getX()), Math.max(p1.getY(), p2.getY()), Math.max(
				p1.getZ(), p2.getZ()));
	}

	public double distance(Pos other) {
		return length(other.getX() - getX(), other.getY() - getY(), other.getZ() - getZ());
	}
	
	public static double length(double dx, double dy, double dz) {
		return Math.sqrt(dx * dx + dy * dy + dz * dz);
	}

	public double distance(DoublePos other) {
		return other.distance(this);
	}
}