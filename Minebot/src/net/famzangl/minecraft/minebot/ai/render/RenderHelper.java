package net.famzangl.minecraft.minebot.ai.render;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import org.lwjgl.opengl.GL11;

/**
 * Helps rendering markers.
 * 
 * @author michael
 * 
 */
public class RenderHelper {

	private static final double MAX = 1.05;
	private static final double MIN = -0.05;

	private boolean hadBlend;

	public void renderStart(RenderWorldLastEvent event, AIHelper helper) {
		final Tessellator tessellator = Tessellator.instance;
		EntityLivingBase player = helper.getMinecraft().renderViewEntity;
		final double x = player.lastTickPosX
				+ (player.posX - player.lastTickPosX) * event.partialTicks;
		final double y = player.lastTickPosY
				+ (player.posY - player.lastTickPosY) * event.partialTicks;
		final double z = player.lastTickPosZ
				+ (player.posZ - player.lastTickPosZ) * event.partialTicks;

		GL11.glBlendFunc(GL11.GL_DST_COLOR, GL11.GL_SRC_COLOR);
		GL11.glDisable(GL11.GL_TEXTURE_2D);

		OpenGlHelper.glBlendFunc(774, 768, 1, 0);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
		GL11.glPushMatrix();
		GL11.glPolygonOffset(-3.0F, -3.0F);
		GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		hadBlend = GL11.glIsEnabled(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_BLEND);
		tessellator.startDrawingQuads();
		tessellator.setTranslation(-x, -y, -z);
	}

	protected void renderEnd() {
		final Tessellator tessellator = Tessellator.instance;

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

	protected void renderMarker(Pos m, float r, float g, float b, float a) {
		final Tessellator tessellator = Tessellator.instance;
		tessellator.setColorRGBA_F(r, g, b, a);
		renderMarkerP(tessellator, m.x, m.y, m.z);
	}

	private void renderMarkerP(Tessellator tessellator, int x, int y, int z) {
		tessellator.addVertex(x + MIN, y + MAX, z + MIN);
		tessellator.addVertex(x + MIN, y + MAX, z + MAX);
		tessellator.addVertex(x + MAX, y + MAX, z + MAX);
		tessellator.addVertex(x + MAX, y + MAX, z + MIN);

		tessellator.addVertex(x + MIN, y + MIN, z + MIN);
		tessellator.addVertex(x + MIN, y + MIN, z + MAX);
		tessellator.addVertex(x + MIN, y + MAX, z + MAX);
		tessellator.addVertex(x + MIN, y + MAX, z + MIN);

		tessellator.addVertex(x + MAX, y + MAX, z + MIN);
		tessellator.addVertex(x + MAX, y + MAX, z + MAX);
		tessellator.addVertex(x + MAX, y + MIN, z + MAX);
		tessellator.addVertex(x + MAX, y + MIN, z + MIN);

		tessellator.addVertex(x + MIN, y + MIN, z + MIN);
		tessellator.addVertex(x + MIN, y + MAX, z + MIN);
		tessellator.addVertex(x + MAX, y + MAX, z + MIN);
		tessellator.addVertex(x + MAX, y + MIN, z + MIN);

		tessellator.addVertex(x + MIN, y + MAX, z + MAX);
		tessellator.addVertex(x + MIN, y + MIN, z + MAX);
		tessellator.addVertex(x + MAX, y + MIN, z + MAX);
		tessellator.addVertex(x + MAX, y + MAX, z + MAX);

		tessellator.addVertex(x + MIN, y + MIN, z + MAX);
		tessellator.addVertex(x + MIN, y + MIN, z + MIN);
		tessellator.addVertex(x + MAX, y + MIN, z + MIN);
		tessellator.addVertex(x + MAX, y + MIN, z + MAX);
	}
}
