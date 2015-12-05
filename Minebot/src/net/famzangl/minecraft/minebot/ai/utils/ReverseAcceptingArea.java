package net.famzangl.minecraft.minebot.ai.utils;

import java.util.ArrayList;

import net.famzangl.minecraft.minebot.ai.path.world.WorldData;
import net.minecraft.util.BlockPos;

/**
 * This class wraps a block area and simply reverses the accept()-Method.
 * <p>
 * Reversing takes O(n) space.
 * @author Michael Zangl
 *
 */
public class ReverseAcceptingArea extends BlockArea {
	private final BlockArea area;
	
	private final static class CollectingVisitor implements AreaVisitor {
		ArrayList<BlockPos > positions = new ArrayList<BlockPos>();

		@Override
		public void visit(WorldData world, int x, int y, int z) {
			positions.add(new BlockPos(x, y, z));
		}
	}
	
	public ReverseAcceptingArea(BlockArea area) {
		super();
		this.area = area;
	}

	@Override
	public void accept(AreaVisitor v, WorldData world) {
		CollectingVisitor collected = new CollectingVisitor();
		area.accept(collected, world);
		ArrayList<BlockPos> positions = collected.positions;
		for (int i = positions.size() - 1; i >= 0; i--) {
			BlockPos p = positions.get(i);
			v.visit(world, p.getX(), p.getY(), p.getZ());
		}
	}

	@Override
	public boolean contains(WorldData world, int x, int y, int z) {
		return false;
	}

	@Override
	public String toString() {
		return "ReverseAcceptingArea [area=" + area + "]";
	}
}
