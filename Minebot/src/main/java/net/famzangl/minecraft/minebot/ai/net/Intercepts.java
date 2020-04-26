package net.famzangl.minecraft.minebot.ai.net;

import net.minecraft.network.IPacket;

public interface Intercepts<T> {

    EInterceptResult intercept(T packet);

    enum EInterceptResult { PASS, DROP }
}
