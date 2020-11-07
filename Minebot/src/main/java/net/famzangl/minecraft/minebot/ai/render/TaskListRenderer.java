package net.famzangl.minecraft.minebot.ai.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSets;
import net.famzangl.minecraft.minebot.ai.path.world.WorldWithDelta;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
public class TaskListRenderer implements DebugRenderer.IDebugRenderer {
    private final List<AITask> tasks;
    private AIHelper helper;

    public TaskListRenderer(List<AITask> tasks, AIHelper helper) {
        this.tasks = tasks;
        this.helper = helper;
    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, double camX, double camY, double camZ) {
        // For drawing destroyed / added blocks
        IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RenderType.getLines());
        RenderingWorldWithDelta rendererDelta = new RenderingWorldWithDelta(matrixStackIn, ivertexbuilder,
                camX, camY, camZ);
        for (AITask task : tasks) {
            task.applyToDelta(rendererDelta);
        }

        rendererDelta.drawPath();
    }

    private class RenderingWorldWithDelta extends WorldWithDelta {
        private final List<BlockPos> pointsToWalkTo = new ArrayList<>();

        private final MatrixStack matrixStackIn;
        private final IVertexBuilder ivertexbuilder;
        private final double camX;
        private final double camY;
        private final double camZ;

        public RenderingWorldWithDelta(MatrixStack matrixStackIn, IVertexBuilder ivertexbuilder, double camX, double camY, double camZ) {
            super(TaskListRenderer.this.helper.getWorld());
            this.matrixStackIn = matrixStackIn;
            this.ivertexbuilder = ivertexbuilder;
            this.camX = camX;
            this.camY = camY;
            this.camZ = camZ;
            pointsToWalkTo.add(getPlayerPosition());
        }

        @Override
        public void setBlock(BlockPos pos, BlockState block) {
            BlockState oldBlock = getBlockState(pos);
            if (BlockSets.AIR.contains(block) && !BlockSets.AIR.contains(oldBlock)) {
                // Paint old block red
                VoxelShape shape = oldBlock.getShape(getBackingWorld(), pos);
                RenderUtils.drawVoxelShapeParts(matrixStackIn, ivertexbuilder,
                        shape.withOffset(pos.getX(), pos.getY(), pos.getZ()),
                        -camX, -camY, -camZ, 0.7f, 0, 0, 1.0F);
            } else if (!BlockSets.AIR.contains(block)) {
                // Paint new block green
                VoxelShape shape = block.getShape(getBackingWorld(), pos);
                RenderUtils.drawVoxelShapeParts(matrixStackIn, ivertexbuilder,
                        shape.withOffset(pos.getX(), pos.getY(), pos.getZ()),
                        -camX, -camY, -camZ, 0, 0.7f, 0, 1.0F);
            }

            super.setBlock(pos, block);
        }

        @Override
        public void setPlayerPosition(BlockPos playerPosition) {
            pointsToWalkTo.add(playerPosition);
            super.setPlayerPosition(playerPosition);
        }

        /**
         * Draw the path the player walked. Cannot be drawn while applying delta (would conflict with other draws)
         */
        public void drawPath() {
            // For hidden (non-visible parts)
            RenderSystem.lineWidth(6.0F);
            //RenderSystem.color4f(1.0f, 1.0f, 0.2F, 0.2f);
            RenderSystem.disableAlphaTest();
            drawPathTrace(0.2f);

            // Visible parts
            RenderSystem.enableAlphaTest();
            //RenderSystem.color4f(1.0f, 1.0f, 0.2F, 0.75F);
            drawPathTrace(0.75f);
        }

        private void drawPathTrace(float alpha) {
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);

            for(int i = 0; i < pointsToWalkTo.size(); ++i) {
                BlockPos point = pointsToWalkTo.get(i);

                float dim = 0.5f * i / pointsToWalkTo.size();
                bufferbuilder.pos(point.getX() - camX + 0.5D, point.getY() - camY + 0.5D,
                            point.getZ() - camZ + 0.5D).color(.8f, .8f, .1f, alpha * (1 - dim)).endVertex();
            }

            tessellator.draw();
        }

    }
}
