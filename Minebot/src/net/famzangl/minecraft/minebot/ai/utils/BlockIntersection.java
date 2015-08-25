package net.famzangl.minecraft.minebot.ai.utils;

import java.util.Arrays;

import net.famzangl.minecraft.minebot.ai.path.world.WorldData;

public class BlockIntersection extends BlockArea {
	private class FilteredAreaVisitor implements AreaVisitor {
		private AreaVisitor v;
		private BlockArea[] areas;

		public FilteredAreaVisitor(AreaVisitor v, BlockArea[] areas) {
			this.v = v;
			this.areas = areas;
		}
		
		@Override
		public void visit(WorldData world, int x, int y, int z) {
			for (BlockArea a : areas) {
				if (!a.contains(world, x, y, z)) {
					return;
				}
			}
			v.visit(world, x, y, z);
		}
		
	}
	
	private BlockArea[] areas;

	public BlockIntersection(BlockArea...areas) {
		if (areas.length == 0) {
			throw new IllegalArgumentException("Empty areas array.");
		}
		this.areas = areas;
	}
	
	@Override
	public boolean contains(WorldData world, int x, int y, int z) {
		for (BlockArea a : areas) {
			if (!a.contains(world, x, y, z)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void accept(AreaVisitor v, WorldData world) {
		areas[0].accept(new FilteredAreaVisitor(v, Arrays.copyOfRange(areas, 1, areas.length)), world);
	}
}
