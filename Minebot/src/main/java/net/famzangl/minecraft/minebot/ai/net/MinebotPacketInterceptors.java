package net.famzangl.minecraft.minebot.ai.net;

import net.famzangl.minecraft.minebot.ai.net.Intercepts.EInterceptResult;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.INetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.IServerPlayNetHandler;

import java.util.HashMap;
import java.util.function.Function;
import java.util.function.Predicate;

public class MinebotPacketInterceptors {
    private final Interceptors<IClientPlayNetHandler> incoming = new Interceptors<>();
    private final Interceptors<IServerPlayNetHandler> outgoing = new Interceptors<>();

    /**
     * Add a handler for an incoming packet
     * @param type The type class
     * @param handler The handler. Returns true if the packet should be passed on, false if it should be discarded
     * @param <T> The type
     */
    public <T extends IPacket<IClientPlayNetHandler>> void addIncomingInterceptor(
            Class<T> type, Function<T, EInterceptResult> handler
    ) {
        incoming.add(type, handler);
    }

    public <T extends IPacket<IServerPlayNetHandler>> void addOutgoingInterceptor(
            Class<T> type, Function<T, EInterceptResult> handler) {
        outgoing.add(type, handler);
    }

    public Intercepts<IPacket<IClientPlayNetHandler>> getIncoming() {
        return incoming;
    }

    public Intercepts<IPacket<IServerPlayNetHandler>> getOutgoing() {
        return outgoing;
    }

    private static class Interceptors<N extends INetHandler> implements Intercepts<IPacket<N>> {
        private final HashMap<Class<? extends IPacket<N>>, Function<? extends IPacket<N>, EInterceptResult>> handlers = new HashMap<>();

        <T extends IPacket<N>> void add(Class<T> type, Function<T, EInterceptResult> handler) {
            handlers.compute(type, (__, oldHandler) -> oldHandler == null ? handler :
                    t -> applyHandler(t, oldHandler) == EInterceptResult.DROP ? EInterceptResult.DROP : applyHandler(t, handler));
        }

        @Override
        public EInterceptResult intercept(IPacket<N> packet) {
            Function<? extends IPacket<N>, EInterceptResult> handler = handlers.get(packet.getClass());
            return handler == null ? EInterceptResult.PASS : applyHandler(packet, handler);
        }

        private <T extends IPacket<N>> EInterceptResult applyHandler(IPacket<N> packet,
                                                                     Function<? extends IPacket<N>, EInterceptResult> handler) {
            return ((Function<T, EInterceptResult>)handler).apply((T)packet);
        }
    }

}
