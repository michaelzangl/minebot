package net.famzangl.minecraft.minebot.ai.net;

import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.util.text.ITextComponent;

public class PersistentChat {

    private final ITextComponent message;

    private final boolean chat;

    private final long time = System.currentTimeMillis();

    public PersistentChat(SChatPacket packetIn) {
        chat = !packetIn.isSystem();
        message = packetIn.getChatComponent();
    }

    public ITextComponent getMessage() {
        return message;
    }

    public boolean isChat() {
        return chat;
    }

    public long getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "PersistentChat [message=" + message + ", chat=" + chat
                + "]";
    }

}
