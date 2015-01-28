package net.famzangl.minecraft.minebot.ai.utils;

import java.lang.reflect.Field;

import net.minecraft.item.ItemStack;

/**
 * Utils for accessing private fields of objects.
 * 
 * @author michael
 *
 */
public final class PrivateFieldUtils {
	private PrivateFieldUtils() {
	}

	/**
	 * Get the value of a private field if we only know it's exact type and the
	 * class that declared it.
	 * 
	 * @param o
	 *            The Object we want the filed value for.
	 * @param baseClass
	 *            The class that declared the field.
	 * @param fieldType
	 *            The exact type of the field declaration.
	 * @return
	 */
	public static <T> T getFieldValue(Object o, Class<?> baseClass,
			Class<T> fieldType) {
		for (Field f : baseClass.getDeclaredFields()) {
			if (typeEquals(f.getType(), fieldType)) {
				f.setAccessible(true);
				try {
					return (T) f.get(o);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
					throw new IllegalArgumentException(e);
				}
			}

			if (f.getType().isArray()) {
				Class<?> componentType = f.getType().getComponentType();
				if (componentType == ItemStack.class) {
				}
			}
		}

		throw new IllegalArgumentException("No field of type " + fieldType
				+ " in " + baseClass);
	}

	public static boolean typeEquals(Class<?> a, Class<?> b) {
		return a.equals(b);
	}
}
