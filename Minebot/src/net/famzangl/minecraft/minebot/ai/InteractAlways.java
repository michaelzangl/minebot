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
package net.famzangl.minecraft.minebot.ai;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import net.minecraft.client.settings.KeyBinding;

/**
 * A key binding that is always pressed.
 * 
 * @author michael
 * 
 */
public final class InteractAlways extends KeyBinding {
	private static final Marker MARKER_KEY = MarkerManager.getMarker("key");
	private static final Logger LOGGER = LogManager
			.getLogger(InteractAlways.class);
	private boolean isPressed;

	InteractAlways(String p_i45001_1_, int p_i45001_2_, String p_i45001_3_,
			boolean pressed) {
		super(p_i45001_1_, p_i45001_2_, p_i45001_3_);
		isPressed = pressed;
	}

	@Override
	public boolean isKeyDown() {
		return true;
	}

	@Override
	public boolean isPressed() {
		final boolean ret = isPressed;
		if (isPressed) {
			LOGGER.debug(MARKER_KEY, "Sending key isPressed() for "
					+ getKeyCode());
		}
		isPressed = false;
		return ret;
	}
}
