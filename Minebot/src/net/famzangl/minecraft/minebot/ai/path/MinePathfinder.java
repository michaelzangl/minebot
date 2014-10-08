package net.famzangl.minecraft.minebot.ai.path;

import java.util.Random;
import java.util.Set;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.BlockWhitelist;
import net.famzangl.minecraft.minebot.ai.task.DestroyInRangeTask;
import net.minecraft.block.Block;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class MinePathfinder extends MovePathFinder {
	protected static final float MIN_FACTOR = 0.1f;
	protected static final int MAX_FACTOR = 10;
	protected static final float MAX_POINTS = 50;
	private static final int MAX_BLOCK_IDS = 4096;
	private final Random rand = new Random();
	protected float maxDistancePoints = 0;
	protected float maxDistanceFactor = 1;
	/**
	 * A horizontal direction that is prefered
	 */
	protected final ForgeDirection preferedDirection;
	/**
	 * 0.0 - 1.0.
	 */
	protected final float preferedDirectionInfluence = 0.3f;

	protected final int preferedLayer;
	/**
	 * 0.0 - 1.0.
	 */
	protected final float preferedLayerInfluence = 0.3f;

	private final FloatBlockCache points;
	private final FloatBlockCache factors;

	protected static interface ISettingsProvider {
		float getFloat(Block name);
	}

	private class FloatBlockCache {
		private final float[] cached = new float[MAX_BLOCK_IDS];
		private final boolean[] isCached = new boolean[MAX_BLOCK_IDS];
		private final ISettingsProvider settingsProvider;

		public FloatBlockCache(ISettingsProvider settingsProvider) {
			super();
			this.settingsProvider = settingsProvider;
		}

		public float getForBlock(int id) {

			if (!isCached[id]) {
				// final String name = Block.blockRegistry.getNameForObject(
				// Block.blockRegistry.getObjectById(id)).replace(
				// "minecraft:", "");
				cached[id] = settingsProvider
						.getFloat((Block) Block.blockRegistry.getObjectById(id));
				isCached[id] = true;
			}
			return cached[id];
		}
	}

	@SuppressWarnings("unchecked")
	public MinePathfinder(ForgeDirection preferedDirection, int preferedLayer) {
		this.preferedDirection = preferedDirection;
		this.preferedLayer = preferedLayer;
		points = new FloatBlockCache(getPointsProvider());
		factors = new FloatBlockCache(getFactorProvider());
		
		for (String k : (Set<String>)Block.blockRegistry.getKeys()) {
			int id = Block.getIdFromBlock((Block) Block.blockRegistry.getObject(k));
			float f = factors.getForBlock(id);
			if (f > 0) {
				headAllowedBlocks.intersectWith(new BlockWhitelist(id));
				footAllowedBlocks.intersectWith(new BlockWhitelist(id));
			}
		}
	}

	protected abstract ISettingsProvider getFactorProvider();

	protected abstract ISettingsProvider getPointsProvider();

	@Override
	protected float rateDestination(int distance, int x, int y, int z) {
		final float r1 = rateOreBlockDistance(distance, x, y + 1, z);
		final float r2 = rateOreBlockDistance(distance, x, y, z);

		float addForDoubleMine = 0;

		if (!(r1 != Float.POSITIVE_INFINITY && r2 != Float.POSITIVE_INFINITY)) {
			addForDoubleMine = settings
					.getFloat("mine_double_add", 1, 0.0f, 10);
		}

		final float rating = Math.min(r1, r2);

		if (rating == Float.POSITIVE_INFINITY) {
			return -1;
		} else {
			float badDirectionMalus = 0;
			final Pos current = helper.getPlayerPosition();
			if (preferedDirection != null && preferedDirection.offsetX != 0) {
				final int dx = x - current.x;
				if (Math.signum(dx) != preferedDirection.offsetX) {
					badDirectionMalus = dx * preferedDirectionInfluence;
				}
			} else if (preferedDirection != null
					&& preferedDirection.offsetZ != 0) {
				final int dz = z - current.z;
				if (Math.signum(dz) != preferedDirection.offsetZ) {
					badDirectionMalus = dz * preferedDirectionInfluence;
				}
			}
			final float badY = Math.abs(y - preferedLayer)
					* preferedLayerInfluence;

			return makeRandom(rating + addForDoubleMine + badDirectionMalus
					+ badY);
		}
	}

	private float makeRandom(float f) {
		final float r = settings.getFloat("mine_randomness", 0, 0, 1)
				* rand.nextFloat();
		return f * (1 - r);
	}
	
	private float rateOreBlockDistance(int distance, int x, int y, int z) {
		final int id = helper.getBlockId(x, y, z);
		final float point = points.getForBlock(id);

		final float factor = factors.getForBlock(id);
		return factor == 0 ? Float.POSITIVE_INFINITY : distance / factor
				* maxDistanceFactor + maxDistancePoints - point;
	}

	private boolean isOreBlock(int x, int y, int z) {
		return factors.getForBlock(helper.getBlockId(x, y, z)) > 0;
	}

	@Override
	protected void addTasksForTarget(Pos currentPos) {
		Pos top, bottom;
		if (isOreBlock(currentPos.x, currentPos.y + 1, currentPos.z)) {
			top = currentPos.add(0, 1, 0);
		} else {
			top = currentPos;
		}
		if (isOreBlock(currentPos.x, currentPos.y, currentPos.z)) {
			bottom = currentPos;
		} else {
			bottom = currentPos.add(0, 1, 0);
		}

		for (int i = 2; i < 5; i++) {
			if (!helper.hasSafeSides(currentPos.x, currentPos.y + i,
					currentPos.z)
					|| !helper.isSafeHeadBlock(currentPos.x, currentPos.y + i
							+ 1, currentPos.z)) {
				break;
			}
			if (isOreBlock(currentPos.x, currentPos.y + i, currentPos.z)) {
				top = currentPos.add(0, i, 0);
			} else {
				break;
			}
		}
		addTask(new DestroyInRangeTask(bottom, top));
	}

	@Override
	protected int materialDistance(int x, int y, int z, boolean onFloor) {
		return isOreBlock(x, y, z) ? 0 : super.materialDistance(x, y, z,
				onFloor);
	}
}