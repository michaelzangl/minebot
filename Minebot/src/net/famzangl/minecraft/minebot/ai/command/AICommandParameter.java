package net.famzangl.minecraft.minebot.ai.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface AICommandParameter {
	ParameterType type();

	String description();

	String fixedName() default "";
	
	boolean optional() default false;
}
