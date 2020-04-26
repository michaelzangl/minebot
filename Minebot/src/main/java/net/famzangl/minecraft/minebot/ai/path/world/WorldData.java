package net.famzangl.minecraft.minebot.ai.path.world;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.WallSignBlock;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.BitArray;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.palette.PalettedContainer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.stream.Stream;

/**
 * This is a world that has some deltas attached to it. This may represent a
 * world state in the future.
 * 
 * @author Michael Zangl
 */
public class WorldData {
	private static final int BARRIER_ID = Block.getStateId(Blocks.BARRIER.getDefaultState());
	private static final int AIR_ID = Block.getStateId(Blocks.AIR.getDefaultState());
	private static final int CACHE_ENTRIES = 10;
	/**
	 * A cache pos that may never occur naturally.
	 */
	private static final int CACHE_INVALID = 0x10000000;
	private static final double FLOOR_HEIGHT = .55;

	private static class FastBlockStorageAccess {
		private static final int FORCED_SIZE = MathHelper.log2DeBruijn(Block.BLOCK_STATE_IDS.size());
		private final PalettedContainer<BlockState> data;
		
		private static final Field BITS_FIELD;
		private static final Field STORAGE_FIELD;
		private BitArray array;
		static {
			BITS_FIELD = Stream.of(PalettedContainer.class.getDeclaredFields()).filter(f -> f.getType() == Integer.TYPE).findFirst().get();
			BITS_FIELD.setAccessible(true);
			STORAGE_FIELD = Stream.of(PalettedContainer.class.getDeclaredFields()).filter(f -> f.getType() == BitArray.class).findFirst().get();
			STORAGE_FIELD.setAccessible(true);
		}

		public FastBlockStorageAccess(ChunkSection extendedBlockStorage) {
			data = extendedBlockStorage.getData();
			try {
				int bits = BITS_FIELD.getInt(data);
				if (bits != FORCED_SIZE) {
					// Don't care about memory. We care about speed
					data.onResize(FORCED_SIZE, Blocks.AIR.getDefaultState());
				}
				array = (BitArray) STORAGE_FIELD.get(data);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		public int get(int lx, int ly, int lz) {
			// Same as return Block.BLOCK_STATE_IDS.get(data.get(lx, ly, lz));
			int index = ly << 8 | lz << 4 | lx;
			return array.getAt(index);
		}
		
	}

	public static abstract class ChunkAccessor {
		protected ChunkSection[] blockStorage;
		private FastBlockStorageAccess[] access;

		public int getBlockIdWithMeta(int x, int y, int z) {
			int blockId = 0;
			if (y >> 4 < blockStorage.length) {
				if (access == null) {
					access = new FastBlockStorageAccess[blockStorage.length];
				}
				FastBlockStorageAccess myAccess = access[y >> 4];
				if (myAccess == null && blockStorage[y >> 4] != null) {
					myAccess = new FastBlockStorageAccess(blockStorage[y >> 4]);
					access[y >> 4] = myAccess;
				}
				if (myAccess != null) {
					final int lx = x & 15;
					final int ly = y & 15;
					final int lz = z & 15;
					blockId = myAccess.get(lx, ly, lz);
				}
			}

			return blockId;
		}
	}

	public static class ChunkAccessorUnmodified extends ChunkAccessor {

		public ChunkAccessorUnmodified(Chunk chunk) {
			blockStorage = chunk.getSections();
		}
	}

	private final long[] cachedPos = new long[CACHE_ENTRIES];
	private final ChunkAccessor[] cached = new ChunkAccessor[CACHE_ENTRIES];

	private int chunkCacheReplaceCounter = 0;

	protected final ClientWorld theWorld;
	private final ClientPlayerEntity thePlayerToGetPositionFrom;

	public WorldData(ClientWorld theWorld,
					 ClientPlayerEntity thePlayerToGetPositionFrom) {
		this.theWorld = theWorld;
		this.thePlayerToGetPositionFrom = thePlayerToGetPositionFrom;
	}

	/**
	 * A fast method that gets a block id for a given position. Use it if you
	 * need to scan many blocks, since the default minecraft block lookup is
	 * slow. For x<0 or x>= 256 it returns bedrock, to make routing easy.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public int getBlockStateId(int x, int y, int z) {
		if (y < 0 || y >= 258) {
			return BARRIER_ID;
		} else if (y >= 256) {
			return AIR_ID;
		}
		ChunkAccessor a = getChunkAccessor(x, z);

		return a == null ? BARRIER_ID : a.getBlockIdWithMeta(x, y, z);
	}

	private ChunkAccessor getChunkAccessor(int x, int z) {
		int chunkX = x >> 4;
		int chunkZ = z >> 4;
		ChunkAccessor chunk = null;
		long posForCache = cachePosition(chunkX, chunkZ);

		for (int i = 0; i < CACHE_ENTRIES; i++) {
			if (cachedPos[i] == posForCache) {
				return cached[i];
			}
		}

		chunk = generateChunkAccessor(chunkX, chunkZ);

		cachedPos[chunkCacheReplaceCounter] = posForCache;
		cached[chunkCacheReplaceCounter] = chunk;

		chunkCacheReplaceCounter++;
		if (chunkCacheReplaceCounter >= CACHE_ENTRIES) {
			chunkCacheReplaceCounter = 0;
		}
		return chunk;
	}

	protected ChunkAccessor generateChunkAccessor(int chunkX, int chunkZ) {
		return new ChunkAccessorUnmodified(theWorld.getChunk(
				chunkX, chunkZ));
	}

	/**
	 * A unique long for each chunk.
	 * 
	 * @param chunkX
	 * @param chunkZ
	 * @return
	 */
	protected long cachePosition(int chunkX, int chunkZ) {
		return (long) chunkX << 32 | (chunkZ & 0xffffffffl);
	}

	public void invalidateChunkCache() {
		for (int i = 0; i < CACHE_ENTRIES; i++) {
			// chunk coord would be invalid.
			cachedPos[i] = CACHE_INVALID;
		}
	}

	public ClientWorld getBackingWorld() {
		return theWorld;
	}

	public int getBlockStateId(BlockPos pos) {
		return getBlockStateId(pos.getX(), pos.getY(), pos.getZ());
	}

	public BlockState getBlockState(BlockPos pos) {
		return Block.getStateById(getBlockStateId(pos));
	}

	public boolean isSideTorch(BlockPos pos) {
		int id = getBlockStateId(pos);
		return BlockSets.WALL_TORCHES.contains(id);
	}

	public BlockPos getHangingOnBlock(BlockPos pos) {
		BlockState meta = getBlockState(pos);
		Direction facing = null;
		if (BlockSets.TORCH.contains(meta)) {
			facing = getTorchDirection(meta);
		} else if (BlockSets.WALL_SIGN.contains(meta)) {
			facing = getSignDirection(meta);
			// TODO Ladder and other hanging blocks.
		} else if (BlockSets.FEET_CAN_WALK_THROUGH.contains(meta)) {
			facing = Direction.UP;
		}
		return facing == null ? null : pos.offset(facing, -1);
	}

	/**
	 * Get the sign/ladder/dispenser/dropper direction
	 * 
	 * @param metaValue
	 * @return
	 */
	private Direction getSignDirection(BlockState metaValue) {
		return (Direction) metaValue.get(WallSignBlock.FACING);
	}

	/**
	 * The direction the torch is facing. Floor would be up. Works for floor and wall torches, readstone and normal.
	 * 
	 * @param metaValue The block state
	 * @return The direction
	 */
	@Nonnull
	public Direction getTorchDirection(BlockState metaValue) {
		if (BlockSets.TORCH.contains(metaValue)) {
			return Direction.UP;
		} else if (BlockSets.WALL_TORCHES.contains(metaValue)) {
			return metaValue.get(HorizontalBlock.HORIZONTAL_FACING);
		} else {
		 throw new IllegalArgumentException("Not a torch meta: " + metaValue);
		}
	}

	/**
	 * Gets the grid player position. A player is always referenced by the foot
	 * block for this bot. The block below the player is the floor block (or
	 * ground block). A slab below the player is still considered ground, so the
	 * floor block is the block the player above that slab. Flat blocks or
	 * blocks the player can walk through are not considered ground, so they are
	 * the floor block instead.
	 * 
	 * @return
	 */
	public BlockPos getPlayerPosition() {
		final int x = (int) Math.floor(thePlayerToGetPositionFrom.getPosX());
		final int y = (int) Math.floor(thePlayerToGetPositionFrom
				.getBoundingBox().minY + FLOOR_HEIGHT);
		final int z = (int) Math.floor(thePlayerToGetPositionFrom.getPosZ());
		return new BlockPos(x, y, z);
	}

	/**
	 * @return Feet of the player
	 */
	public Vec3d getExactPlayerPosition() {
		return new Vec3d(thePlayerToGetPositionFrom.getPosX(),
				thePlayerToGetPositionFrom.getBoundingBox().minY,
				thePlayerToGetPositionFrom.getPosZ());
	}

	public BlockBounds getBlockBounds(BlockPos pos) {
		return getBlockBounds(pos.getX(), pos.getY(), pos.getZ());
	}

	public BlockBounds getBlockBounds(int x, int y, int z) {
		BlockBounds res = BlockBounds.forBlockWithMeta(getBlockStateId(x, y,
				z));
		if (res == BlockBounds.UNKNOWN_BLOCK) {
			// TODO: Replace this.
			ClientWorld world = getBackingWorld();
			BlockPos pos = new BlockPos(x, y, z);
			BlockState state = world.getBlockState(pos);
			VoxelShape bounds = state.getShape(world, pos);

			return new BlockBounds(bounds);
		}
		return res;
	}

	/**
	 * Returns the current state of the world if this world is a state in the
	 * future.
	 * 
	 * @return A world.
	 */
	public WorldData getCurrentState() {
		return this;
	}

	public long getWorldTime() {
		return theWorld.getGameTime();
	}

}