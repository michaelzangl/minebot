package net.famzangl.minecraft.minebot.ai.path.world;

import java.util.HashMap;
import java.util.HashSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import net.famzangl.minecraft.minebot.ai.command.BlockWithData;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;

public class BlockBoundsCache {
	private static final Marker MARKER_BOUNDS_PROBLEM = MarkerManager
			.getMarker("missing bounds");
	private static final Logger LOGGER = LogManager
			.getLogger(BlockBounds.class);

	private static BlockBounds[] bounds;

	private BlockBoundsCache() {
	}

	public static BlockBounds getBounds(BlockWithData bwd) {
		return getBounds(bwd.getBlockWithMeta());
	}

	public static BlockBounds getBounds(int blockWithMeta) {
		return bounds[blockWithMeta];
	}

	public static void initialize() {
		bounds = new BlockBounds[16 * 4096];
		HashMap<BlockBounds, BlockBounds> usedBounds = new HashMap<BlockBounds, BlockBounds>();
		usedBounds.put(BlockBounds.FULL_BLOCK, BlockBounds.FULL_BLOCK);
		usedBounds.put(BlockBounds.LOWER_HALF_BLOCK, BlockBounds.LOWER_HALF_BLOCK);
		usedBounds.put(BlockBounds.UPPER_HALF_BLOCK, BlockBounds.UPPER_HALF_BLOCK);
		for (int i = 0; i < bounds.length; i++) {
			try {
				BlockBounds bound = attemptLoad(i);
				usedBounds.put(bound, bound);
				bounds[i] = usedBounds.get(bound);
			} catch (Throwable e) {
				LOGGER.warn(MARKER_BOUNDS_PROBLEM,
						"Could not create bounds for " + new BlockWithData(i));
				bounds[i] = BlockBounds.FULL_BLOCK;
			}
		}
	}

	private static BlockBounds attemptLoad(int blockWithMeta) {
		BlockWithData d = new BlockWithData(blockWithMeta);
		Block block = d.getBlock();
		final IBlockState state = d.getBlockState();
		IBlockAccess world = new IBlockAccess() {
			@Override
			public boolean isAirBlock(BlockPos pos) {
				throw new UnsupportedOperationException();
			}

			@Override
			public WorldType getWorldType() {
				throw new UnsupportedOperationException();
			}

			@Override
			public TileEntity getTileEntity(BlockPos pos) {
				throw new UnsupportedOperationException();
			}

			@Override
			public int getStrongPower(BlockPos pos, EnumFacing direction) {
				throw new UnsupportedOperationException();
			}

			@Override
			public int getCombinedLight(BlockPos pos, int p_175626_2_) {
				throw new UnsupportedOperationException();
			}

			@Override
			public IBlockState getBlockState(BlockPos pos) {
				if (Pos.ZERO.equals(pos)) {
					return state;
				}
				throw new UnsupportedOperationException();
			}

			@Override
			public BiomeGenBase getBiomeGenForCoords(BlockPos pos) {
				throw new UnsupportedOperationException();
			}

			@Override
			public boolean extendedLevelsInChunkCache() {
				throw new UnsupportedOperationException();
			}
		};
		block.setBlockBoundsBasedOnState(world, Pos.ZERO);

		return new BlockBounds(block);
	}
}
