package net.famzangl.minecraft.minebot.ai.path.world;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;

public abstract class BlockBoundsCache {
    private static final Marker MARKER_BOUNDS_PROBLEM = MarkerManager.getMarker("missing bounds");
    private static final Logger LOGGER = LogManager.getLogger(BlockBounds.class);

    // Reduces memory and make more cache efficient by pooling the used bounds
    private static final HashMap<BlockBounds, BlockBounds> usedBounds = new HashMap<BlockBounds, BlockBounds>();
    private static final BlockPos POS_FAKE_BLOCK_IS_AT = new BlockPos(0, 100, 0);

    static {
        usedBounds.put(BlockBounds.FULL_BLOCK, BlockBounds.FULL_BLOCK);
        usedBounds.put(BlockBounds.LOWER_HALF_BLOCK, BlockBounds.LOWER_HALF_BLOCK);
        usedBounds.put(BlockBounds.UPPER_HALF_BLOCK, BlockBounds.UPPER_HALF_BLOCK);
    }

    public static final BlockBoundsCache COLLISION = new BlockBoundsCache() {
        @Override
        protected VoxelShape computeBounds(BlockState state, IBlockReader world, BlockPos position) {
            return state.getCollisionShape(world, position);
        }
    };
    public static final BlockBoundsCache RAYTRACE = new BlockBoundsCache() {
        @Override
        protected VoxelShape computeBounds(BlockState state, IBlockReader world, BlockPos position) {
            return state.getRaytraceShape(world, position, null);
        }
    };

    private BlockBounds[] bounds = new BlockBounds[10];

    private BlockBoundsCache() {
    }

    public BlockBounds get(int x, int y, int z, WorldData worldData) {
        int blockStateId = worldData.getBlockStateId(x, y, z);
        BlockBounds myBounds = get(blockStateId);
        if (myBounds == BlockBounds.UNKNOWN_BLOCK) {
            // Bounds depend on world state
            try {
                BlockState state = BlockSet.getStateById(blockStateId);
                BlockPos pos = new BlockPos(x, y, z);
                FakeBlockReaderWithWorld worldReader = new FakeBlockReaderWithWorld(state, pos, worldData);
                return BlockBounds.from(computeBounds(state, worldReader, pos));
            } catch (FakeBlockReader.CannotComputeBounds e) {
                LOGGER.error("Cannot compute bounds");
                return BlockBounds.FULL_BLOCK;
            }
        } else {
            return myBounds;
        }
    }

    public BlockBounds get(int blockStateId) {
        if (blockStateId < bounds.length && bounds[blockStateId] != null) {
            return bounds[blockStateId];
        }

        if (bounds.length <= blockStateId) {
            bounds = Arrays.copyOf(bounds, Math.max(blockStateId + 1, bounds.length * 2));
        }

        // Compute bounds
        BlockState state = BlockSet.getStateById(blockStateId);
        BlockBounds myBounds = computeBounds(state);
        bounds[blockStateId] = myBounds;
        return bounds[blockStateId];
    }

    private BlockBounds computeBounds(BlockState state) {
        try {
            IBlockReader world = new FakeBlockReader(state);

            BlockBounds bounds = BlockBounds.from(computeBounds(state, world, POS_FAKE_BLOCK_IS_AT));
            LOGGER.debug("Computed bounds for {}: {}", state, bounds);
            // dedup
            usedBounds.computeIfAbsent(bounds, __ -> bounds);
            return usedBounds.get(bounds);
        } catch (FakeBlockReader.CannotComputeBounds e) {
            LOGGER.warn("Cannot compute the bounds for block state " + state, e);
            // Do not dedub, since we need == to check this.
            return BlockBounds.UNKNOWN_BLOCK;
        }
    }

    protected abstract VoxelShape computeBounds(BlockState state, IBlockReader world, BlockPos position);

    private static class FakeBlockReader implements IBlockReader {
        protected final BlockState state;

        public FakeBlockReader(BlockState state) {
            this.state = state;
        }

        @Nullable
        @Override
        public TileEntity getTileEntity(BlockPos pos) {
            throw new CannotComputeBounds("Bounds depend on tile entity");
        }

        @Override
        public BlockState getBlockState(BlockPos pos) {
            if (POS_FAKE_BLOCK_IS_AT.equals(pos)) {
                return state;
            }
            throw new CannotComputeBounds("Bounds depend on other block at relative position " + pos.subtract(POS_FAKE_BLOCK_IS_AT));
        }

        @Override
        public FluidState getFluidState(BlockPos pos) {
            throw new CannotComputeBounds("Bounds depend on fluid state");
        }

        @Override
        public int getLightValue(BlockPos pos) {
            return 0;
        }

        @Override
        public int getMaxLightLevel() {
            return 7;
        }

        @Override
        public int getHeight() {
            return 255;
        }

        @Override
        public BlockRayTraceResult rayTraceBlocks(RayTraceContext context) {
            throw new CannotComputeBounds("Bounds use ray trace");
        }

        @Nullable
        @Override
        public BlockRayTraceResult rayTraceBlocks(Vector3d p_217296_1_, Vector3d p_217296_2_, BlockPos p_217296_3_,
                                                  VoxelShape p_217296_4_, BlockState p_217296_5_) {
            throw new CannotComputeBounds("Bounds use ray trace");
        }

        private class CannotComputeBounds extends RuntimeException {
            public CannotComputeBounds(String message) {
                super(message);
            }
        }
    }

    private static class FakeBlockReaderWithWorld extends FakeBlockReader {
        private final BlockPos posOfThatState;
        private final WorldData world;

        FakeBlockReaderWithWorld(BlockState state, BlockPos posOfThatState, WorldData world) {
            super(state);
            this.posOfThatState = posOfThatState;
            this.world = world;
        }

        @Override
        public BlockState getBlockState(BlockPos pos) {
            if (pos.equals(posOfThatState)) {
                return state;
            } else {
                return world.getBlockState(pos);
            }
        }
    }
}
