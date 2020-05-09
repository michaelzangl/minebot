package net.famzangl.minecraft.minebot.ai.utils;

import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.famzangl.minecraft.minebot.ai.path.world.WorldData;

/**
 * This is an area that only contains specific blocks.
 * 
 * @author Michael Zangl
 *
 */
public class BlockFilteredArea<W extends WorldData> extends AbstractFilteredArea<W> {
	private final BlockSet containedBlocks;

	public BlockFilteredArea(BlockArea<W> base,
			BlockSet containedBlocks) {
		super(base);
		this.containedBlocks = containedBlocks;
	}

	@Override
	protected boolean test(W world, int x, int y, int z) {
		return containedBlocks.isAt(world, x, y, z);
	}
}
