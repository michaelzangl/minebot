package net.famzangl.minecraft.minebot.ai.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AICommandInvocation {
	SafeStrategyRule safeRule() default SafeStrategyRule.NONE;

}
