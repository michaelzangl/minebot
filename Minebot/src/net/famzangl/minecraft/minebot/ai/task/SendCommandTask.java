package net.famzangl.minecraft.minebot.ai.task;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;

public class SendCommandTask implements AITask {
	private final String command;
	private boolean send;

	public SendCommandTask(String command) {
		super();
		this.command = command;
	}

	@Override
	public boolean isFinished(AIHelper h) {
		return send;
	}

	@Override
	public void runTick(AIHelper h) {
		if (!send && h.getMinecraft().ingameGUI.getChatGUI() != null) {
			GuiChat chat = new GuiChat();
            h.getMinecraft().displayGuiScreen(chat);
			chat.func_146403_a(command);
			h.getMinecraft().displayGuiScreen((GuiScreen)null);
			send = true;
		}
	}
}
