package net.famzangl.minecraft.minebot.ai.utils;

import net.famzangl.minecraft.minebot.ai.path.world.WorldData;

/**
 * An area that is empty.
 * 
 * @author Michael Zangl
 *
 */
public class BlockAreaEmpty<W extends WorldData> extends BlockArea<W> {
	public static final BlockAreaEmpty INSTANCE = new BlockAreaEmpty();

	public BlockAreaEmpty() {
	}

	@Override
	public boolean contains(WorldData world, int x, int y, int z) {
		return false;
	}

	@Override
	public <WorldT2 extends W> void accept(AreaVisitor<? super WorldT2> visitor, WorldT2 world) {
		// nothing to visit
	}
}
