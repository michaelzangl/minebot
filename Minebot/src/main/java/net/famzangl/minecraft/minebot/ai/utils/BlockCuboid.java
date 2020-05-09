package net.famzangl.minecraft.minebot.ai.utils;

import net.famzangl.minecraft.minebot.ai.path.world.Pos;
import net.famzangl.minecraft.minebot.ai.path.world.WorldData;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

/**
 * This is cuboid of blocks.
 * <p>
 * It may not be empty.
 * 
 * @author Michael Zangl
 */
public class BlockCuboid<WorldT extends WorldData> extends BlockArea<WorldT> {
	/**
	 * The minimum x, y, z coordinate
	 */
	private BlockPos min;
	/**
	 * The maximum x, y, z coordinate that is in the cuboid.
	 */
	private BlockPos max;

	public BlockCuboid(BlockPos p1, BlockPos p2) {
		min = Pos.minPos(p1, p2);
		max = Pos.maxPos(p1, p2);
	}

	@Override
	public boolean contains(WorldData world, int x, int y, int z) {
		return min.getX() <= x && x <= max.getX() && min.getY() <= y
				&& y <= max.getY() && min.getZ() <= z && z <= max.getZ();
	}

	public BlockPos getMax() {
		return max;
	}

	public BlockPos getMin() {
		return min;
	}

	public int getVolume() {
		return (max.getX() - min.getX() + 1) * (max.getY() - min.getY() + 1)
				* (max.getZ() - min.getZ() + 1);
	}

	public <WorldT2 extends WorldT> void accept(AreaVisitor<? super WorldT2> visitor, WorldT2 world) {
		int minY = min.getY();
		int maxY = max.getY();
		for (int y = minY; y <= maxY; y++) {
			acceptY(visitor, y, world);
		}
	}

	private <WorldT2 extends WorldT> void acceptY(AreaVisitor<? super WorldT2> visitor, int y, WorldT2 world) {
		int minZ = min.getZ();
		int maxZ = max.getZ();
		int minX = min.getX();
		int maxX = max.getX();
		for (int z = minZ; z <= maxZ; z++) {
			for (int x = minX; x <= maxX; x++) {
				visitor.visit(world, x, y, z);
			}
		}
	}

	/**
	 * Extend in x and z directions.
	 * 
	 * @param extend
	 *            how much
	 * @return The extended cuboid.
	 */
	public BlockCuboid<WorldT> extendXZ(int extend) {
		return new BlockCuboid<>(min.add(-extend, 0, -extend), max.add(extend, 0,
				extend));
	}

	public BlockCuboid<WorldT> extend(int amount, Direction direction) {
		return boundsWith(move(amount, direction));
	}

	private BlockCuboid<WorldT> boundsWith(BlockCuboid<?> other) {
		return new BlockCuboid<>(Pos.minPos(min, other.min), Pos.maxPos(max, other.max));
	}

	public BlockCuboid<WorldT> move(int amount, Direction direction) {
		return move(Pos.ZERO.offset(direction, amount));
	}

	public BlockCuboid<WorldT> move(Vec3i vec) {
		return new BlockCuboid<>(min.add(vec), max.add(vec));
	}

	@Override
	public String toString() {
		return "BlockCuboid [x=" + min.getX() + ".." + max.getX() + "," +
				"y=" + min.getY() + ".." + max.getY() + "," +
				"z=" + min.getZ() + ".." + max.getZ() + "]";
	}
}
