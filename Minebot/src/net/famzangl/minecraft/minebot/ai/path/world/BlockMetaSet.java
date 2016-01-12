package net.famzangl.minecraft.minebot.ai.path.world;

import net.minecraft.block.Block;

/**
 * A set of blocks, filterable by meta.
 * 
 * @author Michael Zangl
 *
 */
public class BlockMetaSet extends BlockSet {

	private BlockMetaSet() {
	}

	public BlockMetaSet(Block block, int meta) {
		if ((meta & 0xf) != meta) {
			throw new IllegalArgumentException("Out of range: " + meta);
		}
		setBlockAndMeta(Block.getIdFromBlock(block), meta);
	}

	public BlockMetaSet(int... blockIdWithMetas) {
		for (int i : blockIdWithMetas) {
			setBlockAndMeta(i >> 4, i & 0xf);
		}
	}

	public BlockMetaSet unionWith(Block block, int meta) {
		return unionWith(new BlockMetaSet(block, meta));
	}

	protected int getSetLength() {
		return MAX_BLOCKIDS * 16 / 64;
	}

	private void setBlock(int blockId) {
		int bit = blockId * 16;
		set[bit / 64] |= 0xffffl << (63);
	}

	private void setBlockAndMeta(int blockId, int meta) {
		int bit = blockId * 16 + (meta & 0xf);
		long mask = 1l << (bit & 63);
		set[bit / 64] |= mask;
	}

	@Override
	public boolean containsAll(int blockId) {
		int bit = blockId * 16;
		long mask = 0xffffl << (bit & 63);
		long query = set[bit / 64];
		return (query & mask) == mask;
	}

	@Override
	public boolean containsAny(int blockId) {
		int bit = blockId * 16;
		long mask = 0xffffl << (bit & 63);
		long query = set[bit / 64];
		return (query & mask) != 0;
	}

	@Override
	public boolean containsWithMeta(int blockWithMeta) {
		int bit = blockWithMeta;
		long mask = 1l << (blockWithMeta & 63);
		long query = set[bit / 64];
		return (query & mask) == mask;
	}

	protected BlockSet compatibleSet(BlockSet bs1) {
		return bs1.convertToMetaSet();
	}

	BlockSet newSet() {
		return new BlockMetaSet();
	}

	@Override
	protected BlockSet convertToMetaSet() {
		return this;
	}

	@Override
	public BlockMetaSet unionWith(BlockSet bs2) {
		return (BlockMetaSet) super.unionWith(bs2);
	}

	@Override
	public BlockMetaSet intersectWith(BlockSet bs2) {
		return (BlockMetaSet) super.intersectWith(bs2);
	}

	@Override
	protected String getForBlock(int blockId) {
		if (containsAny(blockId)) {
			if (containsAll(blockId)) {
				// shorten it.
				return super.getForBlock(blockId);
			}

			StringBuilder b = new StringBuilder();
			b.append(Block.getBlockById(blockId).getLocalizedName());
			b.append(" (");
			boolean needsComma = false;
			for (int i = 0; i < 16; i++) {
				if (containsWithMeta((blockId << 4) + i)) {
					if (needsComma)
						b.append(", ");
					else
						needsComma = true;
					b.append(blockId);
					b.append(":");
					b.append(i);
				}
			}
			b.append(")");
			return b.toString();
		}
		return null;
	}

	public static BlockMetaSet fromBlockSet(BlockSet set) {
		BlockMetaSet converted = new BlockMetaSet();
		for (int blockId = 0; blockId < MAX_BLOCKIDS; blockId++) {
			if (set.containsAny(blockId)) {
				int bit = blockId * 16;
				long mask = 0xffffl << (bit & 63);
				converted.set[bit / 64] |= mask;
			}
		}
		return converted;
	}
}
