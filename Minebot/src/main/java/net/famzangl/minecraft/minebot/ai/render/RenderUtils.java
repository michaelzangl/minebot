package net.famzangl.minecraft.minebot.ai.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Matrix4f;

public class RenderUtils {
    // Just because WorldRenderer#drawVoxelShapeParts ignores color

    public static void drawVoxelShapeParts(MatrixStack matrixStackIn, IVertexBuilder bufferIn, VoxelShape shapeIn, double xIn, double yIn, double zIn, float red, float green, float blue, float alpha) {
        for (AxisAlignedBB axisalignedbb : shapeIn.toBoundingBoxList()) {
            drawShape(matrixStackIn, bufferIn, axisalignedbb, xIn, yIn, zIn, red, green, blue, alpha);
        }
    }

    public static void drawShape(MatrixStack matrixStackIn, IVertexBuilder bufferIn, AxisAlignedBB axisalignedbb, double xIn, double yIn, double zIn, float red, float green, float blue, float alpha) {
        drawSingleShape(matrixStackIn, bufferIn, VoxelShapes.create(axisalignedbb), xIn, yIn, zIn, red, green, blue, alpha);
    }

    private static void drawSingleShape(MatrixStack matrixStackIn, IVertexBuilder bufferIn, VoxelShape shapeIn, double xIn, double yIn, double zIn, float red, float green, float blue, float alpha) {
        Matrix4f matrix4f = matrixStackIn.getLast().getMatrix();
        shapeIn.forEachEdge((x1, y1, z1, x2, y2, y3) -> {
            bufferIn.pos(matrix4f, (float) (x1 + xIn), (float) (y1 + yIn), (float) (z1 + zIn)).color(red, green, blue, alpha).endVertex();
            bufferIn.pos(matrix4f, (float) (x2 + xIn), (float) (y2 + yIn), (float) (y3 + zIn)).color(red, green, blue, alpha).endVertex();
        });
    }

}
