package net.famzangl.minecraft.minebot.ai.path.world;

import net.famzangl.minecraft.minebot.ai.utils.RandUtils;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;

public class BlockBounds {
	private final double minX;
	private final double maxX;
	private final double minY;
	private final double maxY;
	private final double minZ;
	private final double maxZ;

	public BlockBounds(double minY, double maxY) {
		this(0, 1, minY, maxY, 0, 1);
	}

	public BlockBounds(double minX, double maxX, double minY, double maxY,
			double minZ, double maxZ) {
		this.minX = minX;
		this.maxX = maxX;
		this.minY = minY;
		this.maxY = maxY;
		this.minZ = minZ;
		this.maxZ = maxZ;
	}

	public static BlockBounds from(VoxelShape vs) {
		return vs.isEmpty() ? new BlockBounds(0.5, 0.5, 0.5, 0.5, 0.5, 0.5) : new BlockBounds(vs.getBoundingBox());
	}

	public BlockBounds(AxisAlignedBB axisAlignedBB) {
		this(axisAlignedBB.minX, axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxY,
				axisAlignedBB.minZ, axisAlignedBB.maxZ);
	}

	/**
	 * This is the same as full block. But you can check with == if this block
	 * was returned.
	 */
	public static final BlockBounds UNKNOWN_BLOCK = new BlockBounds(0, 1);

	public static final BlockBounds FULL_BLOCK = new BlockBounds(0, 1);
	public static final BlockBounds LOWER_HALF_BLOCK = new BlockBounds(0, 0.5);
	public static final BlockBounds UPPER_HALF_BLOCK = new BlockBounds(0.5, 1);

	public BlockBounds onlySide(Direction side) {
		return new BlockBounds(side == Direction.EAST ? maxX : minX,
				side == Direction.WEST ? minX : maxX,
				side == Direction.UP ? maxY : minY,
				side == Direction.DOWN ? minY : maxY,
				side == Direction.SOUTH ? maxZ : minZ,
				side == Direction.NORTH ? minZ : maxZ);
	}

	public Vec3d random(BlockPos pos, double centered) {
		return new Vec3d(
				pos.getX() + RandUtils.getBetweenCentered(minX, maxX, centered),
				pos.getY() + RandUtils.getBetweenCentered(minY, maxY, centered),
				pos.getZ() + RandUtils.getBetweenCentered(minZ, maxZ, centered));
	}

	public BlockBounds clampY(double minY2, double maxY2) {
		return new BlockBounds(minX, maxX, Math.max(minY, minY2), Math.min(
				maxY, maxY2), minZ, maxZ);
	}

	public double getMinX() {
		return minX;
	}

	public double getMaxX() {
		return maxX;
	}

	public double getMinY() {
		return minY;
	}

	public double getMaxY() {
		return maxY;
	}

	public double getMinZ() {
		return minZ;
	}

	public double getMaxZ() {
		return maxZ;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(maxX);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(maxY);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(maxZ);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(minX);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(minY);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(minZ);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		BlockBounds other = (BlockBounds) obj;
		if (Double.doubleToLongBits(maxX) != Double
				.doubleToLongBits(other.maxX))
			return false;
		if (Double.doubleToLongBits(maxY) != Double
				.doubleToLongBits(other.maxY))
			return false;
		if (Double.doubleToLongBits(maxZ) != Double
				.doubleToLongBits(other.maxZ))
			return false;
		if (Double.doubleToLongBits(minX) != Double
				.doubleToLongBits(other.minX))
			return false;
		if (Double.doubleToLongBits(minY) != Double
				.doubleToLongBits(other.minY))
			return false;
		if (Double.doubleToLongBits(minZ) != Double
				.doubleToLongBits(other.minZ))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "BlockBounds [x=" + minX + ".." + maxX + ", y="
				+ minY + ".." + maxY + ", z=" + minZ + ".." + maxZ
				+ "]";
	}
}
