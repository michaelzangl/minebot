package net.famzangl.minecraft.minebot.settings;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class FieldValidation {
	public static abstract class FieldValidator {
		protected final Field f;

		public FieldValidator(Field f) {
			this.f = f;
		}

		public void validate(Object loaded, Object defaultReference) {
			try {
				f.setAccessible(true);
				validateImpl(loaded, defaultReference);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		protected abstract void validateImpl(Object loaded,
				Object defaultReference) throws IllegalAccessException;

		protected Object validateValues(Object loadedValue, Object defaultValue) {
			return loadedValue;
		}
	}

	public static abstract class ObjectFieldValidator extends FieldValidator {
		public ObjectFieldValidator(Field f) {
			super(f);
		}

		protected void validateImpl(Object loaded, Object defaultReference)
				throws IllegalAccessException {
			Object loadedValue = f.get(loaded);
			Object defaultValue = f.get(defaultReference);

			Object newValue = validateValues(loadedValue, defaultValue);
			f.set(loaded, newValue);
		}

		protected Object validateValues(Object loadedValue, Object defaultValue) {
			return loadedValue;
		}
	}
	public static class RecourseValidator extends ObjectFieldValidator {

		public RecourseValidator(Field f) {
			super(f);
		}
		
		@Override
		protected Object validateValues(Object loadedValue, Object defaultValue) {
			validateAfterLoad(loadedValue, defaultValue);
			return loadedValue;
		}
		
	}

	public static class NotNullValidator extends ObjectFieldValidator {
		public NotNullValidator(Field f) {
			super(f);
		}

		@Override
		protected Object validateValues(Object loadedValue, Object defaultValue) {
			return loadedValue == null ? defaultValue : loadedValue;
		}
	}

	public static class ClampedFloatValidator extends FieldValidator {
		public ClampedFloatValidator(Field f) {
			super(f);
		}

		@Override
		protected void validateImpl(Object loaded, Object defaultReference)
				throws IllegalAccessException {
			ClampedFloat annot = f.getAnnotation(ClampedFloat.class);
			float loadedValue = f.getFloat(loaded);
			if (annot.max() > loadedValue) {
				f.setFloat(loaded, annot.max());
			} else if (annot.min() < loadedValue) {
				f.setFloat(loaded, annot.min());
			}
		}

		@Override
		protected Object validateValues(Object loadedValue, Object defaultValue) {
			f.getAnnotation(ClampedFloat.class);

			return loadedValue == null ? defaultValue : loadedValue;
		}
	}

	private FieldValidation() {
	}

	public static void validateAfterLoad(Object loadedValue, Object defaultValue) {
		if (loadedValue.getClass() != defaultValue.getClass()) {
			// cannot validate this
			throw new IllegalArgumentException("Loaded class is a "
					+ loadedValue.getClass().getName() + ", but we need a "
					+ defaultValue.getClass().getName());
		}
		
		List<FieldValidator> validators = FieldValidation.getForClass(loadedValue.getClass());
		for (FieldValidator v : validators) {
			v.validate(loadedValue, defaultValue);
		}
	}

	public static List<FieldValidator> getForClass(Class<? extends Object> subclass) {
		Class<? extends Object> clazz = subclass;
		ArrayList<FieldValidator> list = new ArrayList<FieldValidator>();
		while (clazz != null) {
			for (Field f : clazz.getDeclaredFields()) {
				addValidators(f, list);
			}
			clazz = clazz.getSuperclass();
		}
		return list;
	}

	private static void addValidators(Field f, ArrayList<FieldValidator> list) {
		if (Modifier.isStatic(f.getModifiers()) || Modifier.isFinal(f.getModifiers())) {
			// cannot set static/final fields.
			return;
		}
		
		if (!f.getType().isPrimitive()) {
			list.add(new NotNullValidator(f));
			list.add(new RecourseValidator(f));
		}

		if (f.isAnnotationPresent(ClampedFloat.class)) {
			list.add(new ClampedFloatValidator(f));
		}
	}
}
