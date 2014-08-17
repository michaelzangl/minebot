package net.famzangl.minecraft.minebot.ai.task;

import java.util.Arrays;
import java.util.List;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;

@SkipWhenSearchingPrefetch
public class SendCommandTask extends AITask {
	private final List<String> commands;
	private boolean send;

	public SendCommandTask(List<String> commands) {
		super();
		this.commands = commands;
	}

	public SendCommandTask(String string) {
		this(Arrays.asList(string));
	}

	@Override
	public boolean isFinished(AIHelper h) {
		return send;
	}

	@Override
	public void runTick(AIHelper h) {
		if (!send && h.getMinecraft().ingameGUI.getChatGUI() != null) {
			final GuiChat chat = new GuiChat();
			h.getMinecraft().displayGuiScreen(chat);
			for (final String command : commands) {
				chat.func_146403_a(command);
			}
			h.getMinecraft().displayGuiScreen((GuiScreen) null);
			send = true;
		}
	}
}
