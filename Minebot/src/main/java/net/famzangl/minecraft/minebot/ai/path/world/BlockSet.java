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
import java.util.Iterator;
import java.util.List;

import net.famzangl.minecraft.minebot.ai.command.BlockWithData;
import net.famzangl.minecraft.minebot.ai.command.BlockWithDataOrDontcare;
import net.famzangl.minecraft.minebot.ai.utils.BlockCuboid;
import net.famzangl.minecraft.minebot.ai.utils.BlockFilteredArea;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;

/**
 * A set of blocks, identified by Id.
 * 
 * @author Michael Zangl
 *
 */
public class BlockSet implements Iterable<BlockWithData> {

	// Should be approx. one cache line.
	public static int MAX_BLOCKIDS = 4096;

	protected final long[] set;

	public BlockSet(int... ids) {
		this();
		for (int i : ids) {
			setBlock(i);
		}
	}

	public BlockSet(Block... blocks) {
		this();
		for (Block b : blocks) {
			setBlock(Block.getIdFromBlock(b));
		}
	}

	BlockSet() {
		set = new long[getSetLength()];
	}

	protected int getSetLength() {
		return MAX_BLOCKIDS / 64;
	}

	private void setBlock(int i) {
		set[i / 64] |= 1l << (i & 63);
	}

	// private void clearBlock(int i) {
	// set[i / 64] &= ~(1 << (i & 63));
	// }

	private boolean contains(int blockId) {
		return containsWithMeta(blockId << 4);
	}

	public boolean containsAll(int blockId) {
		return contains(blockId);
	}

	public boolean containsAny(int blockId) {
		return contains(blockId);
	}

	public boolean contains(Block block) {
		return contains(Block.getIdFromBlock(block));
	}

	@Deprecated
	public boolean contains(IBlockState meta) {
		return contains(meta.getBlock());
	}

	public boolean containsWithMeta(int blockWithMeta) {
		int bit = blockWithMeta >> 4;
		long query = set[bit / 64];
		return (query & (1l << (bit & 63))) != 0;
	}

	public BlockSet intersectWith(BlockSet bs2) {
		BlockSet bs1 = bs2.compatibleSet(this);
		bs2 = bs1.compatibleSet(bs2);
		BlockSet res = bs1.newSet();
		for (int i = 0; i < res.set.length; i++) {
			res.set[i] = bs1.set[i] & bs2.set[i];
		}
		return res;
	}

	protected BlockSet compatibleSet(BlockSet bs1) {
		return bs1;
	}

	BlockSet newSet() {
		return new BlockSet();
	}

	protected BlockSet convertToMetaSet() {
		return BlockMetaSet.fromBlockSet(this);
	}

	public BlockSet unionWith(BlockSet bs2) {
		BlockSet bs1 = bs2.compatibleSet(this);
		bs2 = bs1.compatibleSet(bs2);
		BlockSet res = bs1.newSet();
		for (int i = 0; i < res.set.length; i++) {
			res.set[i] = bs1.set[i] | bs2.set[i];
		}
		return res;
	}

	public BlockSet invert() {
		BlockSet res = newSet();
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
		return containsWithMeta(world.getBlockIdWithMeta(pos));
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
		return containsWithMeta(world.getBlockIdWithMeta(x, y, z));
	}

	/**
	 * Checks if all of the given area in the world is filled with this block.
	 * 
	 * @param world
	 *            The world
	 * @param area
	 *            The area
	 * @return <code>true</code> if the area is filled with blocks of this set.
	 */
	public boolean isAt(WorldData world, BlockCuboid area) {
		return new BlockFilteredArea(area, this).getVolume(world) == area
				.getVolume(world);
	}

	@Deprecated
	public List<BlockPos> findBlocks(WorldData world, BlockPos around,
			int radius) {
		ArrayList<BlockPos> pos = new ArrayList<BlockPos>();
		// FIXME: Use areas for this.
		for (int x = around.getX() - radius; x <= around.getX() + radius; x++) {
			for (int z = around.getZ() - radius; z <= around.getZ() + radius; z++) {
				for (int y = around.getY() - radius; y <= around.getY()
						+ radius; y++) {
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
		builder.append("BlockSet [");
		getBlockString(builder);
		builder.append("]");
		return builder.toString();
	}

	public void getBlockString(StringBuilder builder) {
		boolean needsComma = false;
		for (int i = 0; i < MAX_BLOCKIDS; i++) {
			String str = getForBlock(i);
			if (str != null) {
				if (needsComma) {
					builder.append(", ");
				} else {
					needsComma = true;
				}
				builder.append(str);
			}
		}
	}

	protected String getForBlock(int blockId) {
		if (contains(blockId)) {
			return Block.getBlockById(blockId).getLocalizedName() + " ("
					+ blockId + ")";
		} else {
			return null;
		}
	}

	public boolean contains(BlockWithDataOrDontcare forBlock) {
		return forBlock.containedIn(this);
	}

	@Override
	public Iterator<BlockWithData> iterator() {
		return new Iterator<BlockWithData>() {
			int nextId = -1;

			@Override
			public boolean hasNext() {
				if (nextId < 0) {
					scanNext();
				}
				return nextId < MAX_BLOCKIDS * 16;
			}

			private void scanNext() {
				do {
					nextId++;
				} while (nextId < MAX_BLOCKIDS * 16
						&& !containsWithMeta(nextId));
			}

			@Override
			public BlockWithData next() {
				if (!hasNext()) {
					throw new IllegalStateException();
				}
				BlockWithData next = new BlockWithData(nextId);
				scanNext();
				return next;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
}
