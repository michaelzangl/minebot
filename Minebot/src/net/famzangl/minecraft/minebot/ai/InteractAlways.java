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

import net.minecraft.client.settings.KeyBinding;

/**
 * A key binding that is always pressed.
 * 
 * @author michael
 * 
 */
public final class InteractAlways extends KeyBinding {
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
			System.out.println("Sending key press event.");
		}
		isPressed = false;
		return ret;
	}
}
