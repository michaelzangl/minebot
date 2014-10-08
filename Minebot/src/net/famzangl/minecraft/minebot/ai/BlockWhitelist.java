package net.famzangl.minecraft.minebot.ai;

import java.util.Arrays;

import net.minecraft.block.Block;

/**
 * A set of blocks, identified by Id.
 * @author michael
 *
 */
public class BlockWhitelist {

	//private final BigInteger set;
	
	public static int MAX_BLOCKIDS = 4096;
	
	private final long[] set = new long[MAX_BLOCKIDS / 64];
	
	
	public BlockWhitelist(int... ids) {
		for (int i : ids) {
			setBlock(i);
		}
	}

	public BlockWhitelist(Block... blocks) {
		for (Block b : blocks) {
			setBlock(Block.getIdFromBlock(b));
		}
	}
	
	private BlockWhitelist() {
	}
	
	private void setBlock(int i) {
		set[i / 64] |= 1l << i;
		if (contains(9)) {
			System.out.println("Water for " + i);
		}
	}
	
//	private void clearBlock(int i) {
//		set[i / 64] &= ~(1 << (i & 63));
//	}
	
	public boolean contains(int blockId) {
		return (set[blockId / 64] & (1l << blockId)) != 0;
	}
	
	public boolean contains(Block block) {
		return contains(Block.getIdFromBlock(block));
	}
	
	public BlockWhitelist intersectWith(BlockWhitelist wl2) {
		BlockWhitelist res = new BlockWhitelist();
		for (int i = 0; i < res.set.length; i++) {
			res.set[i] = set[i] & wl2.set[i];
		}
		return res;
	}
	
	public BlockWhitelist unionWith(BlockWhitelist wl2) {
		BlockWhitelist res = new BlockWhitelist();
		for (int i = 0; i < res.set.length; i++) {
			res.set[i] = set[i] | wl2.set[i];
		}
		return res;
	}

	public BlockWhitelist invert() {
		BlockWhitelist res = new BlockWhitelist();
		for (int i = 0; i < res.set.length; i++) {
			res.set[i] = ~set[i];
		}
		return res;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(set);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BlockWhitelist other = (BlockWhitelist) obj;
		if (!Arrays.equals(set, other.set))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("BlockWhitelist [");
		boolean needsComma = false;
		for (int i = 0; i < MAX_BLOCKIDS; i++) {
			if (contains(i)) {
				if (needsComma) {
					builder.append(", ");
				} else {
					needsComma = true;
				}
				builder.append(Block.getBlockById(i).getLocalizedName());
				builder.append(" (");
				builder.append(i);
				builder.append(")");
			}
		}
		builder.append("]");
		return builder.toString();
	}
	
	
}
