/*******************************************************************************
 * This file is part of Minebot.
 *
 * Minebot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Minebot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Minebot.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package net.famzangl.minecraft.minebot.ai.path.world;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;

/**
 * A set of blocks, identified by Id.
 * 
 * @author michael
 *
 */
public class BlockSet {

	public static int MAX_BLOCKIDS = 4096;

	private final long[] set = new long[MAX_BLOCKIDS / 64];

	public BlockSet(int... ids) {
		for (int i : ids) {
			setBlock(i);
		}
	}

	public BlockSet(Block... blocks) {
		for (Block b : blocks) {
			setBlock(Block.getIdFromBlock(b));
		}
	}

	private BlockSet() {
	}

	private void setBlock(int i) {
		set[i / 64] |= 1l << i;
		if (contains(9)) {
			System.out.println("Water for " + i);
		}
	}

	// private void clearBlock(int i) {
	// set[i / 64] &= ~(1 << (i & 63));
	// }

	public boolean contains(int blockId) {
		return (set[blockId / 64] & (1l << blockId)) != 0;
	}

	public boolean contains(Block block) {
		return contains(Block.getIdFromBlock(block));
	}

	public boolean contains(IBlockState meta) {
		return contains(meta.getBlock());
	}

	public BlockSet intersectWith(BlockSet wl2) {
		BlockSet res = new BlockSet();
		for (int i = 0; i < res.set.length; i++) {
			res.set[i] = set[i] & wl2.set[i];
		}
		return res;
	}

	public BlockSet unionWith(BlockSet wl2) {
		BlockSet res = new BlockSet();
		for (int i = 0; i < res.set.length; i++) {
			res.set[i] = set[i] | wl2.set[i];
		}
		return res;
	}

	public BlockSet invert() {
		BlockSet res = new BlockSet();
		for (int i = 0; i < res.set.length; i++) {
			res.set[i] = ~set[i];
		}
		return res;
	}

	/**
	 * Checks if one of these blocks is at the given position in the world.
	 * 
	 * @param world
	 *            The world.
	 * @param pos
	 *            The position in the world.
	 * @return
	 */
	public boolean isAt(WorldData world, BlockPos pos) {
		return contains(world.getBlockId(pos));
	}

	/**
	 * Checks if one of these blocks is at the given position in the world.
	 * 
	 * @param world
	 *            The world.
	 * @param x
	 *            The position in the world.
	 * @param y
	 *            The position in the world.
	 * @param z
	 *            The position in the world.
	 * @return
	 */
	public boolean isAt(WorldData world, int x, int y, int z) {
		return contains(world.getBlockId(x, y, z));
	}
	
	public List<BlockPos> findBlocks(WorldData world, BlockPos around, int radius) {
		ArrayList<BlockPos> pos = new ArrayList<BlockPos>();
		for (int x = around .getX() - radius; x <= around.getX() + radius; x++) {
			for (int z = around.getZ() - radius; z <= around.getZ() + radius; z++) {
				for (int y = around.getY() - radius; y <= around.getY() + radius; y++) {
					if (isAt(world, x, y, z)) {
						pos.add(new BlockPos(x, y, z));
					}
				}
			}
		}
		return pos;
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
		BlockSet other = (BlockSet) obj;
		if (!Arrays.equals(set, other.set))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("BlockWhitelist [");
		getBlockString(builder);
		builder.append("]");
		return builder.toString();
	}

	public void getBlockString(StringBuilder builder) {
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
	}
}
