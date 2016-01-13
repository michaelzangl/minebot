package net.famzangl.minecraft.minebot.ai.path.world;

import java.util.Arrays;
import java.util.Hashtable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import net.famzangl.minecraft.minebot.settings.MinebotSettings;
import net.minecraft.block.Block;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

public class WorldWithDelta extends WorldData {
	private static final Marker MARKER_WORLD_DELTA = MarkerManager
			.getMarker("worlddata");
	private static final Logger LOGGER = LogManager
			.getLogger(WorldWithDelta.class);

	private Hashtable<Long, ChunkWithDelta> chunkDeltas = new Hashtable<Long, ChunkWithDelta>();

	private BlockPos playerPosition;

	private WorldData currentWorld;

	public static class ChunkWithDelta extends ChunkAccessor {
		private final WorldClient theWorld;
		private final int chunkX;
		private final int chunkZ;

		private static final char NOT_REPLACED = 0xffff;

		private char[][] replacedBlockIds = new char[0][];

		public ChunkWithDelta(WorldClient theWorld, int chunkX, int chunkZ) {
			super();
			this.theWorld = theWorld;
			this.chunkX = chunkX;
			this.chunkZ = chunkZ;
		}

		@Override
		public int getBlockIdWithMeta(final int x, final int y, final int z) {
			if (y >> 4 < replacedBlockIds.length) {
				char[] replacements = replacedBlockIds[y >> 4];
				if (replacements != null) {
					final int lx = x & 15;
					final int ly = y & 15;
					final int lz = z & 15;
					int replacementId = replacements[ly << 8 | lz << 4 | lx] & 0xffff;
					if (replacementId != NOT_REPLACED) {
						return replacementId;
					}
				}
			}

			if (blockStorage == null) {
				LOGGER.trace(MARKER_WORLD_DELTA,
						"Chunk delta fall through: load block storage for ("
								+ chunkX + "," + chunkZ + ")");
				blockStorage = theWorld.getChunkFromChunkCoords(chunkX, chunkZ)
						.getBlockStorageArray();
			}
			return super.getBlockIdWithMeta(x, y, z);
		}

		public void replaceBlock(int x, int y, int z, int blockWithMeta) {
			int chunkY = y >> 4;
			if (chunkY >= replacedBlockIds.length) {
				replacedBlockIds = (char[][]) Arrays.copyOf(replacedBlockIds,
						chunkY + 1);
			}
			char[] replacements = replacedBlockIds[chunkY];
			if (replacements == null) {
				replacements = new char[16 * 16 * 16];
				Arrays.fill(replacements, NOT_REPLACED);
				replacedBlockIds[chunkY] = replacements;
			}
			final int lx = x & 15;
			final int ly = y & 15;
			final int lz = z & 15;
			replacements[ly << 8 | lz << 4 | lx] = (char) blockWithMeta;
		}

		public void invalidateCache() {
			blockStorage = null;
		}
	}

	public WorldWithDelta(WorldData currentWorld) {
		super(currentWorld.theWorld, null);
		if (currentWorld instanceof WorldWithDelta) {
			throw new IllegalArgumentException(
					"Cannot make a delta of a delta.");
		}
		this.currentWorld = currentWorld;
		this.playerPosition = currentWorld.getPlayerPosition();
	}

	protected ChunkAccessor generateChunkAccessor(int chunkX, int chunkZ) {
		ChunkAccessor chunk;
		chunk = chunkDeltas.get(cachePosition(chunkX, chunkZ));
		if (chunk == null) {
			chunk = super.generateChunkAccessor(chunkX, chunkZ);
		}
		return chunk;
	}

	@Override
	public void invalidateChunkCache() {
		for (ChunkWithDelta v : chunkDeltas.values()) {
			v.invalidateCache();
		}

		super.invalidateChunkCache();
	}

	public void setBlock(BlockPos pos, Block block) {
		setBlock(pos, block.getIdFromBlock(block), 0);
	}

	public void setBlock(BlockPos pos, int blockId, int meta) {
		setBlock(pos.getX(), pos.getY(), pos.getZ(), blockId, meta);
	}

	public void setBlock(int x, int y, int z, int blockId, int meta) {
		if (blockId > 0xfff || meta > 0xf) {
			throw new IllegalArgumentException("block id/meta " + blockId + ":"
					+ meta + " out of range.");
		}
		setBlockIdAndMeta(x, y, z, blockId << 4 | meta);
	}

	private void setBlockIdAndMeta(int x, int y, int z, int blockWithMeta) {
		LOGGER.trace(MARKER_WORLD_DELTA, "Setblock at (" + x + "," + y + ","
				+ z + ") with " + (blockWithMeta >> 4) + ":"
				+ (blockWithMeta & 0xf));
		int chunkX = x >> 4;
		int chunkZ = z >> 4;

		long key = cachePosition(chunkX, chunkZ);
		ChunkWithDelta delta = chunkDeltas.get(key);
		if (delta == null) {
			delta = new ChunkWithDelta(theWorld, chunkX, chunkZ);
			chunkDeltas.put(key, delta);
		}
		delta.replaceBlock(x, y, z, blockWithMeta);
		invalidateChunkCache();
	}

	@Override
	public BlockPos getPlayerPosition() {
		return playerPosition;
	}

	@Override
	public Vec3 getExactPlayerPosition() {
		return new Vec3(playerPosition.getX() + .5, playerPosition.getY(),
				playerPosition.getZ() + .5);
	}

	public void setPlayerPosition(BlockPos playerPosition) {
		LOGGER.trace(MARKER_WORLD_DELTA, "Set player position: "
				+ playerPosition);
		this.playerPosition = playerPosition;
	}

	@Override
	public WorldData getCurrentState() {
		return currentWorld;
	}
}
