package net.famzangl.minecraft.minebot.ai.command;

import java.util.List;

import net.famzangl.minecraft.minebot.ai.commands.CommandFeed;
import net.famzangl.minecraft.minebot.ai.commands.CommandFish;
import net.famzangl.minecraft.minebot.ai.commands.CommandGetWood;
import net.famzangl.minecraft.minebot.ai.commands.CommandHelp;
import net.famzangl.minecraft.minebot.ai.commands.CommandKill;
import net.famzangl.minecraft.minebot.ai.commands.CommandMine;
import net.famzangl.minecraft.minebot.ai.commands.CommandPlant;
import net.famzangl.minecraft.minebot.ai.commands.CommandRun;
import net.famzangl.minecraft.minebot.ai.commands.CommandSit;
import net.famzangl.minecraft.minebot.ai.commands.CommandStop;
import net.famzangl.minecraft.minebot.ai.commands.CommandTint;
import net.famzangl.minecraft.minebot.ai.commands.CommandTunnel;
import net.famzangl.minecraft.minebot.ai.commands.CommandUngrab;
import net.famzangl.minecraft.minebot.build.commands.CommandBuild;
import net.famzangl.minecraft.minebot.build.commands.CommandClearArea;
import net.famzangl.minecraft.minebot.build.commands.CommandListBuilds;
import net.famzangl.minecraft.minebot.build.commands.CommandReset;
import net.famzangl.minecraft.minebot.build.commands.CommandReverse;
import net.famzangl.minecraft.minebot.build.commands.CommandScheduleBuild;
import net.famzangl.minecraft.minebot.build.commands.CommandSetPos;
import net.famzangl.minecraft.minebot.build.commands.CommandStepNext;
import net.famzangl.minecraft.minebot.build.commands.CommandStepPlace;
import net.famzangl.minecraft.minebot.build.commands.CommandStepWalk;
import net.minecraft.util.ChatComponentText;

import com.google.common.base.Function;

/**
 * Controlls the AI from a chat line.
 * 
 * @author michael
 * 
 */
public class AIChatController {
	public static AIChatController runningInstance = null;

	private final IAIControllable controlled;

	private static final CommandRegistry registry = new CommandRegistry();

	private static final int PER_PAGE = 8;

	static {
		registerCommand(CommandHelp.class);
		registerCommand(CommandMine.class);
		registerCommand(CommandTunnel.class);
		registerCommand(CommandStop.class);
		registerCommand(CommandUngrab.class);
		registerCommand(CommandRun.class);
		registerCommand(CommandPlant.class);
		registerCommand(CommandGetWood.class);
		registerCommand(CommandTint.class);

		registerCommand(CommandKill.class);
		registerCommand(CommandFish.class);
		registerCommand(CommandSit.class);
		registerCommand(CommandFeed.class);

		registerCommand(CommandBuild.class);
		registerCommand(CommandClearArea.class);
		registerCommand(CommandReset.class);
		registerCommand(CommandScheduleBuild.class);
		registerCommand(CommandSetPos.class);
		registerCommand(CommandStepNext.class);
		registerCommand(CommandStepPlace.class);
		registerCommand(CommandStepWalk.class);
		registerCommand(CommandListBuilds.class);
		registerCommand(CommandReverse.class);
	}

	public AIChatController(IAIControllable c) {
		controlled = c;
		this.registry.setControlled(c);
		runningInstance = this;
	}

	private static void registerCommand(Class<?> commandClass) {
		registry.register(commandClass);
	}

	public static void addChatLine(String message) {
		if (runningInstance != null) {
			runningInstance.addToChat("[Minebot] " + message);
		}
	}

	public static CommandRegistry getRegistry() {
		return registry;
	}

	private void addToChat(String string) {
		controlled.getMinecraft().thePlayer
				.addChatMessage(new ChatComponentText(string));
	}
	
	public static <T> void addToChatPaged(String title, int page, List<T> data, Function<T, String> convert) {
		AIChatController.addChatLine(title + " " + page + " / "
				+ (int) Math.ceil((float) data.size() / PER_PAGE));
		for (int i = Math.max(0, page - 1) * PER_PAGE; i < Math.min(page
				* PER_PAGE, data.size()); i++) {
			String line = convert.apply(data.get(i));
			AIChatController.addChatLine(line);
		}
	}

}
