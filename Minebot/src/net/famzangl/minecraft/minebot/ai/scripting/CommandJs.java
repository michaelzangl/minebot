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
package net.famzangl.minecraft.minebot.ai.scripting;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.command.AIChatController;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.ai.command.AICommandInvocation;
import net.famzangl.minecraft.minebot.ai.command.AICommandParameter;
import net.famzangl.minecraft.minebot.ai.command.ParameterType;
import net.famzangl.minecraft.minebot.ai.scripting.CommandJs.TickProvider;
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy.TickResult;
import net.famzangl.minecraft.minebot.ai.task.error.StringTaskError;
import net.famzangl.minecraft.minebot.ai.task.error.TaskError;
import net.famzangl.minecraft.minebot.ai.utils.PrivateFieldUtils;
import net.minecraft.launchwrapper.LaunchClassLoader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

@AICommand(name = "minebot", helpText = "Execute a javascript file.")
public class CommandJs {
	private static final Marker MARKER_ENGINE = MarkerManager
			.getMarker("engine");
	private static final Marker MARKER_SYNC = MarkerManager.getMarker("sync");
	private static final Logger LOGGER = LogManager.getLogger(CommandJs.class);

	public static class ScriptStrategy {
		private final AIStrategy strategy;

		public ScriptStrategy(AIStrategy strategy) {
			if (strategy == null) {
				throw new NullPointerException();
			}
			this.strategy = strategy;
		}

		protected AIStrategy getStrategy() {
			return strategy;
		}

		public boolean hasFailed() {
			return strategy.hasFailed();
		}

		@Override
		public String toString() {
			return "ScriptStrategy [strategy=" + strategy + "]";
		}

	}

	public interface TickProvider {

		AIHelper getHelper();

		void setActiveStrategy(ScriptStrategy strategy, AIHelper helper);

		void tickDone();

		void pauseForStrategy();

		ScriptEngine getEngine();

		DescriptionBuilder getDescription();
	}

	/**
	 * This class runs a given script file. The script is run in a new thread
	 * that is interlocked with the game thread.
	 * 
	 * @author michael
	 *
	 */
	public static class ScriptRunner implements Runnable, TickProvider {
		private static final class StrategyTicker implements Runnable {
			private TickResult result;
			private AIHelper helper;
			private AIStrategy activeStrategy;

			public StrategyTicker(AIHelper helper, AIStrategy activeStrategy) {
				super();
				this.helper = helper;
				this.activeStrategy = activeStrategy;
			}

			@Override
			public synchronized void run() {
				try {
					result = activeStrategy.gameTick(helper);
				} finally {
					if (result == null) {
						LOGGER.error("Strategy returned null: "
								+ activeStrategy);
						result = TickResult.ABORT;
					}
					this.notifyAll();
				}
			}

			public AIHelper getHelper() {
				return helper;
			}

			public synchronized TickResult getResult() {
				while (result == null) {
					try {
						this.wait();
					} catch (InterruptedException e) {
					}
				}
				return result;
			}
		}

		private final DescriptionBuilder description;
		private final File fileName;
		private boolean finished;
		private AIHelper tickHelper;
		private final Object tickHelperMutex = new Object();
		/**
		 * guarded by tickHelperMutex
		 */
		private boolean scriptInsideTick = false;
		private boolean scriptWaitingForTick = false;
		private boolean stopped;
		private AIStrategy activeStrategy;
		private final Object activeStrategyMutex = new Object();
		private TaskError error;
		private final Object errorMutex = new Object();
		private ScriptEngine engine;
		private StrategyTicker pendingTicker;

		public ScriptRunner(File file) {
			this.fileName = file;
			description = new DescriptionBuilder(fileName);
		}

		@Override
		public void run() {
			try {
				synchronized (tickHelperMutex) {
					if (stopped) {
						throw new ScriptException("Cannot reactivate.");
					}
				}
				engine = generateScriptEngine();
				FileReader fis;
				try {
					fis = new FileReader(fileName);
				} catch (FileNotFoundException e) {
					throw new ScriptException("File was not found: " + fileName);
				}
				engine.put("minescript", new MineScript(this));
				engine.eval(fis);
			} catch (Throwable e) {
				System.out.println("Error while executing the script.");
				System.out.println("Your are using java "
						+ System.getProperty("java.version") + " "
						+ System.getProperty("java.vendor") + " on "
						+ System.getProperty("os.name"));
				e.printStackTrace();
				synchronized (errorMutex) {
					error = new StringTaskError(e.getMessage());
				}
			} finally {
				finished = true;
				tickDone();
			}
		}

		@Override
		public DescriptionBuilder getDescription() {
			return description;
		}

		/**
		 * Attempt to generate a nashorn script engine
		 * 
		 * @return A script engine
		 * @throws ScriptException If no engine could be created.
		 */
		private ScriptEngine generateScriptEngine() throws ScriptException {
			ScriptEngineManager manager = new ScriptEngineManager(null);
			LOGGER.trace(MARKER_ENGINE,
					"Creating javascript engine. Class loader hirarchy:");
			ClassLoader cl = getClass().getClassLoader();
			while (cl != null) {
				LOGGER.trace(MARKER_ENGINE, "  - " + cl);
				if (cl instanceof LaunchClassLoader) {
					cl = PrivateFieldUtils.getFieldValue(cl,
							LaunchClassLoader.class, ClassLoader.class);
				} else {
					cl = cl.getParent();
				}
			}

			ScriptEngine nashorn = manager.getEngineByName("nashorn");
			if (nashorn != null) {
				fixLaunchClassLoader();
				return nashorn;
			}

			LOGGER.warn(MARKER_ENGINE,
					"Could not create any nashorn engine. Falling back to JavaScript.");
			ScriptEngine js = manager.getEngineByName("JavaScript");
			if (js != null) {
				return js;
			}

			LOGGER.error(MARKER_ENGINE, "Could not create any engine.");
			throw new ScriptException("No Javascript engine was found.");
		}

		/**
		 * This is a fix that allows minecraft and nashorn classes to interoperate.
		 */
		private void fixLaunchClassLoader() {
			if (getClass().getClassLoader() instanceof LaunchClassLoader) {
				LaunchClassLoader loader = (LaunchClassLoader) getClass()
						.getClassLoader();
				// allows you to extend Minebot classes.
				loader.addClassLoaderExclusion("jdk.nashorn.");
			}
		}

		public boolean isFinished() {
			synchronized (tickHelperMutex) {
				printError();
				return finished;
			}
		}

		/**
		 * Runs if we need to wait for a game tick.
		 * 
		 * @param helper
		 * @return
		 */
		public TickResult runForTick(AIHelper helper) {
			printError();
			AIStrategy strat;

			TickResult tickResult = runStrategyGameTick(helper);
			if (tickResult != null) {
				return tickResult;
			}
			synchronized (tickHelperMutex) {
				if (scriptWaitingForTick) {
					tickHelper = helper;
					scriptInsideTick = true;
					scriptWaitingForTick = false;
					tickHelperMutex.notifyAll();
					while (scriptInsideTick) {
						try {
							tickHelperMutex.wait();
						} catch (InterruptedException e) {
						}
					}
					tickHelper = null;
				}
				return finished ? TickResult.NO_MORE_WORK
						: TickResult.TICK_HANDLED;
			}
		}

		private void printError() {
			synchronized (errorMutex) {
				if (error != null) {
					AIChatController.addChatLine("JS Error: "
							+ error.getMessage());
					error = null;
				}
			}
		}

		@Override
		public void tickDone() {
			synchronized (tickHelperMutex) {
				LOGGER.error(MARKER_SYNC, "Script requests that we resume.");
				printError();
				scriptInsideTick = false;
				scriptWaitingForTick = false;
				tickHelperMutex.notifyAll();
			}

		}

		@Override
		public AIHelper getHelper() {
			synchronized (tickHelperMutex) {
				LOGGER.error(MARKER_SYNC,
						"Synchronize to getting script helper.");
				if (stopped) {
					throw new RuntimeException("Stop.");
				}
				while (tickHelper == null) {
					try {
						scriptWaitingForTick = true;
						tickHelperMutex.wait();
					} catch (InterruptedException e) {
					}
				}
				scriptInsideTick = true;
				return tickHelper;
			}
		}

		public void stop() {
			synchronized (tickHelperMutex) {
				stopped = true;
			}
		}

		@Override
		public void setActiveStrategy(ScriptStrategy strategy, AIHelper helper) {
			synchronized (activeStrategyMutex) {
				LOGGER.error(MARKER_SYNC, "Change strategy to " + strategy);
				if (activeStrategy != null) {
					activeStrategy.setActive(false, helper);
				}
				activeStrategy = strategy == null ? null : strategy
						.getStrategy();
				if (activeStrategy != null) {
					activeStrategy.setActive(true, helper);
				}
				description.setActiveStrategy(activeStrategy);
				activeStrategyMutex.notifyAll();
			}
		}

		/**
		 * Gets the description string.
		 * @param helper
		 * @return A string describing this strategy.
		 */
		public String getDescription(AIHelper helper) {
			return description.getDescriptionString(helper);
		}

		@Override
		public void pauseForStrategy() {
			synchronized (activeStrategyMutex) {
				while (activeStrategy != null) {
					if (pendingTicker != null) {
						LOGGER.error(MARKER_SYNC,
								"Handling strategy tick in js thread.");
						tickHelper = pendingTicker.getHelper();
						pendingTicker.run();
						tickHelper = null;
						pendingTicker = null;
					}
					try {
						activeStrategyMutex.wait();
					} catch (InterruptedException e) {
					}
				}
			}
		}

		private TickResult runStrategyGameTick(AIHelper helper) {
			StrategyTicker ticker;
			synchronized (activeStrategyMutex) {
				if (activeStrategy == null) {
					return null;
				}
				// We need to dispatch a activeStrategy.gameTick(helper) on the
				// JS thread.
				ticker = new StrategyTicker(helper, activeStrategy);
				pendingTicker = ticker;
				activeStrategyMutex.notifyAll();
			}
			TickResult tickResult = ticker.getResult();
			if (tickResult == TickResult.NO_MORE_WORK
					|| tickResult == TickResult.ABORT) {
				setActiveStrategy(null, helper);
				return null;
			}
			return tickResult;
		}

		public TaskError getError() {
			synchronized (errorMutex) {
				return error;
			}
		}

		@Override
		public ScriptEngine getEngine() {
			return engine;
		}
	}

	public static class RunScriptStrategy extends AIStrategy {
		private final ScriptRunner scriptRunner;

		public RunScriptStrategy(File file) {
			scriptRunner = new ScriptRunner(file);
		}

		@Override
		protected void onActivate(AIHelper helper) {
			Thread scriptThread = new Thread(scriptRunner, "minebot-js");
			scriptThread.start();
			super.onActivate(helper);
		}

		@Override
		protected void onDeactivate(AIHelper helper) {
			scriptRunner.stop();
			super.onDeactivate(helper);
		}

		@Override
		public boolean checkShouldTakeOver(AIHelper helper) {
			return !scriptRunner.isFinished();
		}

		@Override
		protected TickResult onGameTick(AIHelper helper) {
			return scriptRunner.runForTick(helper);
		}

		@Override
		public String getDescription(AIHelper helper) {
			return scriptRunner.getDescription(helper);
		}
	}

	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "js", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.FILE, relativeToSettingsFile = "scripts", description = "") File file) {
		return new RunScriptStrategy(file);
	}
}
