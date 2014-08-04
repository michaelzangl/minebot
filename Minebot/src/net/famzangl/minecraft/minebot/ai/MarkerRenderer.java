package net.famzangl.minecraft.minebot.ai;

import net.famzangl.minecraft.minebot.Pos;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;

/**
 * Renders the marker boxes for indicating pos1/pos2
 * 
 * @author michael
 * 
 */
public class MarkerRenderer {

	private static final double MAX = 1.05;
	private static final double MIN = -0.05;

	public void render(double x, double y, double z, Pos... markerPos) {

		GL11.glBlendFunc(GL11.GL_DST_COLOR, GL11.GL_SRC_COLOR);
		GL11.glDisable(GL11.GL_TEXTURE_2D);

		OpenGlHelper.glBlendFunc(774, 768, 1, 0);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
		GL11.glPushMatrix();
		GL11.glPolygonOffset(-3.0F, -3.0F);
		GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		final boolean hadBlend = GL11.glIsEnabled(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_BLEND);
		final Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.setTranslation(-x, -y, -z);

		float redPart = 1.0f;
		for (final Pos m : markerPos) {
			tessellator.setColorRGBA_F(redPart, 0, 0, 0.5f);
			if (m != null) {
				renderMarker(m);
			}
			redPart *= 0.6;
		}

		tessellator.draw();
		tessellator.setTranslation(0.0D, 0.0D, 0.0D);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glPolygonOffset(0.0F, 0.0F);
		GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		if (!hadBlend) {
			GL11.glDisable(GL11.GL_BLEND);
		}
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDepthMask(true);
		GL11.glPopMatrix();
	}

	private void renderMarker(Pos m) {
		final Tessellator tessellator = Tessellator.instance;
		tessellator.addVertex(m.x + MIN, m.y + MAX, m.z + MIN);
		tessellator.addVertex(m.x + MIN, m.y + MAX, m.z + MAX);
		tessellator.addVertex(m.x + MAX, m.y + MAX, m.z + MAX);
		tessellator.addVertex(m.x + MAX, m.y + MAX, m.z + MIN);

		tessellator.addVertex(m.x + MIN, m.y + MIN, m.z + MIN);
		tessellator.addVertex(m.x + MIN, m.y + MIN, m.z + MAX);
		tessellator.addVertex(m.x + MIN, m.y + MAX, m.z + MAX);
		tessellator.addVertex(m.x + MIN, m.y + MAX, m.z + MIN);

		tessellator.addVertex(m.x + MAX, m.y + MAX, m.z + MIN);
		tessellator.addVertex(m.x + MAX, m.y + MAX, m.z + MAX);
		tessellator.addVertex(m.x + MAX, m.y + MIN, m.z + MAX);
		tessellator.addVertex(m.x + MAX, m.y + MIN, m.z + MIN);

		tessellator.addVertex(m.x + MIN, m.y + MIN, m.z + MIN);
		tessellator.addVertex(m.x + MIN, m.y + MAX, m.z + MIN);
		tessellator.addVertex(m.x + MAX, m.y + MAX, m.z + MIN);
		tessellator.addVertex(m.x + MAX, m.y + MIN, m.z + MIN);

		tessellator.addVertex(m.x + MIN, m.y + MAX, m.z + MAX);
		tessellator.addVertex(m.x + MIN, m.y + MIN, m.z + MAX);
		tessellator.addVertex(m.x + MAX, m.y + MIN, m.z + MAX);
		tessellator.addVertex(m.x + MAX, m.y + MAX, m.z + MAX);

		tessellator.addVertex(m.x + MIN, m.y + MIN, m.z + MAX);
		tessellator.addVertex(m.x + MIN, m.y + MIN, m.z + MIN);
		tessellator.addVertex(m.x + MAX, m.y + MIN, m.z + MIN);
		tessellator.addVertex(m.x + MAX, m.y + MIN, m.z + MAX);
	}
}
