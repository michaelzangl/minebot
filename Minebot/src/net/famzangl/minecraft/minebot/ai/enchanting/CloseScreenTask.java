package net.famzangl.minecraft.minebot.ai.enchanting;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.famzangl.minecraft.minebot.ai.task.TaskOperations;
import net.minecraft.client.gui.GuiScreen;

public class CloseScreenTask extends AITask {

	@Override
	public boolean isFinished(AIHelper h) {
		return h.getMinecraft().currentScreen == null;
	}

	@Override
	public void runTick(AIHelper h, TaskOperations o) {
		h.getMinecraft().displayGuiScreen((GuiScreen) null);
		h.getMinecraft().setIngameFocus();
	}

}
