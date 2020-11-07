package net.famzangl.minecraft.minebot.ai.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.debug.DebugRenderer;

import java.util.function.Supplier;

public class ActiveStrategyRenderer implements DebugRenderer.IDebugRenderer {
    private final Supplier<AIStrategy> strategySupplier;
    private final AIHelper helper;

    public ActiveStrategyRenderer(Supplier<AIStrategy> strategySupplier, AIHelper helper) {
        this.strategySupplier = strategySupplier;
        this.helper = helper;
    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, double camX, double camY, double camZ) {
        AIStrategy strategy = strategySupplier.get();
        if (strategy != null) {
            strategy.getDebugRenderer(helper).render(matrixStackIn, bufferIn, camX, camY, camZ);
        }
    }
}