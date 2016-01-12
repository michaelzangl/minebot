/*******************************************************************************
 * This file is part of Minebot.
 *
 * Minebot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Minebot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Minebot.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package net.famzangl.minecraft.minebot.ai.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

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
		if (o == null) {
			throw new NullPointerException();
		}
		if (!baseClass.isAssignableFrom(o.getClass())) {
			throw new IllegalArgumentException("Got a " + o.getClass().getName() + " but expected a " + baseClass.getName());
		}
		for (Field f : baseClass.getDeclaredFields()) {
			if (typeEquals(f.getType(), fieldType) && !Modifier.isStatic(f.getModifiers())) {
				f.setAccessible(true);
				try {
					return (T) f.get(o);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
					throw new IllegalArgumentException(e);
				}
			}
		}

		throw new IllegalArgumentException("No field of type " + fieldType
				+ " in " + baseClass);
	}
	public static <T> void setFieldValue(Object o, Class<?> baseClass,
			Class<T> fieldType, T value) {
		if (o == null) {
			throw new NullPointerException();
		}
		if (!baseClass.isAssignableFrom(o.getClass())) {
			throw new IllegalArgumentException("Got a " + o.getClass().getName() + " but expected a " + baseClass.getName());
		}
		for (Field f : baseClass.getDeclaredFields()) {
			if (typeEquals(f.getType(), fieldType) && !Modifier.isStatic(f.getModifiers())) {
				f.setAccessible(true);
				try {
					f.set(o, value);
					return;
				} catch (IllegalAccessException e) {
					e.printStackTrace();
					throw new IllegalArgumentException(e);
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
