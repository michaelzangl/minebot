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
package net.famzangl.minecraft.minebot.ai.command;

import com.google.common.base.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.StringTextComponent;

import java.util.List;

/**
 * Controlls the AI from a chat line.
 * 
 * @author michael
 * 
 */
public class AIChatController {
	private static final CommandRegistry registry = new CommandRegistry();

	private static final int PER_PAGE = 8;


	private AIChatController() {
	}

	public static void addChatLine(String message) {
		addToChat("[Minebot] " + message);
	}

	public static CommandRegistry getRegistry() {
		return registry;
	}

	private static void addToChat(String string) {
		Minecraft.getInstance().player
				.sendMessage(new StringTextComponent(string));
	}

	public static <T> void addToChatPaged(String title, int page, List<T> data,
			Function<T, String> convert) {

		int totalPages = (int) Math.ceil(1.0f + data.size() / PER_PAGE);
		AIChatController.addChatLine(title + " " + page + " / " + totalPages);

		int start = Math.max(0, page - 1) * PER_PAGE;
		int end = Math.min(page * PER_PAGE, data.size());
		for (int i = start; i < end; i++) {
			final String line = convert.apply(data.get(i));
			addChatLine(line);
		}
	}

}
