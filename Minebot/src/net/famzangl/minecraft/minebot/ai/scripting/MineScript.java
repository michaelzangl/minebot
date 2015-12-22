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
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import javax.script.ScriptException;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.command.AIChatController;
import net.famzangl.minecraft.minebot.ai.command.UnknownCommandException;
import net.famzangl.minecraft.minebot.ai.commands.CommandRun;
import net.famzangl.minecraft.minebot.ai.net.MinebotNetHandler.PersistentChat;
import net.famzangl.minecraft.minebot.ai.scripting.CommandJs.RunScriptStrategy;
import net.famzangl.minecraft.minebot.ai.scripting.CommandJs.ScriptStrategy;
import net.famzangl.minecraft.minebot.ai.scripting.CommandJs.TickProvider;
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.InventoryDefinition;
import net.famzangl.minecraft.minebot.ai.strategy.RunFileStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.StackStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.StrategyStack;
import net.famzangl.minecraft.minebot.ai.strategy.WalkTowardsStrategy;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

/**
 * The minescript object that is exported to js.
 * 
 * @author michael
 *
 */
public class MineScript {

	private final TickProvider tickProvider;

	public MineScript(TickProvider tickProvider) {
		this.tickProvider = tickProvider;
	}

	private AIHelper waitForTick() {
		AIHelper helper = tickProvider.getHelper();
		return helper;
	}

	/**
	 * Evaluates a command and returns the strategy it resulted in. This is as
	 * if the user entered the command, so there are multiple event handlers
	 * active.
	 * 
	 * @param commandLine
	 * @throws UnknownCommandException
	 */
	public ScriptStrategy safeStrategy(String command, Object... arguments)
			throws UnknownCommandException {
		return new ScriptStrategy(AIChatController.getRegistry()
				.evaluateCommandWithSaferule(waitForTick(), command, toStringArray(arguments)));
	}

	/**
	 * Get a minebot strategy.
	 * 
	 * @param commandLine
	 * @throws UnknownCommandException
	 */
	public ScriptStrategy strategy(String command, Object... arguments)
			throws UnknownCommandException {
		return new ScriptStrategy(AIChatController.getRegistry()
				.evaluateCommand(waitForTick(), command, toStringArray(arguments)));
	}
	
	private String[] toStringArray(Object[] arguments) {
		String[] strs = new String[arguments.length];
		for (int i = 0; i < arguments.length; i++) {
			Object a = arguments[i];
			strs[i] = a.toString();
		}
		return strs;
	}

	public ScriptStrategy strategyWalkTowards(double x, double z) {
		return new ScriptStrategy(new WalkTowardsStrategy(x, z));
	}

	public WrappedBlockPos getPlayerBlockPosition() {
		return new WrappedBlockPos(waitForTick().getPlayerPosition());
	}
	
	public FoundEntity getPlayer() {
		return new FoundEntity(waitForTick().getMinecraft().thePlayer);
	}

	public FoundEntity[] getEntities(Class clazz, double range) {
		AIHelper helper = waitForTick();
		Vec3 p = helper.getMinecraft().thePlayer.getPositionEyes(1);
		AxisAlignedBB box = new AxisAlignedBB(p.xCoord - range,
				p.yCoord - range, p.zCoord - range, p.xCoord + range, p.yCoord
						+ range, p.zCoord + range);
		List<Entity> es = helper.getMinecraft().theWorld.getEntitiesWithinAABB(
				clazz, box);
		FoundEntity[] res = new FoundEntity[es.size()];
		for (int i = 0; i < res.length; i++) {
			res[i] = new FoundEntity(es.get(i));
		}
		return res;
	}

	public void displayChat(String message) {
		AIChatController.addChatLine(message);
	}
	
	private ChatMessage[] chatMessageCache = new ChatMessage[0];
	
	public ChatMessage[] getChatMessages() throws ScriptException {
		List<PersistentChat> messages = waitForTick().getNetworkHelper().getChatMessages();
		int len = messages.size();
		if (len > chatMessageCache.length) {
			int oldLength = chatMessageCache.length;
			chatMessageCache = Arrays.copyOf(chatMessageCache, len);
			for (int i = oldLength;i < len; i++) {
				PersistentChat m = messages.get(i);
				chatMessageCache[i] = new ChatMessage(m, tickProvider.getEngine());
			}
		}
		return chatMessageCache;
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

	public InventoryDefinition getInventory() {
		return new InventoryDefinition(
				waitForTick().getMinecraft().thePlayer.inventory);
	}

	public ScriptStrategy stack(Object... strats) {
		StrategyStack stack = new StrategyStack();
		for (Object s : strats) {
			AIStrategy strategy;
			if (s instanceof ScriptStrategy) {
				strategy = ((ScriptStrategy) s).getStrategy();
			} else if (s instanceof AIStrategy) {
				strategy = (AIStrategy) s;
			} else {
				throw new IllegalArgumentException("Class " + s.getClass() + " is not strategy.");
			}
			stack.addStrategy(strategy);
		}
		return new ScriptStrategy(new StackStrategy(stack));
	}

	public void doNothing() {
		tickProvider.setActiveStrategy(null, waitForTick());
		tickProvider.tickDone();
		try {
			// Hope that main thread exits tick.
			Thread.sleep(5);
		} catch (InterruptedException e) {
		}
	}

	public void doStrategy(AIStrategy strategy) {
		doStrategy(new ScriptStrategy(strategy));
	}

	public void doStrategy(ScriptStrategy strategy) {
		tickProvider.setActiveStrategy(strategy, waitForTick());
		tickProvider.tickDone();
		tickProvider.pauseForStrategy();
		if (strategy.hasFailed()) {
			throw new StrategyFailedException();
		}
	}

	public void setDescription(String description) {
		tickProvider.setDescription(description);
	}

	public void serverCommand(String command) {
		RunFileStrategy.runCommand(waitForTick(), command);
		tickProvider.tickDone();
	}

	/**
	 * Get a value from disk.
	 * 
	 * @param key
	 */
	public String getString(String key) {
		return getString(key, null);
	}

	public String getString(String key, String defaultValue) {
		File file = getPersistenceFile(key);
		try {
			if (!file.exists()) {
				return defaultValue;
			}
			Scanner scanner = new Scanner(file, "UTF-8");
			String line = scanner.useDelimiter("\\A").next();
			scanner.close();
			return line;
		} catch (FileNotFoundException e) {
			return defaultValue;
		} catch (NoSuchElementException e) {
			return "";
		}
	}

	public void storeString(String key, String value) {
		File file = getPersistenceFile(key);

		try {
			PrintWriter writer = new PrintWriter(file, "UTF-8");
			writer.print(value);
			writer.close();
		} catch (FileNotFoundException e) {
			throw new UnsupportedOperationException("Persistence dir is not writeable.");
		} catch (UnsupportedEncodingException e) {
		}
	}
	
	public void getJSON(String key, String value) {
		//TODO
	}

	private File getPersistenceFile(String key) {
		if (!key.matches("[a-zA-Z0-8\\._-]+")) {
			throw new IllegalArgumentException("Invalid key name: " + key);
		}
		File dir = AIHelper.getMinebotDir();
		File persistence = new File(dir, "js-persistence");
		if (!persistence.exists()) {
			persistence.mkdirs();
		}
		File file = new File(persistence, key);
		return file;
	}
}