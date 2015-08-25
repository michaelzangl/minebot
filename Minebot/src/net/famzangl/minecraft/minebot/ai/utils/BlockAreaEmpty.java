package net.famzangl.minecraft.minebot.ai.utils;

import net.famzangl.minecraft.minebot.ai.path.world.WorldData;

/**
 * An area that is empty.
 * 
 * @author Michael Zangl
 *
 */
public class BlockAreaEmpty extends BlockArea {
	public static final BlockAreaEmpty INSTANCE = new BlockAreaEmpty();

	private BlockAreaEmpty() {
	}

	@Override
	public boolean contains(WorldData world, int x, int y, int z) {
		return false;
	}
	
	@Override
	public void accept(AreaVisitor v, WorldData world) {
		// nothing to visit.
	}
}
