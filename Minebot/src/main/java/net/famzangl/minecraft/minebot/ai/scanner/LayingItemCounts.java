package net.famzangl.minecraft.minebot.ai.scanner;

import net.famzangl.minecraft.minebot.ai.ItemFilter;
import net.famzangl.minecraft.minebot.ai.blockmap.BlockCubeCounter;
import net.famzangl.minecraft.minebot.ai.blockmap.ChunkCubeHashMap;
import net.famzangl.minecraft.minebot.ai.blockmap.ChunkCubeProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.List;

/**
 * This class counts the items that lay around. You can simply use 
 * @author michael
 *
 */
public class LayingItemCounts implements ChunkCubeProvider<BlockCubeCounter> {
	private ClientWorld itemWorld;
	private ChunkCubeHashMap<BlockCubeCounter> chunks = new ChunkCubeHashMap<BlockCubeCounter>(this);
	private ItemFilter filter;

	public LayingItemCounts(ClientWorld itemWorld, ItemFilter filter) {
		this.itemWorld = itemWorld;
		this.filter = filter;
	}

	@Override
	public BlockCubeCounter getForChunk(int chunkStartX, int chunkStartY,
			int chunkStartZ) {
		BlockCubeCounter counter = new BlockCubeCounter();
		List<ItemEntity> items = itemWorld.getEntitiesWithinAABB(
				ItemEntity.class, new AxisAlignedBB(chunkStartX, chunkStartY,
						chunkStartZ, chunkStartX + 16, chunkStartY + 16,
						chunkStartZ + 16));
		for (ItemEntity i : items) {
			ItemStack item = i.getItem();
			if (item == null) {
				continue;
			}
			if (filter.matches(item)) {
				BlockPos pos = i.getPosition();
				counter.increment(pos.getX(), pos.getY(), pos.getZ(), 1);
				if (pos.getY() > 0) {
					counter.increment(pos.getX(), pos.getY() - 1, pos.getZ(), 1);
				}
			}
		}
		return counter;
	}
	
	public int getItemsAt(int x, int y, int z) {
		return chunks.get(x, y, z).get(x, y, z);
	}
}
