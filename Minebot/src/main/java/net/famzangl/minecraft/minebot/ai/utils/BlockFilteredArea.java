package net.famzangl.minecraft.minebot.ai.utils;

import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.famzangl.minecraft.minebot.ai.path.world.WorldData;

/**
 * This is an area that only contains specific blocks.
 * 
 * @author Michael Zangl
 *
 */
public class BlockFilteredArea<W extends WorldData> extends BlockArea<W> {
	private final BlockArea<W> base;
	private final BlockSet containedBlocks;

	private static class FilteredVisitor<W extends WorldData> implements AreaVisitor<W> {

		private final AreaVisitor<? super W> visitor;
		private final WorldData world;
		private final BlockSet containedBlocks;

		public FilteredVisitor(AreaVisitor<? super W> visitor, WorldData world,
							   BlockSet containedBlocks) {
			this.visitor = visitor;
			this.world = world;
			this.containedBlocks = containedBlocks;
		}

		@Override
		public void visit(W world, int x, int y, int z) {
			if (containedBlocks.isAt(world, x, y, z)) {
				visitor.visit(world, x, y, z);
			}
		}

	}

	public BlockFilteredArea(BlockArea<W> base,
			BlockSet containedBlocks) {
		this.base = base;
		this.containedBlocks = containedBlocks;
	}

	@Override
	public <WorldT2 extends W> void accept(AreaVisitor<? super WorldT2> visitor, WorldT2 world) {
		base.accept(new FilteredVisitor<WorldT2>(visitor, world, containedBlocks), world);
	}

	@Override
	public boolean contains(W world, int x, int y, int z) {
		return containedBlocks.isAt(world, x, y, z) && base.contains(world, x, y, z);
	}
}
