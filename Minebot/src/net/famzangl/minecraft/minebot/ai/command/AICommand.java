package net.famzangl.minecraft.minebot.ai.command;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.AIStrategy;
import net.minecraft.command.ICommandSender;

/**
 * An AI command that does something.
 * 
 * @author michael
 * 
 */
public interface AICommand {
	public String getName();

	/**
	 * args to use. [] define optional args.
	 * 
	 * @return
	 */
	public String getArgsUsage();
	
	public String getHelpText();

	/**
	 * 
	 * @param sender TODO
	 * @param args Args, where agrs[0] is the name.
	 * @param h
	 * @param aiChatController 
	 * @return A Strategy or <code>null</code> if no strategy should be started.
	 */
	public AIStrategy evaluateCommand(ICommandSender sender, String[] args, AIHelper h, AIChatController aiChatController);
}
