package net.famzangl.minecraft.minebot.ai.net;

import net.famzangl.minecraft.minebot.ai.net.Intercepts.InterceptResult;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.INetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.IServerPlayNetHandler;

import java.util.HashMap;
import java.util.function.Function;

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
            Class<T> type, Function<T, InterceptResult<T>> handler
    ) {
        incoming.add(type, handler);
    }

    public <T extends IPacket<IServerPlayNetHandler>> void addOutgoingInterceptor(
            Class<T> type, Function<T, InterceptResult<T>> handler) {
        outgoing.add(type, handler);
    }

    public Intercepts<IPacket<IClientPlayNetHandler>> getIncoming() {
        return incoming;
    }

    public Intercepts<IPacket<IServerPlayNetHandler>> getOutgoing() {
        return outgoing;
    }

    private static class Interceptors<N extends INetHandler> implements Intercepts<IPacket<N>> {
        private final HashMap<Class<? extends IPacket<N>>, InterceptorFunction<? extends IPacket<N>>> handlers = new HashMap<>();

        <T extends IPacket<N>> void add(Class<T> type, Function<T, InterceptResult<T>> handler) {
            InterceptorFunction<T> typesafeHandler = handler::apply;
            handlers.compute(type, (__, oldHandler) -> oldHandler == null ? typesafeHandler :
                    packet -> applyHandler(packet, oldHandler).then(newPacket -> applyHandler(newPacket, typesafeHandler)));
        }

        @Override
        public <U extends IPacket<N>> InterceptResult<U> intercept(U packet) {
            InterceptorFunction<? extends IPacket<N>> handler = handlers.get(packet.getClass());
            return handler == null ? InterceptResult.pass() : applyHandler(packet, handler);
        }


        private <T extends IPacket<N>> InterceptResult<T> applyHandler(IPacket<N> packet,
                                                                       InterceptorFunction<? extends IPacket<N>> handler) {
            return ((InterceptorFunction<T>)handler).apply((T)packet);
        }
    }

    interface InterceptorFunction<T> extends Function<T, InterceptResult<T>> {}

}
