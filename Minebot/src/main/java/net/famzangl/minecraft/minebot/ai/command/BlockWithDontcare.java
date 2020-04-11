package net.famzangl.minecraft.minebot.ai.command;

import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.famzangl.minecraft.minebot.ai.task.inventory.ItemWithSubtype;
import net.minecraft.block.Block;

/**
 * This is a special {@link BlockWithData}. This is used whenever the data value
 * does not matter.
 * 
 * @author Michael Zangl
 *
 */
public class BlockWithDontcare extends BlockWithDataOrDontcare {
	public BlockWithDontcare(int blockId) {
		super(BlockWithData.toBlockWithMeta(blockId, 0));
	}
	
	public BlockWithDontcare(Block block) {
		this(Block.getIdFromBlock(block));
	}

	@Override
	public String toBlockString() {
		return getBlockString() + ":*";
	}
	
	@Override
	public BlockSet toBlockSet() {
		return new BlockSet(getBlockId());
	}
	
	@Override
	public boolean containedIn(BlockSet blockSet) {
		return blockSet.containsAll(getBlockId());
	}

	public ItemWithSubtype getItemType() {
		return new ItemWithSubtype(getBlockId(), -1);
	}
}
