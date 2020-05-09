package net.famzangl.minecraft.minebot.ai.net;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Function;

public interface Intercepts<N> {

    default <T extends N> void withInterceptors(T packet, Consumer<T> consumesPacket) {
        T result = intercept(packet).getResultingPacket(packet);
        if (result != null) {
            consumesPacket.accept(packet);
        }
    }

    <U extends N> InterceptResult<U> intercept(U packet);

    abstract class InterceptResult<T> {
        private InterceptResult() {}

        @Nullable
        abstract T getResultingPacket(@Nonnull T packet);

        public InterceptResult<T> then(Function<T, InterceptResult<T>> next) {
            InterceptResult<T> me = this;
            return new InterceptResult<T>() {
                @Nullable
                @Override
                T getResultingPacket(@Nonnull T packet) {
                    T first = me.getResultingPacket(packet);
                    return first == null ? null : next.apply(first).getResultingPacket(first);
                }
            };
        }

        public static <T> InterceptResult<T> pass() {
            return new InterceptResult<T>() {
                @Nullable
                @Override
                T getResultingPacket(@Nonnull T packet) {
                    return packet;
                }
            };
        }

        public static <T> InterceptResult<T> drop() {
            return new InterceptResult<T>() {
                @Nullable
                @Override
                T getResultingPacket(@Nonnull T packet) {
                    return null;
                }
            };
        }

        public static <T> InterceptResult<T> replaceBy(T replaced) {
            return new InterceptResult<T>() {
                @Nullable
                @Override
                T getResultingPacket(@Nonnull T packet) {
                    return replaced;
                }
            };
        }

    }
}
