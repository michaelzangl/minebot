package net.famzangl.minecraft.minebot.ai.render;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.minecraftforge.client.event.RenderWorldLastEvent;

public interface MarkingStrategy {

	void drawMarkers(RenderWorldLastEvent event, AIHelper helper);

}
