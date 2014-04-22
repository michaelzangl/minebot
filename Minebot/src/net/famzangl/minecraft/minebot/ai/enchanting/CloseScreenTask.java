package net.famzangl.minecraft.minebot.ai.enchanting;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.minecraft.client.gui.GuiScreen;

public class CloseScreenTask implements AITask {

	@Override
	public boolean isFinished(AIHelper h) {
		return h.getMinecraft().currentScreen == null;
	}

	@Override
	public void runTick(AIHelper h) {
		h.getMinecraft().displayGuiScreen((GuiScreen) null);
		h.getMinecraft().setIngameFocus();
	}

}
