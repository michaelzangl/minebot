package net.famzangl.minecraft.minebot.ai.path.world;

import net.famzangl.minecraft.minebot.ai.utils.RandUtils;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class BlockBounds {
	private static final Marker MARKER_BOUNDS_PROBLEM = MarkerManager
			.getMarker("missing bounds");
	private static final Logger LOGGER = LogManager
			.getLogger(BlockBounds.class);

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

	/**
	 * This is the same as full block. But you can check with == if this block
	 * was returned.
	 */
	public static final BlockBounds UNKNOWN_BLOCK = new BlockBounds(0, 1);

	public static final BlockBounds FULL_BLOCK = new BlockBounds(0, 1);
	private static final BlockSet FULL_BLOCKS = BlockSets.SIMPLE_CUBE.unionWith(BlockSets.AIR);
	public static final BlockBounds LOWER_HALF_BLOCK = new BlockBounds(0, 0.5);
	private static final BlockSet LOWER_HALF_BLOCKS = BlockSets.LOWER_SLABS;
	public static final BlockBounds UPPER_HALF_BLOCK = new BlockBounds(0.5, 1);

	private static final BlockSet UPPER_HALF_BLOCKS = BlockSets.UPPER_SLABS;

	public static BlockBounds forBlockWithMeta(int blockIdWithMeta) {
		if (FULL_BLOCKS.containsWithMeta(blockIdWithMeta)) {
			return FULL_BLOCK;
		} else if (LOWER_HALF_BLOCKS.containsWithMeta(blockIdWithMeta)) {
			return LOWER_HALF_BLOCK;
		} else if (UPPER_HALF_BLOCKS.containsWithMeta(blockIdWithMeta)) {
			return UPPER_HALF_BLOCK;
		}

		LOGGER.warn(MARKER_BOUNDS_PROBLEM, "Missing bounds for block+meta: "
				+ blockIdWithMeta);
		return FULL_BLOCK;
		// Block block = Block.getBlockById(blockIdWithMeta >> 4);
		// FIXME: Set bounds based on state.
		// return new BlockBounds(block.getBlockBoundsMinX(),
		// block.getBlockBoundsMaxX(), block.getBlockBoundsMinY(),
		// block.getBlockBoundsMaxY(), block.getBlockBoundsMinZ(),
		// block.getBlockBoundsMaxZ());
	}

	public BlockBounds onlySide(EnumFacing side) {
		return new BlockBounds(side == EnumFacing.EAST ? maxX : minX,
				side == EnumFacing.WEST ? minX : maxX,
				side == EnumFacing.UP ? maxY : minY,
				side == EnumFacing.DOWN ? minY : maxY,
				side == EnumFacing.SOUTH ? maxZ : minZ,
				side == EnumFacing.NORTH ? minZ : maxZ);
	}

	public Vec3 random(BlockPos pos, double centered) {
		return new Vec3(
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
	public String toString() {
		return "BlockBounds [minX=" + minX + ", maxX=" + maxX + ", minY="
				+ minY + ", maxY=" + maxY + ", minZ=" + minZ + ", maxZ=" + maxZ
				+ "]";
	}

}
