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
package net.famzangl.minecraft.minebot.ai.command;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy;

/**
 * This is the basic definition of a command.
 * 
 * @author michael
 * 
 */
public class CommandDefinition {
	private final ArrayList<ArgumentDefinition> arguments;
	private final ArrayList<Integer> parameterStarts;
	private final Method method;
	private final ArrayList<ParameterBuilder> builders;

	// public CommandDefinition(Method m) {
	// // this.method = m;
	// // final Class<?>[] types = m.getParameterTypes();
	// // final Annotation[][] parameterAnnotations =
	// // m.getParameterAnnotations();
	// // builders = new ParameterBuilder[types.length];
	// // parameterStarts = new int[types.length + 1];
	// // for (int parameter = 0; parameter < types.length; parameter++) {
	// // ParameterBuilder b = getParameter(types, parameterAnnotations,
	// // parameter);
	// // parameterStarts[parameter] = arguments.size();
	// // b.addArguments(arguments);
	// // builders[parameter] = b;
	// // }
	// // parameterStarts[types.length] = arguments.size();
	// }

	private CommandDefinition(Method method, ArrayList<ParameterBuilder> builders,
			ArrayList<ArgumentDefinition> arguments,
			ArrayList<Integer> parameterStarts) {
		this.method = method;
		this.builders = builders;
		this.arguments = arguments;
		this.parameterStarts = parameterStarts;
	}

	private static ParameterBuilder getParameter(Method method,
			final Class<?>[] types, final Annotation[][] parameterAnnotations,
			int parameter) {
		ParameterBuilder builder;
		if (types[parameter] == AIHelper.class) {
			builder = new AIHelperBuilder(null);
		} else {
			final AICommandParameter annot = findParameterAnnotation(method,
					parameterAnnotations[parameter]);
			switch (annot.type()) {
			case BLOCK_NAME:
				builder = new BlockNameBuilder(annot);
				break;
			case FIXED:
				builder = new FixedNameBuilder(annot);
				break;
			case NUMBER:
				builder = new NumberNameBuilder(annot);
				break;
			case DOUBLE:
				builder = new DoubleNameBuilder(annot);
				break;
			case COMMAND:
				builder = new CommandNameBuilder(annot);
				break;
			case COLOR:
				builder = new ColorNameBuilder(annot);
				break;
			case ENUM:
				builder = new EnumNameBuilder(annot, types[parameter]);
				break;
			case FILE:
				builder = new FileNameBuilder(annot);
				break;
			case POSITION:
				builder = new PositionNameBuilder(annot);
				break;
			case STRING:
				builder = new StringNameBuilder(annot);
				break;
			case BLOCK_STATE:
				builder = new BlockStateNameBuilder(annot);
				break;
			default:
				throw new IllegalArgumentException("Unknown type: "
						+ annot.type());
			}
		}
		return builder;
	}

	public static ArrayList<CommandDefinition> getDefinitions(Method method) {
		ArrayList<CommandDefinition> list = new ArrayList<CommandDefinition>();
		final Class<?>[] types = method.getParameterTypes();
		final Annotation[][] parameterAnnotations = method.getParameterAnnotations();
		ArrayList<ParameterBuilder> builders = new ArrayList<ParameterBuilder>();
		ArrayList<ArgumentDefinition> arguments = new ArrayList<ArgumentDefinition>();
		ArrayList<Integer> parameterStarts = new ArrayList<Integer>();
		
		checkParameters(method, types, parameterAnnotations);

		addParametersFromTo(list, method, types, parameterAnnotations, builders,
				arguments, parameterStarts, 0);

		return list;
	}

	private static void checkParameters(Method method, Class<?>[] types,
			Annotation[][] parameterAnnotations) {
		for (int i = 0; i < types.length; i++) {
			ParameterBuilder param = getParameter(method, types, parameterAnnotations, i);
			try {
			param.isTypeValid(types[i]);}
			catch (UnsupportedOperationException e) {
				System.err.println("Cannot check parameters for " + method.getName() + ": " + e.getMessage());
			}
		}
	}

	private static void addParametersFromTo(ArrayList<CommandDefinition> list,
			Method method, Class<?>[] types, Annotation[][] parameterAnnotations,
			ArrayList<ParameterBuilder> builders,
			ArrayList<ArgumentDefinition> arguments,
			ArrayList<Integer> parameterStarts, int fromIndex) {
		if (fromIndex >= types.length) {
			parameterStarts.add(arguments.size());
			list.add(new CommandDefinition(method, builders, arguments,
					parameterStarts));
			return;
		}

		ParameterBuilder nextBuilder = getParameter(method, types,
				parameterAnnotations, fromIndex);
		if (nextBuilder.isOptional()) {
			ArrayList<ParameterBuilder> builders2 = new ArrayList<ParameterBuilder>(
					builders);
			ArrayList<ArgumentDefinition> arguments2 = new ArrayList<ArgumentDefinition>(
					arguments);
			ArrayList<Integer> parameterStarts2 = new ArrayList<Integer>(
					parameterStarts);
			addParameterBuilder(
					new OptionalParameterBuilder(findParameterAnnotation(method,
							parameterAnnotations[fromIndex])), builders2,
					arguments2, parameterStarts2);
			addParametersFromTo(list, method, types, parameterAnnotations,
					builders2, arguments2, parameterStarts2, fromIndex + 1);
		}
		addParameterBuilder(nextBuilder, builders, arguments, parameterStarts);
		addParametersFromTo(list, method, types, parameterAnnotations, builders,
				arguments, parameterStarts, fromIndex + 1);
	}

	private static void addParameterBuilder(ParameterBuilder nextBuilder,
			ArrayList<ParameterBuilder> builders,
			ArrayList<ArgumentDefinition> arguments,
			ArrayList<Integer> parameterStarts) {
		parameterStarts.add(arguments.size());
		nextBuilder.addArguments(arguments);
		builders.add(nextBuilder);
	}

	private static AICommandParameter findParameterAnnotation(Method method,
			Annotation[] annotations) {
		for (final Annotation annotation : annotations) {
			if (annotation instanceof AICommandParameter) {
				// TODO: Type check of parameter.
				return (AICommandParameter) annotation;
			}
		}
		throw new IllegalArgumentException(
				"Could not find parameter annotation for " + method.getName()
						+ " of  class " + method.getDeclaringClass().getSimpleName());
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
			if (!arguments.get(i).couldEvaluateAgainst(Arrays.asList(arguments2).subList(0, i), arguments2[i])) {
				return false;
			}
		}
		return true;
	}

	public AIStrategy evaluate(AIHelper helper, String[] args) {
		final Object[] params = new Object[builders.size()];
		for (int parameter = 0; parameter < builders.size(); parameter++) {
			final String[] argsPart = Arrays.copyOfRange(args,
					parameterStarts.get(parameter), parameterStarts.get(parameter + 1));
			params[parameter] = builders.get(parameter).getParameter(helper,
					argsPart);
		}
		Object result = null;
		try {
			result = method.invoke(null, params);
			if (result instanceof AIStrategy) {
				return (AIStrategy) result;
			}
		} catch (final IllegalAccessException e) {
			doThrow(e);
		} catch (final IllegalArgumentException e) {
			dumIAE(e, params);
			doThrow(e);
		} catch (final InvocationTargetException e) {
			final Throwable exception = e.getTargetException();
			doThrow(exception);
		}
		throw new CommandEvaluationException("No AI strategy was created.  Result was: " + result);
	}

	private void dumIAE(IllegalArgumentException e, Object[] params) {
		System.err.println("There was an argument mismatch. This is what I attempted to use:");
		for (Object p : params) {
			System.err.println("   - " + p);
		}
		System.err.println("This is the method I attempted to use: " + method.toGenericString());
	}

	private void doThrow(Throwable exception) {
		exception.printStackTrace();
		throw exception instanceof CommandEvaluationException ? (CommandEvaluationException) exception
				: new CommandEvaluationException(
						"Unexpected error while evaluating.", exception);
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
		return getCommandAnnotation().name();
	}

	public SafeStrategyRule getSafeStrategyRule() {
		return method.getAnnotation(AICommandInvocation.class).safeRule();
	}
}
