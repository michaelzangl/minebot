package net.famzangl.minecraft.minebot.ai.command;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.AIStrategy;

/**
 * This is the basic definition of a command.
 * 
 * @author michael
 * 
 */
public class CommandDefinition {
	private final ParameterBuilder[] builders;
	private final ArrayList<ArgumentDefinition> arguments = new ArrayList<ArgumentDefinition>();
	private final int[] parameterStarts;
	private final Method method;

	public CommandDefinition(Method m) {
		this.method = m;
		final Class<?>[] types = m.getParameterTypes();
		final Annotation[][] parameterAnnotations = m.getParameterAnnotations();
		builders = new ParameterBuilder[types.length];
		parameterStarts = new int[types.length + 1];
		for (int parameter = 0; parameter < types.length; parameter++) {
			ParameterBuilder b;
			if (types[parameter] == AIHelper.class) {
				b = new AIHelperBuilder(null);
			} else {
				AICommandParameter annot = findParameterAnnotation(parameterAnnotations[parameter]);
				switch (annot.type()) {
				case BLOCK_NAME:
					b = new BlockNameBuilder(annot);
					break;
				case FIXED:
					b = new FixedNameBuilder(annot);
					break;
				case NUMBER:
					b = new NumberNameBuilder(annot);
					break;
				case COMMAND:
					b = new CommandNameBuilder(annot);
					break;
				case COLOR:
					b = new ColorNameBuilder(annot);
					break;
				case ENUM:
					b = new EnumNameBuilder(annot, types[parameter]);
					break;
				case FILE:
					b = new FileNameBuilder(annot);
					break;
				case POSITION:
					b = new PositionNameBuilder(annot);
					break;
				default:
					throw new IllegalArgumentException("Unknown type: "
							+ annot.type());
				}
			}
			parameterStarts[parameter] = arguments.size();
			b.addArguments(arguments);
			builders[parameter] = b;
		}
		parameterStarts[types.length] = arguments.size();
	}

	private AICommandParameter findParameterAnnotation(Annotation[] annotations) {
		for (Annotation a : annotations) {
			if (a instanceof AICommandParameter) {
				// TODO: Type check of parameter.
				return (AICommandParameter) a;
			}
		}
		throw new IllegalArgumentException(
				"Could not find parameter annotation for " + method.getName()
						+ " of  class "
						+ method.getDeclaringClass().getSimpleName());
	}

	// public ArrayList<ArgumentDefinition> getArguments() {
	// return arguments;
	// }

	public boolean couldEvaluateAgainst(String[] arguments2) {
		if (arguments2.length != arguments.size()) {
			return false;
		}
		return couldEvaluateStartingWith(arguments2);
	}

	public boolean couldEvaluateStartingWith(String[] arguments2) {
		if (arguments2.length > arguments.size()) {
			return false;
		}
		for (int i = 0; i < arguments2.length; i++) {
			if (!arguments.get(i).couldEvaluateAgainst(arguments2[i])) {
				return false;
			}
		}
		return true;
	}

	public AIStrategy evaluate(AIHelper helper, String[] args) {
		Object[] params = new Object[builders.length];
		for (int parameter = 0; parameter < builders.length; parameter++) {
			String[] argsPart = Arrays.copyOfRange(args,
					parameterStarts[parameter], parameterStarts[parameter + 1]);
			params[parameter] = builders[parameter].getParameter(helper,
					argsPart);
		}
		try {
			Object result = method.invoke(null, params);
			if (result instanceof AIStrategy) {
				return (AIStrategy) result;
			} else {
				return null;
			}
		} catch (IllegalAccessException e) {
			doThrow(e);
		} catch (IllegalArgumentException e) {
			doThrow(e);
		} catch (InvocationTargetException e) {
			Throwable exception = e.getTargetException();
			doThrow(exception);
		}
		return null;
	}

	private void doThrow(Throwable exception) {
		exception.printStackTrace();
		throw exception instanceof CommandEvaluationException ? (CommandEvaluationException) exception : new CommandEvaluationException("Unexpected error while evaluating.", exception);
	}

	public ArrayList<ArgumentDefinition> getArguments() {
		return arguments;
	}

	private AICommand getCommandAnnotation() {
		return method.getDeclaringClass().getAnnotation(AICommand.class);
	}

	public String getHelpText() {
		return getCommandAnnotation().helpText();
	}

	public String getCommandName() {
		return method.getDeclaringClass().getAnnotation(AICommand.class).name();
	}
}
