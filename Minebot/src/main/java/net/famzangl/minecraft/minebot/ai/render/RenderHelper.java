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

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.GlStateManager;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.TickEvent;
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

    public static final VertexFormat VF = new VertexFormat(ImmutableList.of(
    				new VertexFormatElement(0, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.POSITION, 3),
        	new VertexFormatElement(0, VertexFormatElement.Type.UBYTE, VertexFormatElement.Usage.COLOR, 4)
	));

	public void renderStart(TickEvent.RenderTickEvent event, AIHelper helper) {
		final Entity player = helper.getMinecraft().getRenderViewEntity();
		if (player == null) {
			return;
		}
		final double x = player.lastTickPosX
				+ (player.getPosX() - player.lastTickPosX) * event.renderTickTime;
		final double y = player.lastTickPosY
				+ (player.getPosY() - player.lastTickPosY) * event.renderTickTime;
		final double z = player.lastTickPosZ
				+ (player.getPosZ() - player.lastTickPosZ) * event.renderTickTime;

		preRender();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder worldrenderer = tessellator.getBuffer();
        worldrenderer.setTranslation(-x, -y, -z);
        worldrenderer.begin(GL11.GL_QUADS, VF);
       // worldrenderer.markDirty();
	}

    private void preRender()
    {
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_DST_COLOR, GL11.GL_SRC_COLOR, 1, 0);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.doPolygonOffset(-3.0F, -3.0F);
        GlStateManager.enablePolygonOffset();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.enableAlpha();
        GlStateManager.pushMatrix();
    }

    private void postRender()
    {
        GlStateManager.disableAlpha();
        GlStateManager.doPolygonOffset(0.0F, 0.0F);
        GlStateManager.disablePolygonOffset();
        GlStateManager.enableAlpha();
        GlStateManager.depthMask(true);
        GlStateManager.popMatrix();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
    }
	protected void renderEnd() {
		final Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder worldrenderer = tessellator.getBuffer();
		tessellator.draw();
		worldrenderer.setTranslation(0.0D, 0.0D, 0.0D);
		postRender();
	}

	protected void renderMarker(BlockPos m, float r, float g, float b, float a) {
		final Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder renderer = tessellator.getBuffer();
		renderer.color(r, g, b, a);
		renderMarkerP(renderer, m.getX(), m.getY(), m.getZ());
	}

	private void renderMarkerP(BufferBuilder worldRenderer, int x, int y, int z) {
		worldRenderer.pos(x + MIN, y + MAX, z + MIN);
		worldRenderer.pos(x + MIN, y + MAX, z + MAX);
		worldRenderer.pos(x + MAX, y + MAX, z + MAX);
		worldRenderer.pos(x + MAX, y + MAX, z + MIN);

		worldRenderer.pos(x + MIN, y + MIN, z + MIN);
		worldRenderer.pos(x + MIN, y + MIN, z + MAX);
		worldRenderer.pos(x + MIN, y + MAX, z + MAX);
		worldRenderer.pos(x + MIN, y + MAX, z + MIN);

		worldRenderer.pos(x + MAX, y + MAX, z + MIN);
		worldRenderer.pos(x + MAX, y + MAX, z + MAX);
		worldRenderer.pos(x + MAX, y + MIN, z + MAX);
		worldRenderer.pos(x + MAX, y + MIN, z + MIN);

		worldRenderer.pos(x + MIN, y + MIN, z + MIN);
		worldRenderer.pos(x + MIN, y + MAX, z + MIN);
		worldRenderer.pos(x + MAX, y + MAX, z + MIN);
		worldRenderer.pos(x + MAX, y + MIN, z + MIN);

		worldRenderer.pos(x + MIN, y + MAX, z + MAX);
		worldRenderer.pos(x + MIN, y + MIN, z + MAX);
		worldRenderer.pos(x + MAX, y + MIN, z + MAX);
		worldRenderer.pos(x + MAX, y + MAX, z + MAX);

		worldRenderer.pos(x + MIN, y + MIN, z + MAX);
		worldRenderer.pos(x + MIN, y + MIN, z + MIN);
		worldRenderer.pos(x + MAX, y + MIN, z + MIN);
		worldRenderer.pos(x + MAX, y + MIN, z + MAX);
	}
}
