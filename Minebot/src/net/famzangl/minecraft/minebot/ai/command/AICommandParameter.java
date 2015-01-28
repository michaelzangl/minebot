package net.famzangl.minecraft.minebot.ai.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.minecraft.block.Block;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface AICommandParameter {
	/**
	 * Subclasses need to have an empty constructor.
	 * @author michael
	 *
	 */
	public static abstract class BlockFilter {
		public abstract boolean matches(Block b);
	}
	
	public static class AnyBlockFilter extends BlockFilter {
		@Override
		public boolean matches(Block b) {
			return true;
		}
	}
	
	ParameterType type();

	String description();

	String fixedName() default "";
	
	boolean optional() default false;

	Class<? extends BlockFilter> blockFilter() default AnyBlockFilter.class;

	String relativeToSettingsFile() default "";
}
