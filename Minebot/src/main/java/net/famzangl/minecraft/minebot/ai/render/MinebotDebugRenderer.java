package net.famzangl.minecraft.minebot.ai.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy;
import net.famzangl.minecraft.minebot.ai.utils.BlockCuboid;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.debug.DebugRenderer;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
public class MinebotDebugRenderer extends DebugRenderer {
    private List<IDebugRenderer> minebotDebugRenderers = new ArrayList<>();

    public MinebotDebugRenderer(Minecraft clientIn, AIHelper helper, Supplier<AIStrategy> strategy) {
        super(clientIn);

        // Minebuild selection area
        minebotDebugRenderers.add(new PosMarkerRenderer(helper::getPos1, 1.0f, 0.4f, 0.4f, "pos1"));
        minebotDebugRenderers.add(new PosMarkerRenderer(helper::getPos2, 0.5f, 0, 0, "pos2"));
        minebotDebugRenderers.add(new CuboidDebugRenderer(() -> {
            if (helper.getPos1() != null && helper.getPos2() != null) {
                return new BlockCuboid<>(helper.getPos1(), helper.getPos2());
            } else {
                return null;
            }
        }, 1.0f, 0, 0));

        // Minebuild tasks
        minebotDebugRenderers.add(new BuildMarkerRenderer(helper));

        // Minebot path finder queue
        minebotDebugRenderers.add(new ActiveStrategyRenderer(strategy, helper));
    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer.Impl bufferIn, double camX, double camY, double camZ) {
        // Reset the render system
        RenderSystem.pushMatrix();
        RenderSystem.enableDepthTest();
        RenderSystem.shadeModel(7425);
        RenderSystem.enableAlphaTest();
        RenderSystem.defaultAlphaFunc();
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.color4f(1, 1, 1, 1);
        RenderSystem.lineWidth(1.0F);

        minebotDebugRenderers.forEach(it -> it.render(matrixStackIn, bufferIn, camX, camY, camZ));

        // This is what MC expects us to do => see other debug renderers
        RenderSystem.lineWidth(1.0F);
        RenderSystem.enableBlend();
        RenderSystem.enableTexture();
        RenderSystem.popMatrix();
        RenderSystem.shadeModel(7424);
    }
}
