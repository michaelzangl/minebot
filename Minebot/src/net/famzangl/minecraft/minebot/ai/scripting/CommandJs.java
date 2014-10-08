package net.famzangl.minecraft.minebot.ai.scripting;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.command.AIChatController;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.ai.command.AICommandInvocation;
import net.famzangl.minecraft.minebot.ai.command.AICommandParameter;
import net.famzangl.minecraft.minebot.ai.command.ParameterType;
import net.famzangl.minecraft.minebot.ai.command.UnknownCommandException;
import net.famzangl.minecraft.minebot.ai.commands.CommandRun;
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy.TickResult;
import net.famzangl.minecraft.minebot.ai.strategy.StackStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.StrategyStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;

@AICommand(name = "minebot", helpText = "Execute a javascript file.")
public class CommandJs {
	
	public static class FoundEntity {
		private final Entity entity;
		private final Pos pos;
		
		public FoundEntity(Entity entity) {
			this.entity = entity;
			pos = new Pos((int) Math.floor(entity.posX), (int) Math.floor(entity.posY), (int) Math.floor(entity.posZ));
		}

		public Pos getPos() {
			return pos;
		}

		public Class<?> getType()  {
			return entity.getClass();
		}
		
		public String getTypeName()  {
			return entity.getClass().getSimpleName();
		}
	}
	
	public static class ScriptStrategy {
		private final AIStrategy strategy;

		public ScriptStrategy(AIStrategy strategy) {
			this.strategy = strategy;
		}

		protected AIStrategy getStrategy() {
			return strategy;
		}
	}

	public interface TickProvider {

		AIHelper getHelper();

		void setDescription(String description);

		void setActiveStrategy(ScriptStrategy strategy, AIHelper helper);

		void tickDone();

		void pauseForStrategy();
	}

	public static class MineScript {

		private final TickProvider tickProvider;

		public MineScript(TickProvider tickProvider) {
			this.tickProvider = tickProvider;
		}

		private AIHelper waitForTick() {
			return tickProvider.getHelper();
		}

		/**
		 * Evaluates a command and returns the strategy it resulted in. This is
		 * as if the user entered the command, so there are multiple event
		 * handlers active.
		 * 
		 * @param commandLine
		 * @throws UnknownCommandException
		 */
		public ScriptStrategy safeStrategy(String command, String... arguments)
				throws UnknownCommandException {
			return new ScriptStrategy(AIChatController.getRegistry()
					.evaluateCommandWithSaferule(waitForTick(), command,
							arguments));
		}

		/**
		 * Get a minebot strategy.
		 * 
		 * @param commandLine
		 * @throws UnknownCommandException
		 */
		public ScriptStrategy strategy(String command, String... arguments)
				throws UnknownCommandException {
			return new ScriptStrategy(AIChatController.getRegistry()
					.evaluateCommand(waitForTick(), command, arguments));
		}

		public Pos getPlayerPosition() {
			return waitForTick().getPlayerPosition();
		}
		
		public FoundEntity[] getEntities(Class clazz, double range){
				AIHelper helper = waitForTick();
				Vec3 p = helper.getMinecraft().thePlayer.getPosition(1);
				AxisAlignedBB box = AxisAlignedBB.getBoundingBox(p.xCoord - range, p.yCoord-range, p.zCoord - range,p.xCoord +range, p.yCoord+range, p.zCoord +range);
				List<Entity> es = helper.getMinecraft().theWorld.getEntitiesWithinAABB(clazz, box);
				FoundEntity[] res = new FoundEntity[es.size()]; 
				for (int i = 0; i < res.length; i++) {
					res[i] = new FoundEntity(es.get(i));
				}
				return res;
		}

		public void displayChat(String message) {
			AIChatController.addChatLine(message);
		}
		
		public boolean isAlive() {
			return waitForTick().isAlive();
		}

		public int getExperienceLevel() {
			return waitForTick().getMinecraft().thePlayer.experienceLevel;
		}

		public int getCurrentTime() {
			return (int) (waitForTick().getMinecraft().theWorld.getWorldTime() % 24000l);
		}

		public ScriptStrategy stack(ScriptStrategy... strats) {
			StrategyStack stack = new StrategyStack();
			for (ScriptStrategy s : strats) {
				stack.addStrategy(s.getStrategy());
			}
			return new ScriptStrategy(new StackStrategy(stack));
		}

		public void doNothing() {
			tickProvider.setActiveStrategy(null, waitForTick());
			tickProvider.tickDone();
		}

		public void doStrategy(ScriptStrategy strategy) {
			tickProvider.setActiveStrategy(strategy, waitForTick());
			tickProvider.tickDone();
			tickProvider.pauseForStrategy();
		}

		public void setDescription(String description) {
			tickProvider.setDescription(description);
		}
		
		public void serverCommand(String command) {
			CommandRun.runCommand(waitForTick(), command);
			tickProvider.tickDone();
		}
	}

	public static class ScriptRunner implements Runnable, TickProvider {
		private String description = "Running script";
		private final String fileName;
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

		public ScriptRunner(String fileName) {
			this.fileName = fileName;
		}

		@Override
		public void run() {
			try {
				synchronized (tickHelperMutex) {
					if (stopped) {
						throw new RuntimeException("Cannot reactivate.");
					}
				}
				ScriptEngineManager manager = new ScriptEngineManager();
				ScriptEngine engine = manager.getEngineByName("JavaScript");
				FileReader fis = new FileReader(fileName);
				engine.put("minescript", new MineScript(this));
				engine.eval(fis);
			} catch (ScriptException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} finally {
				finished = true;
				tickDone();
			}
		}

		public boolean isFinished() {
			return finished;
		}

		/**
		 * Runs if we need to wait for a game tick.
		 * 
		 * @param helper
		 * @return
		 */
		public TickResult runForTick(AIHelper helper) {
			synchronized (tickHelperMutex) {
				synchronized (activeStrategyMutex) {
					if (activeStrategy != null) {
						TickResult tickResult = activeStrategy.gameTick(helper);
						if (tickResult == TickResult.NO_MORE_WORK
								|| tickResult == TickResult.ABORT) {
							activeStrategy.setActive(false, helper);
							activeStrategy = null;
							activeStrategyMutex.notifyAll();
						} else {
							return tickResult;
						}
					}
				}
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

		@Override
		public void tickDone() {
			synchronized (tickHelperMutex) {
				scriptInsideTick = false;
				scriptWaitingForTick = false;
				tickHelperMutex.notifyAll();
			}

		}

		@Override
		public AIHelper getHelper() {
			synchronized (tickHelperMutex) {
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
		public void setDescription(String description) {
			this.description = description;
		}

		@Override
		public void setActiveStrategy(ScriptStrategy strategy, AIHelper helper) {
			synchronized (activeStrategyMutex) {
				if (activeStrategy != null) {
					activeStrategy.setActive(false, helper);
				}
				activeStrategy = strategy == null ? null : strategy
						.getStrategy();
				if (activeStrategy != null) {
					activeStrategy.setActive(true, helper);
				}
				activeStrategyMutex.notifyAll();
			}
		}

		public String getDescription(AIHelper helper) {
			int lastSlash = fileName.lastIndexOf("/") + 1;
			return fileName.substring(lastSlash) + ": " + description;
		}

		@Override
		public void pauseForStrategy() {
			synchronized (activeStrategyMutex) {
				while (activeStrategy != null) {
					try {
						activeStrategyMutex.wait();
					} catch (InterruptedException e) {
					}
				}
			}
		}
	}

	public static class RunScriptStrategy extends AIStrategy {
		private final ScriptRunner scriptRunner;

		public RunScriptStrategy(String fileName) {
			scriptRunner = new ScriptRunner(fileName);
		}

		@Override
		protected void onActivate(AIHelper helper) {
			Thread scriptThread = new Thread(scriptRunner);
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
			@AICommandParameter(type = ParameterType.FILE, description = "") String file) {
		return new RunScriptStrategy(file);
	}
}
