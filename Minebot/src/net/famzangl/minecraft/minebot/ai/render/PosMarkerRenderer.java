package net.famzangl.minecraft.minebot.ai.render;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.minecraftforge.client.event.RenderWorldLastEvent;

/**
 * Renders the marker boxes for indicating pos1/pos2
 * 
 * @author michael
 * 
 */
public class PosMarkerRenderer extends RenderHelper {

	private final float r;
	private final float g;
	private final float b;

	public PosMarkerRenderer(float r, float g, float b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}

	public void render(RenderWorldLastEvent event, AIHelper helper,
			Pos... markerPos) {
		renderStart(event, helper);

		float part = 1.0f;
		for (final Pos m : markerPos) {
			if (m != null) {
				renderMarker(m, r * part + (1 - part), g * part + (1 - part), b
						* part + (1 - part), 0.7f * part);
			}
			part *= 0.6;
		}
		renderEnd();
	}

}
