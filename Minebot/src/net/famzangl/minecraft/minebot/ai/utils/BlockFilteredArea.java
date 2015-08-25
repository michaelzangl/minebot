package net.famzangl.minecraft.minebot.ai.utils;

import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.famzangl.minecraft.minebot.ai.path.world.WorldData;

/**
 * This is an area that only contains specific blocks.
 * 
 * @author Michael Zangl
 *
 */
public class BlockFilteredArea extends BlockArea {
	private BlockArea base;
	private BlockSet containedBlocks;

	private static class FitleredVisitor implements AreaVisitor {

		private AreaVisitor v;
		private WorldData world;
		private BlockSet containedBlocks;

		public FitleredVisitor(AreaVisitor v, WorldData world,
				BlockSet containedBlocks) {
			this.v = v;
			this.world = world;
			this.containedBlocks = containedBlocks;
		}

		@Override
		public void visit(WorldData world, int x, int y, int z) {
			if (containedBlocks.isAt(world, x, y, z)) {
				v.visit(world, x, y, z);
			}
		}

	}

	public BlockFilteredArea(BlockArea base,
			BlockSet containedBlocks) {
		this.base = base;
		this.containedBlocks = containedBlocks;
	}

	@Override
	public void accept(AreaVisitor v, WorldData world) {
		base.accept(new FitleredVisitor(v, world, containedBlocks), world);
	}

	@Override
	public boolean contains(WorldData world, int x, int y, int z) {
		return containedBlocks.isAt(world, x, y, z) && base.contains(world, x, y, z);
	}
}
