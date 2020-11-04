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
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.minecraft.client.renderer.BufferBuilder;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.event.TickEvent;
import org.lwjgl.opengl.GL11;

/**
 * Helps rendering markers.
 * 
 * @author michael
 * 
 */
public class RenderHelper {

	private static final float MAX = 1.05f;
	private static final float MIN = -0.05f;

    public static final VertexFormat VF = new VertexFormat(ImmutableList.of(
    				new VertexFormatElement(0, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.POSITION, 3),
        	new VertexFormatElement(0, VertexFormatElement.Type.UBYTE, VertexFormatElement.Usage.COLOR, 4)
	));
	private Matrix4f matrix;

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
        matrix = new Matrix4f();
        matrix.translate(new Vector3f((float)-x, (float)-y, (float)-z));
        worldrenderer.begin(GL11.GL_QUADS, VF);
       // worldrenderer.markDirty();
	}

    private void preRender()
    {
    	// TODO: See net.minecraft.client.renderer.WorldRenderer.renderWorldBorder

    }

    private void postRender()
    {
    	// TODO …
    }
	protected void renderEnd() {
		final Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder worldrenderer = tessellator.getBuffer();
		tessellator.draw();
		postRender();
	}

	protected void renderMarker(BlockPos m, float r, float g, float b, float a) {
		final Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder renderer = tessellator.getBuffer();
		renderer.color(r, g, b, a);
		renderMarkerP(renderer, m.getX(), m.getY(), m.getZ());
	}

	private void renderMarkerP(BufferBuilder worldRenderer, int x, int y, int z) {
		worldRenderer.pos(matrix, x + MIN, y + MAX, z + MIN).endVertex();
		worldRenderer.pos(matrix, x + MIN, y + MAX, z + MAX).endVertex();;
		worldRenderer.pos(matrix, x + MAX, y + MAX, z + MAX).endVertex();;
		worldRenderer.pos(matrix, x + MAX, y + MAX, z + MIN).endVertex();;

		worldRenderer.pos(matrix, x + MIN, y + MIN, z + MIN).endVertex();;
		worldRenderer.pos(matrix, x + MIN, y + MIN, z + MAX).endVertex();;
		worldRenderer.pos(matrix, x + MIN, y + MAX, z + MAX).endVertex();;
		worldRenderer.pos(matrix, x + MIN, y + MAX, z + MIN).endVertex();;

		worldRenderer.pos(matrix, x + MAX, y + MAX, z + MIN).endVertex();;
		worldRenderer.pos(matrix, x + MAX, y + MAX, z + MAX).endVertex();;
		worldRenderer.pos(matrix, x + MAX, y + MIN, z + MAX).endVertex();;
		worldRenderer.pos(matrix, x + MAX, y + MIN, z + MIN).endVertex();;

		worldRenderer.pos(matrix, x + MIN, y + MIN, z + MIN).endVertex();;
		worldRenderer.pos(matrix, x + MIN, y + MAX, z + MIN).endVertex();;
		worldRenderer.pos(matrix, x + MAX, y + MAX, z + MIN).endVertex();;
		worldRenderer.pos(matrix, x + MAX, y + MIN, z + MIN).endVertex();;

		worldRenderer.pos(matrix, x + MIN, y + MAX, z + MAX).endVertex();;
		worldRenderer.pos(matrix, x + MIN, y + MIN, z + MAX).endVertex();;
		worldRenderer.pos(matrix, x + MAX, y + MIN, z + MAX).endVertex();;
		worldRenderer.pos(matrix, x + MAX, y + MAX, z + MAX).endVertex();;

		worldRenderer.pos(matrix, x + MIN, y + MIN, z + MAX).endVertex();;
		worldRenderer.pos(matrix, x + MIN, y + MIN, z + MIN).endVertex();;
		worldRenderer.pos(matrix, x + MAX, y + MIN, z + MIN).endVertex();;
		worldRenderer.pos(matrix, x + MAX, y + MIN, z + MAX).endVertex();;
	}
}
