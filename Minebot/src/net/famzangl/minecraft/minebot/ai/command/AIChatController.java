package net.famzangl.minecraft.minebot.ai.command;

import java.util.ArrayList;
import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIStrategy;
import net.famzangl.minecraft.minebot.ai.animals.CommandFeed;
import net.famzangl.minecraft.minebot.ai.animals.CommandKill;
import net.famzangl.minecraft.minebot.build.CommandBuild;
import net.famzangl.minecraft.minebot.build.CommandClear;
import net.famzangl.minecraft.minebot.build.CommandListBuild;
import net.famzangl.minecraft.minebot.build.CommandScheduleBuild;
import net.famzangl.minecraft.minebot.build.CommandStepNext;
import net.famzangl.minecraft.minebot.build.CommandStepPlace;
import net.famzangl.minecraft.minebot.build.CommandStepWalk;
import net.famzangl.minecraft.minebot.build.reverse.CommandReverse;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.ClientCommandHandler;

/**
 * Controlls the AI from a chat line.
 * 
 * @author michael
 * 
 */
public class AIChatController extends CommandBase {
	public static AIChatController runningInstance = null;

	private IAIControllable controlled;

	private static ArrayList<AICommand> commands = new ArrayList<AICommand>();

	static {
		registerCommand(new Usage());
		registerCommand(new Help());
		
		registerCommand(new CommandUngrab());
		registerCommand(new CommandStop());
		registerCommand(new CommandRun());
		registerCommand(new CommandSetPos(false));
		registerCommand(new CommandSetPos(true));
		
		registerCommand(new CommandMine());
		registerCommand(new CommandFish());
		registerCommand(new CommandFeed());
		registerCommand(new CommandKill());

		registerCommand(new CommandScheduleBuild());
		registerCommand(new CommandListBuild());
		registerCommand(new CommandStepNext());
		registerCommand(new CommandStepWalk());
		registerCommand(new CommandStepPlace());
		registerCommand(new CommandClear());
		registerCommand(new CommandBuild());
		registerCommand(new CommandReverse());
	}

	public AIChatController(IAIControllable c) {
		this.controlled = c;
		ClientCommandHandler.instance.registerCommand(this);
		System.out.println("Command registered.");
		runningInstance = this;
	}

	private static void registerCommand(AICommand aiCommand) {
		commands.add(aiCommand);
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if (args.length < 1) {
			usage();
		} else {
			AICommand command = getCommand(args[0]);
			if (command == null) {
				usage();
			} else {
				try {
					AIStrategy strategy = command.evaluateCommand(sender, args,
							controlled.getAiHelper(), this);
					if (strategy != null) {
						controlled.requestUseStrategy(strategy);
					}
				} catch (Throwable t) {
					addChatLine("Error (please report): " + t.getMessage());
					t.printStackTrace();
				}
			}
		}
	}

	public AICommand getCommand(String name) {
		for (AICommand c : commands) {
			if (c.getName().equals(name)) {
				return c;
			}
		}
		return null;
	}

	public void usage() {
		for (AICommand c : commands) {
			usage(c);
		}
	}

	public void usage(AICommand c) {
		addToChat("/" + getCommandName() + " " + c.getName() + " "
				+ c.getArgsUsage());
	}

	@Override
	public String getCommandUsage(ICommandSender var1) {
		return "See /minebot usage";
	}

	@Override
	public String getCommandName() {
		return "minebot";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}

	public static void addChatLine(String message) {
		if (runningInstance != null) {
			runningInstance.addToChat("[Minebot] " + message);
		}
	}

	private void addToChat(String string) {
		controlled.getMinecraft().thePlayer
				.addChatMessage(new ChatComponentText(string));
	}

	public static Pos parsePos(ICommandSender sender, String[] args, int offset) {
		int i = sender.getPlayerCoordinates().posX;
		int j = sender.getPlayerCoordinates().posY - 2;
		int k = sender.getPlayerCoordinates().posZ;
		i = MathHelper.floor_double(func_110666_a(sender, i,
				args[offset + 0]));
		j = MathHelper.floor_double(func_110666_a(sender, j,
				args[offset + 1]));
		k = MathHelper.floor_double(func_110666_a(sender, k,
				args[offset + 2]));
		return new Pos(i, j, k);
	}
}
