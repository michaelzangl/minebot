package net.famzangl.minecraft.minebot.ai.enchanting;

import java.lang.reflect.Field;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.minecraft.client.gui.GuiEnchantment;
import net.minecraft.inventory.ContainerEnchantment;

public class SelectEnchantmentTask implements AITask {

	private static final int E_SLOT = 2;

	@Override
	public boolean isFinished(AIHelper h) {
		if (!(h.getMinecraft().currentScreen instanceof GuiEnchantment)) {
			return false;
		} else {
			final GuiEnchantment screen = (GuiEnchantment) h.getMinecraft().currentScreen;
			return screen.inventorySlots.getSlot(0).getHasStack()
					&& screen.inventorySlots.getSlot(0).getStack()
							.isItemEnchanted();
		}
	}

	@Override
	public void runTick(AIHelper h) {
		if (!(h.getMinecraft().currentScreen instanceof GuiEnchantment)) {
			System.out.println("Screen not opened.");
			h.desync();
			return;
		}
		final GuiEnchantment screen = (GuiEnchantment) h.getMinecraft().currentScreen;
		if (!screen.inventorySlots.getSlot(0).getHasStack()) {
			System.out.println("No stack in slot.");
			h.desync();
			return;
		}
		if (screen.inventorySlots.getSlot(0).getStack().isItemEnchanted()) {
			System.out.println("Already enchanted.");
			return;
		}

		try {
			final Field field = GuiEnchantment.class
					.getDeclaredField("field_147075_G");
			field.setAccessible(true);
			final ContainerEnchantment c = (ContainerEnchantment) field
					.get(screen);

			if (c.enchantLevels[E_SLOT] == 0) {
				System.out.println("No enchantment levels computed yet.");
				return;
			}
			if (h.getMinecraft().thePlayer.experienceLevel < c.enchantLevels[E_SLOT]) {
				System.out.println("Abort enchantment, not enough levels.");
				return;
			}
			if (c.enchantItem(h.getMinecraft().thePlayer, E_SLOT)) {
				h.getMinecraft().playerController.sendEnchantPacket(c.windowId,
						E_SLOT);
			}
			System.out.println("Sent enchant request package.");
			return;
		} catch (final Throwable e) {
			e.printStackTrace();
			h.desync();
			return;
		}
	}

}
