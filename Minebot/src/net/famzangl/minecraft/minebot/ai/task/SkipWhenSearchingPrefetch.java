package net.famzangl.minecraft.minebot.ai.task;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This states that the given task may be ignored when searching for prefetch
 * tasks.
 * 
 * See {@link CanPrefaceAndDestroy}.
 * 
 * @author michael
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface SkipWhenSearchingPrefetch {

}
