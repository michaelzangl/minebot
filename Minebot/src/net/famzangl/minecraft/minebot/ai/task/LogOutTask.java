package net.famzangl.minecraft.minebot.ai.task;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.multiplayer.WorldClient;

public class LogOutTask implements AITask {

	private boolean send;

	@Override
	public boolean isFinished(AIHelper h) {
		return send;
	}

	@Override
	public void runTick(AIHelper h) {
		if (!send) {
			Minecraft mc = h.getMinecraft();

			mc.theWorld.sendQuittingDisconnectingPacket();
			mc.loadWorld((WorldClient) null);
			mc.displayGuiScreen(new GuiMainMenu());
			send = true;
		}
	}

}
