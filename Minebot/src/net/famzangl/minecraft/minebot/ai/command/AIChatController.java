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

import java.util.List;

import net.famzangl.minecraft.minebot.ai.commands.CommandAirbridge;
import net.famzangl.minecraft.minebot.ai.commands.CommandBuildWay;
import net.famzangl.minecraft.minebot.ai.commands.CommandCraft;
import net.famzangl.minecraft.minebot.ai.commands.CommandDumpSigns;
import net.famzangl.minecraft.minebot.ai.commands.CommandEat;
import net.famzangl.minecraft.minebot.ai.commands.CommandEnchant;
import net.famzangl.minecraft.minebot.ai.commands.CommandFeed;
import net.famzangl.minecraft.minebot.ai.commands.CommandFillArea;
import net.famzangl.minecraft.minebot.ai.commands.CommandFish;
import net.famzangl.minecraft.minebot.ai.commands.CommandFurnace;
import net.famzangl.minecraft.minebot.ai.commands.CommandGet;
import net.famzangl.minecraft.minebot.ai.commands.CommandHelp;
import net.famzangl.minecraft.minebot.ai.commands.CommandKill;
import net.famzangl.minecraft.minebot.ai.commands.CommandLoad;
import net.famzangl.minecraft.minebot.ai.commands.CommandLookAt;
import net.famzangl.minecraft.minebot.ai.commands.CommandLumberjack;
import net.famzangl.minecraft.minebot.ai.commands.CommandMine;
import net.famzangl.minecraft.minebot.ai.commands.CommandPathfind;
import net.famzangl.minecraft.minebot.ai.commands.CommandPause;
import net.famzangl.minecraft.minebot.ai.commands.CommandPlant;
import net.famzangl.minecraft.minebot.ai.commands.CommandRenderMap;
import net.famzangl.minecraft.minebot.ai.commands.CommandRespawn;
import net.famzangl.minecraft.minebot.ai.commands.CommandResume;
import net.famzangl.minecraft.minebot.ai.commands.CommandRun;
import net.famzangl.minecraft.minebot.ai.commands.CommandSettings;
import net.famzangl.minecraft.minebot.ai.commands.CommandShear;
import net.famzangl.minecraft.minebot.ai.commands.CommandSit;
import net.famzangl.minecraft.minebot.ai.commands.CommandStop;
import net.famzangl.minecraft.minebot.ai.commands.CommandStore;
import net.famzangl.minecraft.minebot.ai.commands.CommandTestMinectaft;
import net.famzangl.minecraft.minebot.ai.commands.CommandTint;
import net.famzangl.minecraft.minebot.ai.commands.CommandTunnel;
import net.famzangl.minecraft.minebot.ai.commands.CommandUngrab;
import net.famzangl.minecraft.minebot.ai.commands.CommandWalk;
import net.famzangl.minecraft.minebot.ai.commands.CommandXPFarm;
import net.famzangl.minecraft.minebot.ai.scripting.CommandJs;
import net.famzangl.minecraft.minebot.build.commands.CommandBuild;
import net.famzangl.minecraft.minebot.build.commands.CommandClearArea;
import net.famzangl.minecraft.minebot.build.commands.CommandCount;
import net.famzangl.minecraft.minebot.build.commands.CommandExpand;
import net.famzangl.minecraft.minebot.build.commands.CommandListBuilds;
import net.famzangl.minecraft.minebot.build.commands.CommandMove;
import net.famzangl.minecraft.minebot.build.commands.CommandReset;
import net.famzangl.minecraft.minebot.build.commands.CommandReverse;
import net.famzangl.minecraft.minebot.build.commands.CommandScheduleBuild;
import net.famzangl.minecraft.minebot.build.commands.CommandSetPos;
import net.famzangl.minecraft.minebot.build.commands.CommandStepNext;
import net.famzangl.minecraft.minebot.build.commands.CommandStepPlace;
import net.famzangl.minecraft.minebot.build.commands.CommandStepWalk;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

import com.google.common.base.Function;

/**
 * Controlls the AI from a chat line.
 * 
 * @author michael
 * 
 */
public class AIChatController {
	private static final CommandRegistry registry = new CommandRegistry();

	private static final int PER_PAGE = 8;

	static {
		registerCommand(CommandHelp.class);
		registerCommand(CommandMine.class);
		registerCommand(CommandTunnel.class);
		registerCommand(CommandStop.class);
		registerCommand(CommandResume.class);
		registerCommand(CommandUngrab.class);
		registerCommand(CommandRun.class);
		registerCommand(CommandLoad.class);
		registerCommand(CommandJs.class);
		registerCommand(CommandPlant.class);
		registerCommand(CommandLumberjack.class);
		registerCommand(CommandTint.class);
		registerCommand(CommandBuildWay.class);
		registerCommand(CommandShear.class);
		registerCommand(CommandPause.class);
		registerCommand(CommandStore.class);
		registerCommand(CommandFurnace.class);
		registerCommand(CommandGet.class);
		registerCommand(CommandCraft.class);
		registerCommand(CommandEnchant.class);
		registerCommand(CommandEat.class);
		registerCommand(CommandRespawn.class);
		registerCommand(CommandXPFarm.class);
		registerCommand(CommandAirbridge.class);
		
		registerCommand(CommandWalk.class);
		registerCommand(CommandLookAt.class);
		registerCommand(CommandPathfind.class);

		registerCommand(CommandKill.class);
		registerCommand(CommandFish.class);
		registerCommand(CommandSit.class);
		registerCommand(CommandFeed.class);
		registerCommand(CommandSettings.class);
		
		registerCommand(CommandDumpSigns.class);
		registerCommand(CommandRenderMap.class);
		registerCommand(CommandTestMinectaft.class);


		registerCommand(CommandBuild.class);
		registerCommand(CommandClearArea.class);
		registerCommand(CommandFillArea.class);
		registerCommand(CommandReset.class);
		registerCommand(CommandScheduleBuild.class);
		registerCommand(CommandSetPos.class);
		registerCommand(CommandExpand.class);
		registerCommand(CommandStepNext.class);
		registerCommand(CommandStepPlace.class);
		registerCommand(CommandStepWalk.class);
		registerCommand(CommandListBuilds.class);
		registerCommand(CommandReverse.class);
		registerCommand(CommandMove.class);
		registerCommand(CommandCount.class);
	}

	private AIChatController() {
	}

	private static void registerCommand(Class<?> commandClass) {
		registry.register(commandClass);
	}

	public static void addChatLine(String message) {
		addToChat("[Minebot] " + message);
	}

	public static CommandRegistry getRegistry() {
		return registry;
	}

	private static void addToChat(String string) {
		Minecraft.getMinecraft().thePlayer
				.addChatMessage(new ChatComponentText(string));
	}

	public static <T> void addToChatPaged(String title, int page, List<T> data,
			Function<T, String> convert) {
		AIChatController.addChatLine(title + " " + page + " / "
				+ (int) Math.ceil((float) data.size() / PER_PAGE));
		for (int i = Math.max(0, page - 1) * PER_PAGE; i < Math.min(page
				* PER_PAGE, data.size()); i++) {
			final String line = convert.apply(data.get(i));
			addChatLine(line);
		}
	}

}
