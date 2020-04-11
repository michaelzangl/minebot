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
package net.famzangl.minecraft.minebot.ai.path;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.PathFinderField;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSets;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.SkeletonEntity;
import net.minecraft.entity.monster.SpiderEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.palette.PalettedContainer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Scanns the area around a given position for blocks. The scanner is
 * chunk-oriented.
 * <p>
 * Scanning order:
 * <p>
 * With {@link ScanChunkTask}: Scan the chunk (16x16x16) from Minecraft. (This
 * might need to be in sync with a game cycle. ??) This includes block state and
 * other stuff. <br>
 * Mark it for propagation in all directions.
 * <p>
 * With PropagateChunkTask: Check for incomming propagations. If there are
 * pending scans in neighbours, re-schedule as soon as they are complete (each
 * neighbour has a pending queue for this). Propagate blocks from each of the 6
 * directions. Propagate entities from each of the 6 directions. Then propagate
 * entities as long as there are changes (from all 6 directions in any order).
 * 
 * @author michael
 *
 */
public class MoveScanner {
	/**
	 * A 16 x 16 x 16 scanned cube.
	 * 
	 * @author michael
	 *
	 */
	public static class ScannedChunk implements Comparable<ScannedChunk> {
		/**
		 * When basic block scanning finished. This is just reading block and
		 * entity postions from the chunk.
		 */
		public final DependWaitEvent blockScanningDone = new DependWaitEvent();
		/**
		 * The #BLOCK_HAS_GOOD_SIDES_TEMP flag was set.
		 */
		public final DependWaitEvent sideTempScanningDone = new DependWaitEvent();
		/**
		 * The BLOCK_IS_SAFE_TO_GO flag was set.
		 */
		public final DependWaitEvent safeToGoScanningDone = new DependWaitEvent();
		public final ReadWriteLock blockLock = new ReentrantReadWriteLock();

		private final int blockOffset;
		/**
		 * Our local block flags.
		 */
		private final short[] blocks;
		private final byte[] dangerZone;
		private static final int DEFAULT_POSITION_FLAGS = 0;

		/**
		 * What we want to propagate to our neighbours. This can be: <li>The
		 * head-Flag
		 */
		private final int[] propagationFlags = new int[] { 0, 0, 0, 0, 0, 0 };
		private final int chunkX;
		private final int chunkY;
		private final int chunkZ;

		public ScannedChunk(int chunkX, int chunkY, int chunkZ, short[] blocks,
				byte[] dangerZone, int blockOffset) {
			this.chunkX = chunkX;
			this.chunkY = chunkY;
			this.chunkZ = chunkZ;
			this.blocks = blocks;
			this.dangerZone = dangerZone;
			this.blockOffset = blockOffset;
		}

		/**
		 * Does a scan of this chunk. Calling policy: You (only) need to hold a
		 * write lock of this chunk.
		 * 
		 * @param chunk
		 * @param chunkY
		 * @param policy
		 */
		public void scanBlocks(Chunk chunk, int chunkY, ScannerPolicy policy) {
			ChunkSection storage = chunk.getSections()[chunkY];
			
			PalettedContainer<BlockState> data = storage.getData();
//			char[] array = storage.getData();
			for (int y = 0; y < 16; y++) {
				for (int z = 0; z < 16; z++) {
					int oResult = local(0, y, z);
//					int oFrom = (y << 8 | z << 4);
					for (int x = 0; x < 16; x ++) {
//						int data = array[oFrom + x];
//						
//						blocks[oResult + x] = (short) (policy.getPositionFlags(data) | DEFAULT_POSITION_FLAGS);
						blocks[oResult + x] = (short) (policy.getPositionFlags(data.get(x, y, z)) | DEFAULT_POSITION_FLAGS);
					}
				}
			}

			@SuppressWarnings("unchecked")
			ClassInheritanceMultiMap entities = chunk.getEntityLists()[chunkY];
			for (Object o : entities) {
				Entity e = (Entity) o;
				int distance = policy.getDangerDistanceFor(e);
				if (distance > 0) {
					int lx = ((int) Math.floor(e.getPosX() + .5)) & 0xf;
					int ly = ((int) Math.floor(e.getPosY() + .5)) & 0xf;
					int lz = ((int) Math.floor(e.getPosZ() + .5)) & 0xf;
					int local = local(lx, ly, lz);
					if (dangerZone[local] < distance) {
						dangerZone[local] = (byte) distance;
					}
				}
			}
			blockScanningDone.enterState();
		}

		/**
		 * Requires read lock of fromDirection-chunk and write lock of this one.
		 * 
		 * @param fromDirection
		 */
		// public void propagateBlockChanges(Direction fromDirection,
		// short flagsToMerge, short flagToRevoke) {
		// for (int y = 0; y < 16; y++) {
		// if (local(0, y + fromDirection.offsetY, 0) < 0
		// || local(0, y + fromDirection.offsetY, 0) >= blocks.length) {
		// continue;
		// }
		// for (int z = 0; z < 16; z++) {
		// for (int x = 0; x < 16; x++) {
		// int from = local(x + fromDirection.offsetX, z
		// + fromDirection.offsetZ, z
		// + fromDirection.offsetZ);
		// int to = local(x, y, z);
		// if ((blocks[from] & flagsToMerge) != 0) {
		// blocks[to] |= BLOCK_IS_NOT_SAFE_TO_GO;
		// }
		// }
		// }
		// }
		// }

		/**
		 * Requires read lock for adjacent chunks.
		 */
		public void computeHasSafeSides() {
			for (int y = 0; y < 16; y++) {
				// if (y == 16 && chunkY == 16) {
				// // the top row is just not accessible.
				// continue;
				// }
				for (int z = 0; z < 16; z++) {
					for (int x = 0; x < 16; x++) {
						boolean safeSide = side(blocks[local(x + 1, y, z)])
								&& side(blocks[local(x - 1, y, z)])
								&& side(blocks[local(x, y, z + 1)])
								&& side(blocks[local(x, y, z - 1)]);
						if (safeSide) {
							blocks[local(x, y, z)] |= BLOCK_HAS_GOOD_SIDES_TEMP;
						} // TODO: Delete on second run.
					}
				}
			}

			sideTempScanningDone.enterState();
		}

		public void computeIsSafeToGo() {
			for (int y = 0; y < 16; y++) {
				if (y >= 15 && chunkY >= 15 || chunkY == 0 && y == 0) {
					// the top row is just not accessible.
					continue;
				}
				for (int z = 0; z < 16; z++) {
					for (int x = 0; x < 16; x++) {
						boolean safeToGo = safeSidesAndFoot(blocks[local(x, y,
								z)])
								&& safeSidesAndHead(blocks[local(x, y + 1, z)])
								&& safeGround(blocks[local(x, y - 1, z)])
								&& safeCeiling(blocks[local(x, y + 2, z)]);
						if (safeToGo) {
							blocks[local(x, y, z)] |= BLOCK_IS_SAFE_TO_GO;
						} // TODO: Delete on second run.
					}
				}
			}

			safeToGoScanningDone.enterState();
		}

		private boolean side(short flagSet) {
			return (flagSet & BLOCK_IS_SAFE_SIDE) == BLOCK_IS_SAFE_SIDE;
		}

		private boolean safeSidesAndHead(short flagSet) {
			return (flagSet & (BLOCK_IS_SAFE_SIDE | BLOCK_IS_SAFE_HEAD)) == (BLOCK_IS_SAFE_SIDE | BLOCK_IS_SAFE_HEAD);
		}

		private boolean safeSidesAndFoot(short flagSet) {
			return (flagSet & (BLOCK_IS_SAFE_SIDE | BLOCK_IS_SAFE_FOOT)) == (BLOCK_IS_SAFE_SIDE | BLOCK_IS_SAFE_FOOT);
		}

		private boolean safeGround(short flagSet) {
			return (flagSet & BLOCK_IS_SAFE_GROUND) != 0;
		}

		private boolean safeCeiling(short flagSet) {
			return (flagSet & BLOCK_IS_SAFE_HEAD) != 0;
		}

		public int getBlockData(int lx, int ly, int lz) {
			return this.blocks[local(lx, ly, lz)] & 255;

		}

		private int local(int lx, int ly, int lz) {
			return ly * STRIDE_Y + lz * STRIDE_Z + lx * STRIDE_X + blockOffset;
		}

		/**
		 * Locks one chunk for writing and the rest for reading. Might reorder
		 * the read lock array.
		 * 
		 * @param writeLock
		 * @param readLock
		 */
		public static void lock(ScannedChunk writeLock,
				ScannedChunk... readLock) {
			Arrays.sort(readLock);
			boolean writeLocked = false;
			for (int i = 0; i < readLock.length; i++) {
				if (!writeLocked && writeLock.compareTo(readLock[i]) < 0) {
					writeLock.blockLock.writeLock().lock();
					writeLocked = true;
				}
				readLock[i].blockLock.readLock().lock();
			}
			if (!writeLocked) {
				writeLock.blockLock.writeLock().lock();
			}
		}

		@Override
		public int compareTo(ScannedChunk o) {
			if (chunkY == o.chunkY) {
				if (chunkZ == o.chunkZ) {
					return chunkX - o.chunkX;
				} else {
					return chunkZ - o.chunkZ;
				}
			} else {
				return chunkY - o.chunkY;
			}
		}

		public static void unlock(ScannedChunk writeLock,
				ScannedChunk[] readLock) {
			for (int i = 0; i < readLock.length; i++) {
				readLock[i].blockLock.readLock().unlock();
			}
			writeLock.blockLock.writeLock().unlock();
		}
	}

	/**
	 * This is an dependend state for any task. It allows tasks to wait for the
	 * state and at the same time atomically lock the state so that the state
	 * may only be exited as soon as the task finished work.
	 * 
	 * @author michael
	 *
	 */
	public static class DependWaitEvent {

		private final boolean stateReached = false;
		/**
		 * Task list has two meanings:
		 * <p>
		 * If stateReached == false it is a list of tasks to notify when state
		 * reached.
		 * <p>
		 * if stateReached == true it is a list of tasks inside us.
		 */
		private final LinkedList<DependingTask> taskList = new LinkedList<DependingTask>();

		private final Object mutex = new Object();

		/**
		 * 
		 * @param task
		 * @return <code>true</code> if the lock is acquired, false if this task
		 *         was set on the wait queue to be notified.
		 */
		public boolean enterWithTask(DependingTask task) {
			synchronized (mutex) {
				taskList.add(task);
				if (stateReached) {
					return true;
				} else {
					return false;
				}
			}
		}

		/**
		 * Releases the lock held by this task. Ignored if it has no lock.
		 * 
		 * @param task
		 */
		public void exitWithTask(DependingTask task) {
			synchronized (mutex) {
				if (stateReached) {
					taskList.remove(task);
					mutex.notifyAll();
				}
			}
		}

		/**
		 * Notifies that the state was entered.
		 */
		public void enterState() {
			synchronized (mutex) {
				if (stateReached) {
					throw new IllegalStateException();
				}
				while (!taskList.isEmpty()) {
					DependingTask task = taskList.remove();
					task.dependencyReached();
				}
				mutex.notifyAll();
			}
		}

		/**
		 * Exits this state. Blocks until all tasks requireing this state are
		 * done.
		 */
		public void exitState() {
			synchronized (mutex) {
				if (!stateReached) {
					throw new IllegalStateException();
				}
				while (!taskList.isEmpty()) {
					try {
						mutex.wait();
					} catch (InterruptedException e) {
					}
				}
			}

		}

		public void waitForEvent() {
			synchronized (mutex) {
				while (!stateReached) {
					try {
						mutex.wait();
					} catch (InterruptedException e) {
					}
				}
			}
		}
	}

	/**
	 * A simple tasks that depends on a common goal.
	 * 
	 * @author michael
	 *
	 */
	public static abstract class DependingTask implements Runnable {
		private ThreadPoolExecutor threadPool;

		public List<DependWaitEvent> waitingOnEvents() {
			return Collections.emptyList();
		}

		@Override
		public final synchronized void run() {
			List<DependWaitEvent> depends = waitingOnEvents();
			try {
				for (DependWaitEvent e : depends) {
					if (!e.enterWithTask(this)) {
						// We are added to wait queue.
						return;
					}
				}
				doRun();
			} finally {
				for (DependWaitEvent e : depends) {
					e.exitWithTask(this);
				}
			}
		}

		public abstract void doRun();

		public void setThreadPoolToUse(ThreadPoolExecutor threadPool) {
			this.threadPool = threadPool;
		}

		public void dependencyReached() {
			threadPool.execute(this);
		}
	}

	public static class DependingTaskScheduler {
		private final ThreadPoolExecutor threadPool;

		public DependingTaskScheduler() {
			threadPool = new ThreadPoolExecutor(4, 20, 60, TimeUnit.SECONDS,
					new LinkedBlockingQueue<Runnable>());
		}

		public void addTask(DependingTask task) {
			task.setThreadPoolToUse(threadPool);
			threadPool.execute(task);
		}

		@Override
		protected void finalize() throws Throwable {
			threadPool.shutdown();
		}
	}

	/**
	 * Scan a chunk and produce a scanned chunk.
	 * 
	 * @author michael
	 *
	 */
	public static class ScanChunkTask extends DependingTask {

		private final ScannerPolicy policy;
		private final ScannedChunk chunk;
		private final Chunk mcChunk;

		public ScanChunkTask(ScannedChunk chunk, ScannerPolicy policy,
				Chunk mcChunk) {
			this.chunk = chunk;
			this.policy = policy;
			this.mcChunk = mcChunk;
		}

		@Override
		public void doRun() {
			Lock writeLock = chunk.blockLock.writeLock();
			writeLock.lock();
			try {
				chunk.scanBlocks(mcChunk, chunk.chunkY, policy);
			} finally {
				writeLock.unlock();
			}
		}
	}

	public class PropagateSidesOfChunkTask extends DependingTask {

		private final ScannedChunk chunk;

		/**
		 * 
		 * @param chunk
		 */
		public PropagateSidesOfChunkTask(ScannedChunk chunk) {
			this.chunk = chunk;
		}

		@Override
		public void doRun() {
			ScannedChunk[] readLocks = new ScannedChunk[] {
					getChunk(chunk.chunkX + 1, chunk.chunkY, chunk.chunkZ),
					getChunk(chunk.chunkX - 1, chunk.chunkY, chunk.chunkZ),
					getChunk(chunk.chunkX, chunk.chunkY, chunk.chunkZ + 1),
					getChunk(chunk.chunkX, chunk.chunkY, chunk.chunkZ - 1) };

			try {
				// accquire negative lock first.

				ScannedChunk.lock(chunk, readLocks);
				chunk.computeHasSafeSides();
			} finally {

				ScannedChunk.unlock(chunk, readLocks);
			}
		}
	}

	public class PropagateSafeToGoTask extends DependingTask {

		private final ScannedChunk chunk;

		/**
		 * 
		 * @param chunk
		 */
		public PropagateSafeToGoTask(ScannedChunk chunk) {
			this.chunk = chunk;
		}

		@Override
		public void doRun() {
			ScannedChunk up = getChunk(chunk.chunkX, chunk.chunkY + 1,
					chunk.chunkZ);
			ScannedChunk down = getChunk(chunk.chunkX, chunk.chunkY - 1,
					chunk.chunkZ);
			ScannedChunk[] readLocks;
			if (chunk.chunkY == 0) {
				readLocks = new ScannedChunk[] { up };
			} else if (chunk.chunkY >= 15) {
				readLocks = new ScannedChunk[] { down };
			} else {
				readLocks = new ScannedChunk[] { up, down };
			}

			try {
				// accquire negative lock first.

				ScannedChunk.lock(chunk, readLocks);
				chunk.computeIsSafeToGo();
			} finally {

				ScannedChunk.unlock(chunk, readLocks);
			}
		}
	}

	/**
	 * This tells a scanner what blocks to scan for.
	 * 
	 * @author michael
	 *
	 */
	public class ScannerPolicy {

		private final BlockSet safeCeiling = BlockSets.SAFE_CEILING;
		private final BlockSet safeHead = BlockSets.HEAD_CAN_WALK_THROUGH;
		private final BlockSet safeFoot = BlockSets.FEET_CAN_WALK_THROUGH;
		private final BlockSet safeFloor = BlockSets.SAFE_GROUND;
		private final BlockSet safeSide = BlockSets.SAFE_SIDE;

		/**
		 * Gets all flags (except safe_to_go) for the given block.
		 * 
		 * @param blockIndex
		 * @return A short (packed int an int).
		 */
		int getPositionFlags(int blockWithMeta) {
			int flag = 0;
			if (safeCeiling.contains(blockWithMeta)) {
				flag |= BLOCK_IS_SAFE_CEILING;
			}
			if (safeHead.contains(blockWithMeta)) {
				flag |= BLOCK_IS_SAFE_HEAD;
			}
			if (safeFoot.contains(blockWithMeta)) {
				flag |= BLOCK_IS_SAFE_FOOT;
			}
			if (safeFloor.contains(blockWithMeta)) {
				flag |= BLOCK_IS_SAFE_GROUND;
			}
			if (safeSide.contains(blockWithMeta)) {
				flag |= BLOCK_IS_SAFE_SIDE;
			}
			return flag;
		}

		public int getPositionFlags(BlockState iBlockState) {
			return Block.BLOCK_STATE_IDS.get(iBlockState);
		}

		public int getDangerDistanceFor(Entity e) {
			if (e instanceof SpiderEntity) {
				return 20;
			} else if (e instanceof SkeletonEntity) {
				return 30;
			} else if (e instanceof CreeperEntity) {
				return 20;
			} else if (e instanceof ZombieEntity) {
				return 20;
			} else {
				return 0;
			}
		}
	}

	/**
	 * How many chunks this scanner scans. Should be more than
	 * {@link PathFinderField#SIZE_X_Z}. The real available size is smaller by 1
	 * chunk in each direction.
	 */
	private final static int CHUNK_SIZE_X_Z = 22;
	private final static int CHUNK_SIZE_Y = 16;
	private final static ScannedChunk EMPTY_SCANNED_CHUNK = null;

	public static final int BLOCK_IS_SAFE_TO_GO = 0x100;
	public static final int BLOCK_IS_SAFE_SIDE = 0x200;
	public static final int BLOCK_IS_SAFE_GROUND = 0x400;
	public static final int BLOCK_IS_SAFE_CEILING = 0x800;
	public static final int BLOCK_IS_SAFE_FOOT = 0x1000;
	public static final int BLOCK_IS_SAFE_HEAD = 0x2000;
	// Airlike blocks that might be walked/shot through by entities.
	public static final int BLOCK_IS_AIRLIKE = 0x4000;
	// A temporary flag used to indicate that the head part (sides + ceiling +
	// head block) is safe.
	public static final int BLOCK_HAS_GOOD_SIDES_TEMP = 0x1000;

	private final int baseChunkX = 0;
	private final int baseChunkZ = 0;
	public static final int STRIDE_X = 1;
	public static final int STRIDE_Y = CHUNK_SIZE_X_Z * 16 * CHUNK_SIZE_Y * 16;
	public static final int STRIDE_Z = CHUNK_SIZE_X_Z * 16;

	private final DependingTaskScheduler scheduler = new DependingTaskScheduler();

	/**
	 * The cunk array.
	 */
	private final ScannedChunk[] chunks = new ScannedChunk[CHUNK_SIZE_X_Z
			* CHUNK_SIZE_X_Z * CHUNK_SIZE_Y];
	/**
	 * An array of block information. It is ordered by y/z/x. You can use
	 * STRIDE_Y, STRIDE_Z and STRIDE_X to get plus one in each direction.
	 * Locking is done by the {@link ScannedChunk} objects.
	 */
	private final short[] blocks = new short[chunks.length * 16 * 16 * 16];
	private final byte[] dangerZone = new byte[chunks.length * 16 * 16 * 16];

	private final ScannerPolicy policy = new ScannerPolicy();
	private final AIHelper helper;

	public MoveScanner(AIHelper helper) {
		this.helper = helper;
	}

	public void centerAround(int x, int z) {
		int newBaseChunkX = x >> 4;
		int newBaseChunkZ = z >> 4;
		if (newBaseChunkX != baseChunkX || newBaseChunkZ != baseChunkZ) {
			// TODO: safe old stuff.
			Arrays.fill(chunks, null);
		}
	}

	public ScannedChunk getChunk(int chunkX, int chunkY, int chunkZ) {
		if (chunkX < baseChunkX + 1
				|| chunkX >= baseChunkX + CHUNK_SIZE_X_Z - 1
				|| chunkZ < baseChunkZ + 1
				|| chunkZ >= baseChunkZ + CHUNK_SIZE_X_Z - 1 || chunkY < 0
				|| chunkY >= CHUNK_SIZE_Y) {
			return EMPTY_SCANNED_CHUNK;
		}
		synchronized (chunks) {
			int i = chunkIndex(chunkX, chunkY, chunkZ);
			if (chunks[i] == null) {
				chunks[i] = new ScannedChunk(chunkX, chunkY, chunkZ, blocks,
						dangerZone, chunkY * STRIDE_Y * 16
								+ (chunkZ % CHUNK_SIZE_X_Z) * STRIDE_Z * 16
								+ (chunkX % CHUNK_SIZE_X_Z) * STRIDE_X * 16);
				Chunk mcChunk = helper.getMinecraft().world
						.getChunk(chunkX, chunkZ);
				scheduler
						.addTask(new ScanChunkTask(chunks[i], policy, mcChunk));
				scheduler.addTask(new PropagateSidesOfChunkTask(chunks[i]));
				scheduler.addTask(new PropagateSafeToGoTask(chunks[i]));
			}
			chunks[i].safeToGoScanningDone.waitForEvent();
			return chunks[i];
		}
	}

	private int chunkIndex(int chunkX, int chunkY, int chunkZ) {
		return chunkY * CHUNK_SIZE_X_Z * CHUNK_SIZE_X_Z + chunkZ
				% CHUNK_SIZE_X_Z * CHUNK_SIZE_X_Z + chunkX % CHUNK_SIZE_X_Z;
	}
}
