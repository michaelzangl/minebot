/*******************************************************************************
 * This file is part of Minebot.
 *
 * Minebot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Minebot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Minebot.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package net.famzangl.minecraft.minebot.ai.render;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;

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

	public void renderStart(RenderTickEvent event, AIHelper helper) {
		final Tessellator tessellator = Tessellator.getInstance();
		final Entity player = helper.getMinecraft().getRenderViewEntity();
		final double x = player.lastTickPosX
				+ (player.posX - player.lastTickPosX) * event.renderTickTime;
		final double y = player.lastTickPosY
				+ (player.posY - player.lastTickPosY) * event.renderTickTime;
		final double z = player.lastTickPosZ
				+ (player.posZ - player.lastTickPosZ) * event.renderTickTime;

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
		tessellator.getWorldRenderer().startDrawingQuads();
		tessellator.getWorldRenderer().setTranslation(-x, -y, -z);
	}

	protected void renderEnd() {
		final Tessellator tessellator = Tessellator.getInstance();

		tessellator.draw();
		tessellator.getWorldRenderer().setTranslation(0.0D, 0.0D, 0.0D);
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

	protected void renderMarker(BlockPos m, float r, float g, float b, float a) {
		final Tessellator tessellator = Tessellator.getInstance();
		tessellator.getWorldRenderer().setColorRGBA_F(r, g, b, a);
		renderMarkerP(tessellator.getWorldRenderer(), m.getX(), m.getY(), m.getZ());
	}

	private void renderMarkerP(WorldRenderer worldRenderer, int x, int y, int z) {
		worldRenderer.addVertex(x + MIN, y + MAX, z + MIN);
		worldRenderer.addVertex(x + MIN, y + MAX, z + MAX);
		worldRenderer.addVertex(x + MAX, y + MAX, z + MAX);
		worldRenderer.addVertex(x + MAX, y + MAX, z + MIN);

		worldRenderer.addVertex(x + MIN, y + MIN, z + MIN);
		worldRenderer.addVertex(x + MIN, y + MIN, z + MAX);
		worldRenderer.addVertex(x + MIN, y + MAX, z + MAX);
		worldRenderer.addVertex(x + MIN, y + MAX, z + MIN);

		worldRenderer.addVertex(x + MAX, y + MAX, z + MIN);
		worldRenderer.addVertex(x + MAX, y + MAX, z + MAX);
		worldRenderer.addVertex(x + MAX, y + MIN, z + MAX);
		worldRenderer.addVertex(x + MAX, y + MIN, z + MIN);

		worldRenderer.addVertex(x + MIN, y + MIN, z + MIN);
		worldRenderer.addVertex(x + MIN, y + MAX, z + MIN);
		worldRenderer.addVertex(x + MAX, y + MAX, z + MIN);
		worldRenderer.addVertex(x + MAX, y + MIN, z + MIN);

		worldRenderer.addVertex(x + MIN, y + MAX, z + MAX);
		worldRenderer.addVertex(x + MIN, y + MIN, z + MAX);
		worldRenderer.addVertex(x + MAX, y + MIN, z + MAX);
		worldRenderer.addVertex(x + MAX, y + MAX, z + MAX);

		worldRenderer.addVertex(x + MIN, y + MIN, z + MAX);
		worldRenderer.addVertex(x + MIN, y + MIN, z + MIN);
		worldRenderer.addVertex(x + MAX, y + MIN, z + MIN);
		worldRenderer.addVertex(x + MAX, y + MIN, z + MAX);
	}
}
