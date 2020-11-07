package net.famzangl.minecraft.minebot.ai.scripting;

import net.famzangl.minecraft.minebot.ai.net.PersistentChat;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

public class ChatMessage {

	public final Object time;
	public final String text;
	public final String textFormatted;
	public final boolean isChat;

	public ChatMessage(PersistentChat m, ScriptEngine engine)
			throws ScriptException {
		time = engine.eval("new Date(" + m.getTime() + ")");
		text = m.getMessage().getUnformattedComponentText();
		textFormatted = m.getMessage().getString();
		isChat = m.isChat();
	}

	@Override
	public String toString() {
		return "ChatMessage [text=" + text + "]";
	}
	
}
