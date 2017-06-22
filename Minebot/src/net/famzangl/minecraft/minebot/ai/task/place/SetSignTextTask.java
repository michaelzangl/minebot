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
package net.famzangl.minecraft.minebot.ai.task.place;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.famzangl.minecraft.minebot.ai.task.TaskOperations;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.math.BlockPos;

/**
 * Set the text of the sign, assuming that the GUI is open.
 * 
 * @author michael
 *
 */
public class SetSignTextTask extends AITask {
	private boolean guiOpened;
	private int timer = 0;

	private final String[] text = new String[4];
	private final BlockPos pos;

	public SetSignTextTask(BlockPos pos, String[] text) {
		this.pos = pos;
		for (int i = 0; i < this.text.length; i++) {
			if (text.length > i && text[i] != null) {
				this.text[i] = makeTextSafe(text[i]);
			} else {
				this.text[i] = "";
			}
		}
	}

	private String makeTextSafe(String string) {
		StringBuffer res = new StringBuffer();
		for (int i = 0; i < string.length() && res.length() < 15; i++) {
			char c = string.charAt(i);
			if (ChatAllowedCharacters.isAllowedCharacter(c)) {
				res.append(c);
			} else if (c == '§') {
				res.append(' ');
			}
		}
		return res.toString();
	}

	@Override
	public boolean isFinished(AIHelper aiHelper) {
		return guiOpened && aiHelper.getMinecraft().currentScreen == null;
	}

	@Override
	public void runTick(AIHelper aiHelper, TaskOperations taskOperations) {
		if (!guiOpened) {
			// wait
			guiOpened = aiHelper.getMinecraft().currentScreen instanceof GuiEditSign;
		} else {
			if (timer == 0) {
				TileEntitySign sign = (TileEntitySign) aiHelper.getMinecraft().world
						.getTileEntity(pos);
				// sign.signText = text;
			} else if (timer == 5) {
				// GuiEditSign edit = (GuiEditSign)
				// h.getMinecraft().currentScreen;
				aiHelper.getMinecraft().displayGuiScreen(null);
			}
			timer++;
		}

	}

	@Override
	public int getGameTickTimeout(AIHelper helper) {
		return 40;
	}
}
