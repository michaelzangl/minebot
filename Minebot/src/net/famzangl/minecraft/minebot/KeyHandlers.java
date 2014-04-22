package net.famzangl.minecraft.minebot;

import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;

public class KeyHandlers {
	private KeyBinding feed_unsit = new KeyBinding("Feed or unsit",
			Keyboard.getKeyIndex("F"), "Command Mod");
	private KeyBinding feed_sit = new KeyBinding("Feed or sit",
			Keyboard.getKeyIndex("G"), "Command Mod");
	private KeyBinding feed_unsit_curr = new KeyBinding(
			"Feed or unsit (current)", Keyboard.getKeyIndex("V"), "Command Mod");
	private KeyBinding feed_sit_curr = new KeyBinding("Feed or sit (current)",
			Keyboard.getKeyIndex("B"), "Command Mod");

	public KeyHandlers() {
		ClientRegistry.registerKeyBinding(feed_unsit);
		ClientRegistry.registerKeyBinding(feed_sit);
		ClientRegistry.registerKeyBinding(feed_unsit_curr);
		ClientRegistry.registerKeyBinding(feed_sit_curr);
	}

	@SubscribeEvent
	public void KeyInputEvent(KeyInputEvent event) {
		if (feed_unsit.isPressed()) {
			FeedKeyHandler.Pressed(event, false, false);
		} else if (feed_sit.isPressed()) {
			FeedKeyHandler.Pressed(event, true, false);
		} else if (feed_unsit_curr.isPressed()) {
			FeedKeyHandler.Pressed(event, false, true);
		} else if (feed_sit_curr.isPressed()) {
			FeedKeyHandler.Pressed(event, true, true);
		}
	}
}
