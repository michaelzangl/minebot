package net.famzangl.minecraft.minebot.ai.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.famzangl.minecraft.minebot.ai.utils.BlockCuboid;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
public class CuboidDebugRenderer implements DebugRenderer.IDebugRenderer {

    public static final double EXPAND = .02;
    private final Supplier<BlockCuboid<?>> pos;
    private final float r;
    private final float g;
    private final float b;

    public CuboidDebugRenderer(Supplier<BlockCuboid<?>> pos, float r, float g, float b) {
        this.pos = pos;
        this.r = r;
        this.g = g;
        this.b = b;
    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, double camX, double camY, double camZ) {
        IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RenderType.getLines());
        BlockCuboid<?> pos = this.pos.get();
        if (pos != null) {
            AxisAlignedBB box = new AxisAlignedBB(pos.getMin().getX() - EXPAND, pos.getMin().getY() - EXPAND, pos.getMin().getZ() - EXPAND,
                    pos.getMax().getX() + 1 + EXPAND, pos.getMax().getY() + 1 + EXPAND, pos.getMax().getZ() + 1 + EXPAND);

            RenderUtils.drawShape(matrixStackIn, ivertexbuilder, box, -camX, -camY, -camZ, r, g, b, 1.0F);
        }
    }
}
