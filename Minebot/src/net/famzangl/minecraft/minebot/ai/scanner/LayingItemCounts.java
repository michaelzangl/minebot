package net.famzangl.minecraft.minebot.ai.scanner;

import java.util.List;

import net.famzangl.minecraft.minebot.ai.ItemFilter;
import net.famzangl.minecraft.minebot.ai.blockmap.BlockCubeCounter;
import net.famzangl.minecraft.minebot.ai.blockmap.ChunkCubeHashMap;
import net.famzangl.minecraft.minebot.ai.blockmap.ChunkCubeProvider;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

/**
 * This class counts the items that lay around. You can simply use 
 * @author michael
 *
 */
public class LayingItemCounts implements ChunkCubeProvider<BlockCubeCounter> {
	private WorldClient itemWorld;
	private ChunkCubeHashMap<BlockCubeCounter> chunks = new ChunkCubeHashMap<BlockCubeCounter>(this);
	private ItemFilter filter;

	public LayingItemCounts(WorldClient itemWorld, ItemFilter filter) {
		this.itemWorld = itemWorld;
		this.filter = filter;
	}

	@Override
	public BlockCubeCounter getForChunk(int chunkStartX, int chunkStartY,
			int chunkStartZ) {
		BlockCubeCounter counter = new BlockCubeCounter();
		List<EntityItem> items = itemWorld.getEntitiesWithinAABB(
				EntityItem.class, new AxisAlignedBB(chunkStartX, chunkStartY,
						chunkStartZ, chunkStartX + 16, chunkStartY + 16,
						chunkStartZ + 16));
		for (EntityItem i : items) {
			ItemStack item = i.getEntityItem();
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
