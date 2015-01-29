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
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;

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

	public void render(RenderTickEvent event, AIHelper helper,
			BlockPos... markerPos) {
		renderStart(event, helper);

		float part = 1.0f;
		for (final BlockPos m : markerPos) {
			if (m != null) {
				renderMarker(m, r * part + (1 - part), g * part + (1 - part), b
						* part + (1 - part), 0.7f * part);
			}
			part *= 0.6;
		}
		renderEnd();
	}

}
