package net.famzangl.minecraft.minebot.ai.command;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This represents a command of the AI system. The user can enter the name on
 * the console to run it.
 * <p>
 * Classes of this type always need at least one constructor with a
 * {@link AICommandInvocation}-Annotation.
 * 
 * @author michael
 * 
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AICommand {
	String name();

	String helpText();
}
