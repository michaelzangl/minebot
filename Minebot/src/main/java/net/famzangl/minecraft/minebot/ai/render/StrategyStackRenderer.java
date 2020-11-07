package net.famzangl.minecraft.minebot.ai.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.StrategyStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.debug.DebugRenderer;

public class StrategyStackRenderer implements DebugRenderer.IDebugRenderer {
    private StrategyStack stack;
    private AIHelper helper;

    public StrategyStackRenderer(StrategyStack stack, AIHelper helper) {
        this.stack = stack;
        this.helper = helper;
    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, double camX, double camY, double camZ) {
        AIStrategy currentStrategy = stack.getCurrentStrategy();
        if (currentStrategy != null) {
            currentStrategy.getDebugRenderer(helper).render(matrixStackIn, bufferIn, camX, camY, camZ);
        }
    }
}
