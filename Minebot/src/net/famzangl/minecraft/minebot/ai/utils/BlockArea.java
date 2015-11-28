package net.famzangl.minecraft.minebot.ai.utils;

import net.famzangl.minecraft.minebot.ai.path.world.WorldData;
import net.minecraft.util.BlockPos;

/**
 * This is an area of blocks. It is allowed to be empty. An area is final and
 * cannot be changed after being created.
 * 
 * @author Michael Zangl
 */
public abstract class BlockArea {
	public interface AreaVisitor {
		public void visit(WorldData world, int x, int y, int z);
	}

	private static class VolumeVisitor implements AreaVisitor {
		private int volume = 0;

		@Override
		public void visit(WorldData world, int x, int y, int z) {
			volume++;
		}
	}

	public boolean contains(WorldData world, BlockPos p) {
		return contains(world, p.getX(), p.getY(), p.getZ());
	}

	public abstract void accept(AreaVisitor v, WorldData world);

	public abstract boolean contains(WorldData world, int x, int y, int z);

	public int getVolume(WorldData world) {
		VolumeVisitor v = new VolumeVisitor();
		accept(v, world);
		return v.volume;
	}
	
	public BlockIntersection intersectWith(BlockArea other) {
		return new BlockIntersection(this, other);
	}

}
