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

import java.util.Random;
import java.util.Set;

import net.famzangl.minecraft.minebot.ai.path.world.BlockFloatMap;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSets;
import net.famzangl.minecraft.minebot.ai.task.DestroyInRangeTask;
import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

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
	protected final EnumFacing preferedDirection;
	/**
	 * 0.0 - 1.0.
	 */
	protected final float preferedDirectionInfluence = 0.3f;

	protected final int preferedLayer;
	/**
	 * 0.0 - 1.0.
	 */
	protected final float preferedLayerInfluence = 0.3f;

	private BlockFloatMap points;
	private BlockFloatMap factors;

//	protected static interface ISettingsProvider {
//		float getFloat(Block name);
//	}

//	private class FloatBlockCache {
//		private final float[] cached = new float[MAX_BLOCK_IDS];
//		private final boolean[] isCached = new boolean[MAX_BLOCK_IDS];
//		private final ISettingsProvider settingsProvider;
//
//		public FloatBlockCache(ISettingsProvider settingsProvider) {
//			super();
//			this.settingsProvider = settingsProvider;
//		}
//
//		public float getForBlock(int id) {
//
//			if (!isCached[id]) {
//				// final String name = Block.blockRegistry.getNameForObject(
//				// Block.blockRegistry.getObjectById(id)).replace(
//				// "minecraft:", "");
//				cached[id] = settingsProvider
//						.getFloat((Block) Block.blockRegistry.getObjectById(id));
//				isCached[id] = true;
//			}
//			return cached[id];
//		}
//	}

	@SuppressWarnings("unchecked")
	public MinePathfinder(EnumFacing preferedDirection, int preferedLayer) {
		this.preferedDirection = preferedDirection;
		this.preferedLayer = preferedLayer;
	}

	@Override
	protected final boolean runSearch(BlockPos playerPosition) {
		// lazy init
		if (points == null) {
			points = getPointsProvider();
			if (points == null) {
				throw new NullPointerException("No points map provided.");
			}
		}
		if (factors == null) {
			factors = getFactorProvider();
			if (factors == null) {
				throw new NullPointerException("No points map provided.");
			}
			for (ResourceLocation k : (Set<ResourceLocation>) Block.blockRegistry
					.getKeys()) {
				int id = Block.getIdFromBlock((Block) Block.blockRegistry
						.getObject(k));
				// TODO: Do this better...
				float f = factors.get(id * 16);
				if (f > 0) {
					headAllowedBlocks.intersectWith(new BlockSet(id));
					footAllowedBlocks.intersectWith(new BlockSet(id));
				}
			}
		}
		
		onPreRunSearch(playerPosition);
		
		return super.runSearch(playerPosition);
	}

	protected void onPreRunSearch(BlockPos playerPosition) {
	}

	protected abstract BlockFloatMap getFactorProvider();

	protected abstract BlockFloatMap getPointsProvider();

	@Override
	protected float rateDestination(int distance, int x, int y, int z) {
		final float r1 = rateOreBlockDistance(distance, x, y + 1, z);
		final float r2 = rateOreBlockDistance(distance, x, y, z);

		float addForDoubleMine = 0;

		if (!(r1 != Float.POSITIVE_INFINITY && r2 != Float.POSITIVE_INFINITY)) {
			addForDoubleMine = settings.getMining().getDoubleBonus();
		}

		final float rating = Math.min(r1, r2);

		if (rating == Float.POSITIVE_INFINITY) {
			return -1;
		} else {
			float badDirectionMalus = 0;
			final BlockPos current = world.getPlayerPosition();
			if (preferedDirection != null
					&& preferedDirection.getFrontOffsetX() != 0) {
				final int dx = x - current.getX();
				if (Math.signum(dx) != preferedDirection.getFrontOffsetX()) {
					badDirectionMalus = dx * preferedDirectionInfluence;
				}
			} else if (preferedDirection != null
					&& preferedDirection.getFrontOffsetZ() != 0) {
				final int dz = z - current.getZ();
				if (Math.signum(dz) != preferedDirection.getFrontOffsetZ()) {
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
		final float r = settings.getMining().getRandomness() * rand.nextFloat();
		return f * (1 - r);
	}

	private float rateOreBlockDistance(int distance, int x, int y, int z) {
		final int id = world.getBlockIdWithMeta(x, y, z);
		final float point = points.get(id);

		final float factor = factors.get(id);
		if (factor == 0) {
			return Float.POSITIVE_INFINITY;
		} else {
			return distance / factor * maxDistanceFactor + maxDistancePoints
					- point;
		}
	}

	protected boolean isOreBlock(int x, int y, int z) {
		return factors.get(world.getBlockIdWithMeta(x, y, z)) > 0;
	}

	@Override
	protected void addTasksForTarget(BlockPos currentPos) {
		BlockPos top, bottom;
		int x = currentPos.getX();
		int y = currentPos.getY();
		int z = currentPos.getZ();
		if (isOreBlock(x, y + 1, z)) {
			top = currentPos.add(0, 1, 0);
		} else {
			top = currentPos;
		}
		if (isOreBlock(x, y, z)) {
			bottom = currentPos;
		} else {
			bottom = currentPos.add(0, 1, 0);
		}

		for (int i = 2; i < 5; i++) {
			if (!BlockSets.safeSideAndCeilingAround(world, x, y + i, z)) {
				break;
			}
			if (isOreBlock(x, y + i, z)) {
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