package net.famzangl.minecraft.minebot.ai;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;

import net.famzangl.minecraft.minebot.Pos;

public class PathFinderField implements Comparator<Integer> {
	private static final long MAX_RUN_TIME = 200;
	// Power of 2!
	private static final int Y_LEVEL = 32;
	private static int SIZE_X_Z = 256;
	private static int FIELD_SIZE = (1 << 16) * Y_LEVEL;

	private PathFinderFieldData data = new PathFinderFieldData();
	private Dest currentDest = null;
	private final PriorityQueue<Integer> pq = new PriorityQueue<Integer>(100,
			this);

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

	protected int getIndexForBlock(int x, int y, int z) {
		return ((x - data.offsetX) & (SIZE_X_Z  - 1)) | (((z - data.offsetZ) & (SIZE_X_Z  - 1)) << 8)
				| (((y - data.offsetY) & (Y_LEVEL - 1)) << 16);
	}

	protected final int getX(int blockIndex) {
		return (blockIndex & (SIZE_X_Z  - 1)) + data.offsetX;
	}

	protected final int getY(int currentNode) {
		return ((currentNode >> 16) & (Y_LEVEL - 1)) + data.offsetY;
	}

	protected final int getZ(int currentNode) {
		return ((currentNode >> 8) & (SIZE_X_Z  - 1)) + data.offsetZ;
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
		return (field[blockIndex] & FIELD_DISTANCE_SET_MASK) == 0 ? Integer.MAX_VALUE
				: (field[blockIndex] & FIELD_DISTANCE_MASK) >> FIELD_DISTANCE_SHIFT;
	}

	private void setDistance(int blockIndex, int distance) {
		field[blockIndex] &= ~FIELD_DISTANCE_MASK;
		field[blockIndex] |= (distance << FIELD_DISTANCE_SHIFT)
				& FIELD_DISTANCE_MASK;
		field[blockIndex] |= FIELD_DISTANCE_SET_MASK;
	}

	private void setMoveFrom(int newIndex, int currentNode) {
		int newx = getX(newIndex);
		int newy = getY(newIndex);
		int newz = getZ(newIndex);
		int oldx = getX(currentNode);
		int oldy = getY(currentNode);
		int oldz = getZ(currentNode);
		field[newIndex] &= ~(FIELD_MOVEFROM_X_MASK | FIELD_MOVEFROM_Y_MASK | FIELD_MOVEFROM_Z_MASK);
		field[newIndex] |= ((newx - oldx) << FIELD_MOVEFROM_X_SHIFT)
				& FIELD_MOVEFROM_X_MASK;
		field[newIndex] |= ((newy - oldy) << FIELD_MOVEFROM_Y_SHIFT)
				& FIELD_MOVEFROM_Y_MASK;
		field[newIndex] |= ((newz - oldz) << FIELD_MOVEFROM_Z_SHIFT)
				& FIELD_MOVEFROM_Z_MASK;
	}

	private int getFromDirectionMasked(int blockIndex, int mask, int shift) {
		int res = (field[blockIndex] & mask) >> shift;
		int signBit = ((mask >> shift) + 1) >> 1;
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
		return getDistance(o1) - getDistance(o2);
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

	public boolean searchSomethingAround(Pos playerPosition) {
		return searchSomethingAround(playerPosition.x, playerPosition.y,
				playerPosition.z);
	}

	public boolean searchSomethingAround(int cx, int cy, int cz) {
		if (data.offsetX != cx - SIZE_X_Z / 2 || data.offsetY != cy - Y_LEVEL / 2
				|| data.offsetZ != cz - SIZE_X_Z / 2) {
			isRunning = false;
		}
		if (!isRunning) {
			System.out.println("Restart");
			field = new int[FIELD_SIZE];
			data.offsetX = cx - SIZE_X_Z / 2;
			data.offsetY = cy - Y_LEVEL / 2;
			data.offsetZ = cz - SIZE_X_Z / 2;
			pq.clear();
			currentDest = null;
			int start = getIndexForBlock(cx, cy, cz);
			pq.add(start);
			setDistance(start, 1);
			isRunning = true;
		}
		startTime = System.currentTimeMillis();
		long iteration = 0;
		while (!pq.isEmpty()
				&& ((iteration++ & 0xff) != 0 || hasTimeLeft(startTime))) {
			int currentNode = pq.poll();
			int currentDistance = getDistance(currentNode);
			Dest head = currentDest;
			if (head != null && currentDistance + 1 > head.destDistanceRating) {
				planPathTo(head.destNode, cx, cy, cz);
				terminated();
				return true;
			}
			float rating = rateDestination(currentNode);
			if (rating >= 0) {
				Dest newDest = new Dest(currentNode, rating);
				if (currentDest == null || newDest.compareTo(currentDest) < 0) {
					currentDest = newDest;
				}
			}

			int[] neighbours = getNeighbours(currentNode);
			for (int n : neighbours) {
				if (n < 0) {
					continue;
				}
				if (isVisited(n)) {
					continue;
				}
				int distance = distanceFor(currentNode, n) + currentDistance;
				if (distance < getDistance(n)) {
					setDistance(n, distance);
					setMoveFrom(n, currentNode);
				}
				if (!isInQueue(n)) {
					setInQueue(n);
					pq.add(n);
				}
			}
			setVisited(currentNode);
		}
		if (pq.isEmpty()) {
			noPathFound();
			terminated();
			return true;
		} else {
			System.out
					.println("Warning: Path finding needs more time. Just got "
							+ iteration + " iterations.");
			return false;
		}
	}

	private boolean hasTimeLeft(long startTime) {
		return startTime + MAX_RUN_TIME > System.currentTimeMillis();
	}

	private void terminated() {
		isRunning = false;
		field = null;
		pq.clear();
		currentDest = null;
	}

	protected int distanceFor(int from, int to) {
		return 1;
	}

	protected void noPathFound() {
		System.out.println("Could not find a path.");
	}

	private void planPathTo(int currentNode, int origX, int origY, int origZ) {
		int cx = getX(currentNode);
		int cy = getY(currentNode);
		int cz = getZ(currentNode);
		System.out.println("Reconstruct.");
		LinkedList<Pos> path = new LinkedList<Pos>();
		while (cx != origX || cy != origY || cz != origZ) {
			path.addFirst(new Pos(cx, cy, cz));
			int current = getIndexForBlock(cx, cy, cz);
			debug(current);
			cx -= getFromDirectionX(current);
			cy -= getFromDirectionY(current);
			cz -= getFromDirectionZ(current);
		}
		path.addFirst(new Pos(origX, origY, origZ));
		foundPath(path);
	}

	protected void foundPath(LinkedList<Pos> path) {
		System.out.println("Found a path!");
		for (Pos p : path) {
			System.out.println("PAth part: " + p);
		}
	}

	// The smaller the better. -1 for no dest.
	private float rateDestination(int currentNode) {
		int cx = getX(currentNode);
		int cy = getY(currentNode);
		int cz = getZ(currentNode);
		int distance = getDistance(currentNode);
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
	 * @return
	 */
	protected float rateDestination(int distance, int x, int y, int z) {
		return distance;
	}

	protected int[] getNeighbours(int currentNode) {
		int[] res = new int[6];
		int cx = getX(currentNode);
		int cz = getZ(currentNode);
		int cy = getY(currentNode);
		res[0] = getNeighbour(currentNode, cx + 1, cy, cz);
		res[1] = getNeighbour(currentNode, cx - 1, cy, cz);
		res[2] = getNeighbour(currentNode, cx, cy + 1, cz);
		res[3] = getNeighbour(currentNode, cx, cy - 1, cz);
		res[4] = getNeighbour(currentNode, cx, cy, cz + 1);
		res[5] = getNeighbour(currentNode, cx, cy, cz - 1);
		return res;
	}

	protected int getNeighbour(int currentNode, int cx, int cy, int cz) {
		return cy > 1 && cy < 256 && cx > data.offsetX
				&& cx < data.offsetX + 256 && cy > data.offsetY
				&& cy < data.offsetY + Y_LEVEL && cz > data.offsetZ
				&& cz < data.offsetZ + 256 ? getIndexForBlock(cx, cy, cz) : -1;
	}

	private void debug(int nodeId) {
		System.out.println("pos="
				+ new Pos(getX(nodeId), getY(nodeId), getZ(nodeId))
				+ ", inQueue=" + isInQueue(nodeId) + ", visited="
				+ isVisited(nodeId) + ", distance=" + getDistance(nodeId)
				+ ", fromX=" + getFromDirectionX(nodeId) + ", fromY="
				+ getFromDirectionY(nodeId) + ", fromZ="
				+ getFromDirectionZ(nodeId) + ", data="
				+ Integer.toHexString(field[nodeId]));
	}
}