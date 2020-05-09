package net.famzangl.minecraft.minebot.ai.utils;

import net.famzangl.minecraft.minebot.ai.path.world.WorldData;

/**
 * Intersection of two block areas
 * @param <WorldT> The world type
 */
public class AreaIntersection<WorldT extends WorldData> extends BlockArea<WorldT> {
	private static class FilteredAreaVisitor<WorldT extends WorldData> implements AreaVisitor<WorldT> {
		private AreaVisitor<? super WorldT> visitor;
		private final BlockArea<? super WorldT> b;

		public FilteredAreaVisitor(AreaVisitor<? super WorldT> visitor, BlockArea<? super WorldT> b) {
			this.visitor = visitor;
			this.b = b;
		}
		
		@Override
		public void visit(WorldT world, int x, int y, int z) {
			if (!b.contains(world, x, y, z)) {
				return;
			}
			visitor.visit(world, x, y, z);
		}
		
	}
	
	private final BlockArea<? super WorldT> a;
	private final BlockArea<? super WorldT> b;

	public AreaIntersection(BlockArea<? super WorldT> a, BlockArea<? super WorldT> b) {
		this.a = a;
		this.b = b;
	}
	
	@Override
	public boolean contains(WorldT world, int x, int y, int z) {
		return a.contains(world, x, y, z) && b.contains(world, x, y, z);
	}

	@Override
	public <WorldT2 extends WorldT> void accept(AreaVisitor<? super WorldT2> visitor, WorldT2 world) {
		a.accept(new FilteredAreaVisitor<WorldT2>(visitor, b), world);
	}

	@Override
	public String toString() {
		return a + " ∩ " + b;
	}
}
