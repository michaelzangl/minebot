package net.famzangl.minecraft.minebot.ai.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.famzangl.minecraft.minebot.ai.utils.BlockCuboid;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
public class PosMarkerRenderer extends CuboidDebugRenderer {

    private final Supplier<BlockPos> pos;
    private final String name;

    public PosMarkerRenderer(Supplier<BlockPos> pos, float r, float g, float b, String name) {
        super(() -> {
            BlockPos val = pos.get();
            return val != null ? new BlockCuboid<>(val, val) : null;
        }, r, g, b);
        this.pos = pos;
        this.name = name;
    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, double camX, double camY, double camZ) {
        super.render(matrixStackIn, bufferIn, camX, camY, camZ);

        BlockPos pos = this.pos.get();
        if (pos != null) {
            DebugRenderer.renderText(name,
                   pos.getX(), pos.getY(), pos.getZ(), -1);
        }
    }
}
