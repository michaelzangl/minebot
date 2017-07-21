package net.famzangl.minecraft.minebot.ai.utils;

import net.famzangl.minecraft.minebot.ai.path.world.WorldData;
import net.minecraft.util.math.BlockPos;

/**
 * This is an area of blocks. It is allowed to be empty. An area is final and
 * cannot be changed after being created.
 * 
 * @author Michael Zangl
 */
public abstract class BlockArea<WorldT extends WorldData> {
	public interface AreaVisitor<WorldT extends WorldData> {
		public void visit(WorldT world, int x, int y, int z);
	}

	private static class VolumeVisitor implements AreaVisitor<WorldData> {
		private int volume = 0;

		@Override
		public void visit(WorldData world, int x, int y, int z) {
			volume++;
		}
	}

	public boolean contains(WorldT world, BlockPos position) {
		return contains(world, position.getX(), position.getY(), position.getZ());
	}

	public abstract <WorldT2 extends WorldT> void accept(AreaVisitor<? super WorldT2> visitor, WorldT2 world);

	public abstract boolean contains(WorldT world, int x, int y, int z);

	public int getVolume(WorldT world) {
		VolumeVisitor volumeVisitor = new VolumeVisitor();
		accept(volumeVisitor, world);
		return volumeVisitor.volume;
	}
	
	public BlockIntersection<WorldT> intersectWith(BlockArea<WorldT> other) {
		return new BlockIntersection<>(this, other);
	}

}
