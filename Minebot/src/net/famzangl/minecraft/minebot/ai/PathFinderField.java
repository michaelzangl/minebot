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
package net.famzangl.minecraft.minebot.ai;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;

import net.minecraft.util.BlockPos;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

/**
 * The basic path finding algorithm.
 * 
 * @author michael
 * 
 */
public class PathFinderField implements Comparator<Integer> {
	private static final Marker MARKER_PATH = MarkerManager.getMarker("path");
	private static final Logger LOGGER = LogManager.getLogger(AIHelper.class);

	private static final long MAX_RUN_TIME = 200;
	// Power of 2!
	private static final int Y_LEVEL = 32;
	private static int SIZE_X_Z = 256;
	private static int FIELD_SIZE = (1 << 16) * Y_LEVEL;
	// Needs to be more than normal distance spread. Power of 2.
	private static int FAST_DISTANCE_ACCESS = 64;

	private final PathFinderFieldData data = new PathFinderFieldData();
	private Dest currentDest = null;
	// Priority queue for everything that is more than max distance spread.
	private final PriorityQueue<Integer> pq = new PriorityQueue<Integer>(100,
			this);

	private final int[][] pqByDistance = new int[FAST_DISTANCE_ACCESS][256];
	private final int[] pqByDistanceFill = new int[FAST_DISTANCE_ACCESS];
	int pqMinDistance = -1;

	// private int[] pqArray = new int[256];
	// private int pqSize = 0;

	private static int FIELD_VISITED_MASK = 0x10000000;
	private static int FIELD_IN_QUEUE_MASK = 0x20000000;
	private static int FIELD_DISTANCE_SET_MASK = 0x40000000;
	private static int FIELD_DISTANCE_MASK = 0x0000ffff;
	private static int FIELD_DISTANCE_SHIFT = 0;
	private static int FIELD_MOVEFROM_X_MASK = 0x000f0000;
	private static int FIELD_MOVEFROM_X_SHIFT = 16;
	private static int FIELD_MOVEFROM_Y_MASK = 0x00f00000;
	private static int FIELD_MOVEFROM_Y_SHIFT = 20;
	private static int FIELD_MOVEFROM_Z_MASK = 0x0f000000;
	private static int FIELD_MOVEFROM_Z_SHIFT = 24;

	private boolean isRunning = false;
	/**
	 * Stored:
	 * 
	 * 
	 */
	private int[] field;
	private long startTime;

	public PathFinderField() {
	}

	protected final int getIndexForBlock(int x, int y, int z) {
		return x - data.offsetX & SIZE_X_Z - 1
				| (z - data.offsetZ & SIZE_X_Z - 1) << 8
				| (y - data.offsetY & Y_LEVEL - 1) << 16;
	}

	protected final int getIndexForBlockSafe(int x, int y, int z) {
		int dx = x - data.offsetX;
		int dz = z - data.offsetZ;
		int dy = y - data.offsetY;
		return dx == (dx & SIZE_X_Z - 1) && dz == (dz & SIZE_X_Z - 1) && dy == (dy & Y_LEVEL - 1) ? dx | (dz << 8) | (dy << 16) : -1;
	}

	protected final int getX(int currentNode) {
		return (currentNode & SIZE_X_Z - 1) + data.offsetX;
	}

	protected final int getY(int currentNode) {
		return (currentNode >> 16 & Y_LEVEL - 1) + data.offsetY;
	}

	protected final int getZ(int currentNode) {
		return (currentNode >> 8 & SIZE_X_Z - 1) + data.offsetZ;
	}

	private boolean isVisited(int blockIndex) {
		return (field[blockIndex] & FIELD_VISITED_MASK) == FIELD_VISITED_MASK;
	}

	private void setVisited(int blockIndex) {
		field[blockIndex] |= FIELD_VISITED_MASK;
	}

	private boolean isInQueue(int blockIndex) {
		return (field[blockIndex] & FIELD_IN_QUEUE_MASK) != 0;
	}

	private void setInQueue(int blockIndex) {
		field[blockIndex] |= FIELD_IN_QUEUE_MASK;
	}

	private int getDistance(int blockIndex) {
		int inField = field[blockIndex];
		return (inField & FIELD_DISTANCE_SET_MASK) == 0 ? Integer.MAX_VALUE
				: (inField & FIELD_DISTANCE_MASK) >> FIELD_DISTANCE_SHIFT;
	}

	private void setDistance(int blockIndex, int distance) {
		field[blockIndex] &= ~FIELD_DISTANCE_MASK;
		field[blockIndex] |= distance << FIELD_DISTANCE_SHIFT
				& FIELD_DISTANCE_MASK;
		field[blockIndex] |= FIELD_DISTANCE_SET_MASK;
	}

	private void setMoveFrom(int newIndex, int currentNode) {
		final int newx = getX(newIndex);
		final int newy = getY(newIndex);
		final int newz = getZ(newIndex);
		final int oldx = getX(currentNode);
		final int oldy = getY(currentNode);
		final int oldz = getZ(currentNode);
		field[newIndex] &= ~(FIELD_MOVEFROM_X_MASK | FIELD_MOVEFROM_Y_MASK | FIELD_MOVEFROM_Z_MASK);
		field[newIndex] |= newx - oldx << FIELD_MOVEFROM_X_SHIFT
				& FIELD_MOVEFROM_X_MASK;
		field[newIndex] |= newy - oldy << FIELD_MOVEFROM_Y_SHIFT
				& FIELD_MOVEFROM_Y_MASK;
		field[newIndex] |= newz - oldz << FIELD_MOVEFROM_Z_SHIFT
				& FIELD_MOVEFROM_Z_MASK;
	}

	private int getFromDirectionMasked(int blockIndex, int mask, int shift) {
		int res = (field[blockIndex] & mask) >> shift;
		final int signBit = (mask >> shift) + 1 >> 1;
		if ((res & signBit) != 0) {
			res |= ~(mask >> shift);
		}
		return res;
	}

	private int getFromDirectionX(int blockIndex) {
		return getFromDirectionMasked(blockIndex, FIELD_MOVEFROM_X_MASK,
				FIELD_MOVEFROM_X_SHIFT);
	}

	private int getFromDirectionY(int blockIndex) {
		return getFromDirectionMasked(blockIndex, FIELD_MOVEFROM_Y_MASK,
				FIELD_MOVEFROM_Y_SHIFT);
	}

	private int getFromDirectionZ(int blockIndex) {
		return getFromDirectionMasked(blockIndex, FIELD_MOVEFROM_Z_MASK,
				FIELD_MOVEFROM_Z_SHIFT);
	}

	@Override
	public int compare(Integer o1, Integer o2) {
		int c1 = getDistance(o1) - getDistance(o2);
		// if (c1 == 0) {
		// return o1 - o2;
		// } else {
		return c1;
		// }
	}

	private static class Dest implements Comparable<Dest> {
		int destNode;
		float destDistanceRating;

		public Dest(int destNode, float rating) {
			super();
			this.destNode = destNode;
			this.destDistanceRating = rating;
		}

		@Override
		public int compareTo(Dest o) {
			return Float.compare(destDistanceRating, o.destDistanceRating);
		}

	}

	protected boolean searchSomethingAround(int cx, int cy, int cz) {
		if (data.offsetX != cx - SIZE_X_Z / 2
				|| data.offsetY != cy - Y_LEVEL / 2
				|| data.offsetZ != cz - SIZE_X_Z / 2) {
			LOGGER.debug(MARKER_PATH,
					"Path finding center changed. Marking for restart.");
			isRunning = false;
		}
		if (!isRunning) {
			LOGGER.debug(MARKER_PATH, "Restart path finder.");
			field = new int[FIELD_SIZE];
			data.offsetX = cx - SIZE_X_Z / 2;
			data.offsetY = cy - Y_LEVEL / 2;
			data.offsetZ = cz - SIZE_X_Z / 2;
			pqClear();
			final int start = getIndexForBlock(cx, cy, cz);
			setDistance(start, 1);
			setInQueue(start);
			pqAdd(start, 1);
			final float startRating = rateDestination(start);
			if (startRating >= 0) {
				currentDest = new Dest(start, startRating);
			} else {
				currentDest = null;
			}
			isRunning = true;
		}
		LOGGER.trace(MARKER_PATH, "Start path finding.");
		startTime = System.currentTimeMillis();
		long iteration = 0;
		while (!pqEmpty()
				&& ((iteration++ & 0xff) == 0 || hasTimeLeft(startTime))) {
			final int currentNode = pqPoll();
			final int currentDistance = getDistance(currentNode);
			if (currentDest != null
					&& currentDistance + 1 > currentDest.destDistanceRating) {
				pqClear();
				break;
			}
			final float rating = rateDestination(currentNode);
			if (rating >= 0) {
				final Dest newDest = new Dest(currentNode, rating);
				if (currentDest == null || newDest.compareTo(currentDest) < 0) {
					currentDest = newDest;
				}
			}

			final int[] neighbours = getNeighbours(currentNode);
			for (final int n : neighbours) {
				if (n < 0) {
					continue;
				}
				final int distance = distanceFor(currentNode, n)
						+ currentDistance;
				if (isVisited(n)) {
					if (distance < getDistance(n)) {
						LOGGER.error(
								MARKER_PATH,
								"A shorter path was found. THis should not happen. Node: %d.",
								n);
					}
					continue;
				}
				if (!isInQueue(n)) {
					setDistance(n, distance);
					setMoveFrom(n, currentNode);
					setInQueue(n);
					pqAdd(n, distance);
				} else {
					int oldDistance = getDistance(n);
					if (distance < oldDistance) {
						setDistance(n, distance);
						setMoveFrom(n, currentNode);
						pqUpdate(n, oldDistance, distance);
					}
				}
			}
			setVisited(currentNode);
		}
		if (pqEmpty()) {
			LOGGER.debug(MARKER_PATH, "Path found to " + currentDest);
			if (currentDest != null) {
				planPathTo(currentDest.destNode, cx, cy, cz);
				terminated();
			} else {
				terminated();
				noPathFound();
			}
			return true;
		} else {
			LOGGER.debug(
					MARKER_PATH,
					"Warning: Path finding needs more time. Just got " + iteration + " iterations.");
			pqStats();

			return false;
		}
	}

	private void pqStats() {
		int min = Integer.MAX_VALUE;
		int max = 0;
		for (Integer i : pq) {
			min = Math.min(min, getDistance(i));
			max = Math.max(max, getDistance(i));
		}
		LOGGER.debug(MARKER_PATH, "Current priority queue range: " + min + " to " + max);
	}

	private boolean hasTimeLeft(long startTime) {
		return startTime + MAX_RUN_TIME > System.currentTimeMillis();
	}

	private void terminated() {
		isRunning = false;
		field = null;
		pqClear();
		currentDest = null;
	}

	public void abort() {
		terminated();
	}

	protected int distanceFor(int from, int to) {
		return 1;
	}

	protected void noPathFound() {
		LOGGER.info(MARKER_PATH, "Path finder did not find a path.");
	}

	private void planPathTo(int currentNode, int origX, int origY, int origZ) {
		LOGGER.debug(MARKER_PATH, "Reconstructing path.");
		int cx = getX(currentNode);
		int cy = getY(currentNode);
		int cz = getZ(currentNode);
		final LinkedList<BlockPos> path = new LinkedList<BlockPos>();
		while (cx != origX || cy != origY || cz != origZ) {
			path.addFirst(new BlockPos(cx, cy, cz));
			final int current = getIndexForBlock(cx, cy, cz);
			debug(current);
			cx -= getFromDirectionX(current);
			cy -= getFromDirectionY(current);
			cz -= getFromDirectionZ(current);
		}
		path.addFirst(new BlockPos(origX, origY, origZ));
		foundPath(path);
	}

	protected void foundPath(LinkedList<BlockPos> path) {
		LOGGER.debug(MARKER_PATH, "Path was found.");
		for (final BlockPos p : path) {
			LOGGER.trace(MARKER_PATH, "Path part: " + p);
		}
	}

	// The smaller the better. -1 for no dest.
	private float rateDestination(int currentNode) {
		final int cx = getX(currentNode);
		final int cy = getY(currentNode);
		final int cz = getZ(currentNode);
		final int distance = getDistance(currentNode);
		return rateDestination(distance, cx, cy, cz);
	}

	/**
	 * Rate a destination place. Negative number means it is no destination. The
	 * smaller the better.
	 * 
	 * @param distance
	 * @param x
	 * @param y
	 * @param z
	 * @return At least the real distance to the block.
	 */
	protected float rateDestination(int distance, int x, int y, int z) {
		return distance;
	}

	private final int[] res = new int[10];

	protected int[] getNeighbours(int currentNode) {
		final int cx = getX(currentNode);
		final int cz = getZ(currentNode);
		final int cy = getY(currentNode);
		res[0] = getNeighbour(currentNode, cx, cy + 1, cz);
		res[1] = getNeighbour(currentNode, cx, cy - 1, cz);
		res[2] = getNeighbour(currentNode, cx + 1, cy, cz);
		res[3] = getNeighbour(currentNode, cx + 1, cy - 1, cz);
		res[4] = getNeighbour(currentNode, cx - 1, cy, cz);
		res[5] = getNeighbour(currentNode, cx - 1, cy - 1, cz);
		res[6] = getNeighbour(currentNode, cx, cy, cz + 1);
		res[7] = getNeighbour(currentNode, cx, cy - 1, cz + 1);
		res[8] = getNeighbour(currentNode, cx, cy, cz - 1);
		res[9] = getNeighbour(currentNode, cx, cy - 1, cz - 1);
		return res;
	}

	protected int getNeighbour(int currentNode, int cx, int cy, int cz) {
		return cy > 1 && cy < 256 && cx > data.offsetX
				&& cx < data.offsetX + 256 && cy > data.offsetY
				&& cy < data.offsetY + Y_LEVEL && cz > data.offsetZ
				&& cz < data.offsetZ + 256 ? getIndexForBlock(cx, cy, cz) : -1;
	}

	private void debug(int nodeId) {
		LOGGER.trace(
				MARKER_PATH,
				"pos=" + new BlockPos(getX(nodeId), getY(nodeId), getZ(nodeId))
						+ ", inQueue=" + isInQueue(nodeId) + ", visited="
						+ isVisited(nodeId) + ", distance="
						+ getDistance(nodeId) + ", fromX="
						+ getFromDirectionX(nodeId) + ", fromY="
						+ getFromDirectionY(nodeId) + ", fromZ="
						+ getFromDirectionZ(nodeId) + ", data="
						+ Integer.toHexString(field[nodeId]));
	}

	private void pqClear() {
		// pqSize = 0;
		pq.clear();
		pqMinDistance = -1;
		Arrays.fill(pqByDistanceFill, 0);
	}

	private boolean pqEmpty() {
		// return pqSize == 0;
		return pq.isEmpty() && pqMinDistance < 0;
	}

	private void pqUpdate(int n, int oldDistance, int distance) {
		pqRemove(n, oldDistance);
		pqAdd(n, distance);
	}

	private void pqRemove(int n, int oldDistance) {
		if (pqMinDistance >= 0
				&& oldDistance < pqMinDistance + FAST_DISTANCE_ACCESS) {
			int slot = pqSlotFor(oldDistance);
			int[] slotArray = pqByDistance[slot];
			int fill = pqByDistanceFill[slot];
			pqByDistanceFill[slot] = fill - 1;
			boolean found = false;
			for (int i = 0; i < fill - 1; i++) {
				if (slotArray[i] == n) {
					slotArray[i] = slotArray[fill - 1];
					found = true;
					break;
				}
			}
			if (!found && slotArray[fill - 1] != n) {
				LOGGER.error(MARKER_PATH, "Warning: Trying to remove " + n
						+ " which is not in this list (d=" + oldDistance + ")");
				// search it in other lists...
				for (int list = 0; list < FAST_DISTANCE_ACCESS; list++) {
					int distance = list + pqMinDistance;
					for (int i = 0; i < pqByDistanceFill[pqSlotFor(distance)]; i++) {
						if (pqByDistance[pqSlotFor(distance)][i] == n) {
							LOGGER.debug(MARKER_PATH, "    -> Found it for d="
									+ distance);
						}
					}
				}
			}

			if (fill == 1 && oldDistance == pqMinDistance) {
				pqFindNextMin();
			}
		} else {
			pq.remove(n);
		}
	}

	private void pqAdd(int node, int distance) {
		if (getDistance(node) != distance) {
			throw new IllegalArgumentException("Got: " + distance
					+ " but real distance is " + getDistance(node));
		}
		if (pqMinDistance < 0) {
			pqMinDistance = distance;
		}
		while (distance < pqMinDistance) {
			// Does not happen often, but we need to handle it.
			pqMinDistance--;

			int slotToEmpty = pqSlotFor(pqMinDistance);
			for (int i = 0; i < pqByDistanceFill[slotToEmpty]; i++) {
				pq.offer(pqByDistance[slotToEmpty][i]);
			}
			pqByDistanceFill[slotToEmpty] = 0;
		}

		if (distance < pqMinDistance + FAST_DISTANCE_ACCESS) {
			int slot = pqSlotFor(distance);
			int[] slotArray = pqByDistance[slot];
			int fill = pqByDistanceFill[slot];
			pqByDistanceFill[slot] = fill + 1;
			if (slotArray.length <= fill + 1) {
				slotArray = Arrays.copyOf(slotArray, slotArray.length * 2);
				pqByDistance[slot] = slotArray;
			}
			slotArray[fill] = node;
		} else {
			pq.offer(node);
		}
	}

	private int pqSlotFor(int distance) {
		return distance & (FAST_DISTANCE_ACCESS - 1);
	}

	/**
	 * Only allowed if pqSize < 0. Otherwise, the behaviour is undefined.
	 * 
	 * @return
	 */
	private int pqPoll() {
		if (pqMinDistance >= 0) {
			int slot = pqSlotFor(pqMinDistance);
			int fill = pqByDistanceFill[slot];
			int result = pqByDistance[slot][fill - 1];
			pqByDistanceFill[slot] = fill - 1;
			if (fill == 1) {
				pqFindNextMin();
			}
			return result;
		} else {
			return pq.poll();
		}
	}

	private void pqFindNextMin() {
		int nextMin = -1;
		int max = pqMinDistance + FAST_DISTANCE_ACCESS;
		if (!pq.isEmpty()) {
			max = Math.max(max, pq.poll());
		}
		for (int d = pqMinDistance; d < max; d++) {
			pqMinDistance = d;
			while (!pq.isEmpty() && getDistance(pq.peek()) == d) {
				pqAdd(pq.poll(), d);
			}

			if (pqByDistanceFill[pqSlotFor(d)] > 0) {
				nextMin = d;
				break;
			}
		}
		pqMinDistance = nextMin;
	}
}