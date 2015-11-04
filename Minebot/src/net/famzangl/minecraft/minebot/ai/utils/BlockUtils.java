package net.famzangl.minecraft.minebot.ai.utils;

import net.famzangl.minecraft.minebot.ai.path.world.WorldData;
import net.famzangl.minecraft.minebot.ai.utils.BlockArea.AreaVisitor;

/**
 * Some block utils you might find helpful.
 * 
 * @author Michael Zangl
 *
 */
public class BlockUtils {
	private static class MinimumVisitor<ValT extends Comparable<ValT>>
			implements AreaVisitor {
		private ValT minimum = null;
		private final BlockMapper<ValT> mapper;

		public MinimumVisitor(BlockMapper<ValT> mapper) {
			this.mapper = mapper;
		}

		public ValT getMinimum() {
			return minimum;
		}

		@Override
		public void visit(WorldData world, int x, int y, int z) {
			ValT val = mapper.getValueFor(world, x, y, z);
			if (minimum == null || val.compareTo(minimum) < 0) {
				minimum = val;
			}
		}
	}

	/**
	 * Computes the minimum over an area of the world.
	 * 
	 * @param area
	 * @param world
	 * @param mapper
	 * @return The minimum value for that area or <code>null</code> if the area
	 *         is empty.
	 */
	public static <ValT extends Comparable<ValT>> ValT getMinimum(
			BlockArea area, WorldData world, BlockMapper<ValT> mapper) {
		MinimumVisitor<ValT> v = new MinimumVisitor<ValT>(mapper);
		area.accept(v, world);
		return v.getMinimum();
	}
}
