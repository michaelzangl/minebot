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

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.AIHelper.ToolRaterResult;
import net.famzangl.minecraft.minebot.ai.command.AIChatController;
import net.famzangl.minecraft.minebot.ai.command.UnknownCommandException;
import net.famzangl.minecraft.minebot.ai.net.PersistentChat;
import net.famzangl.minecraft.minebot.ai.scripting.CommandJs.ScriptStrategy;
import net.famzangl.minecraft.minebot.ai.scripting.CommandJs.TickProvider;
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.InventoryDefinition;
import net.famzangl.minecraft.minebot.ai.strategy.RunFileStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.StackStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.StrategyStack;
import net.famzangl.minecraft.minebot.ai.strategy.WalkTowardsStrategy;
import net.famzangl.minecraft.minebot.ai.tools.ToolRater;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.command.arguments.EntitySelectorParser;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.math.vector.Vector2f;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
	 * @param command
	 * @throws UnknownCommandException
	 */
	public ScriptStrategy safeStrategy(String command, Object... arguments)
			throws CommandSyntaxException {
		return new ScriptStrategy(AIChatController.getRegistry()
				.evaluateCommandWithSaferule(waitForTick(), toCommandString(command, arguments)));
	}

	/**
	 * Get a minebot strategy.
	 * 
	 * @param command
	 * @throws UnknownCommandException
	 */
	public ScriptStrategy strategy(String command, Object... arguments)
			throws CommandSyntaxException {
		return new ScriptStrategy(AIChatController.getRegistry()
				.evaluateCommand(waitForTick(), toCommandString(command, arguments)));
	}

	private String toCommandString(String command, Object[] arguments) {
		return Stream.concat(
				Stream.of(command),
				Stream.of(arguments).map(Object::toString)
		).collect(Collectors.joining(" "));
	}


	public ScriptStrategy strategyWalkTowards(double x, double z) {
		return new ScriptStrategy(new WalkTowardsStrategy(x, z));
	}

	public WrappedBlockPos getPlayerBlockPosition() {
		return new WrappedBlockPos(waitForTick().getPlayerPosition());
	}

	public FoundEntity getPlayer() {
		return new FoundEntity(waitForTick().getMinecraft().player);
	}

	/**
	 * 
	 * @param entityDescr
	 * @return An array of found entities.
	 * @throws ScriptException
	 */
	public Object getEntities(String entityDescr) throws ScriptException {
		return getEntities(entityDescr, null);
	}

	/**
	 * See the testfor command.
	 * 
	 * @param entityDescr
	 * @param nbtO
	 * @return An array of found entities.
	 * @throws ScriptException
	 */
	public Object getEntities(String entityDescr, Object nbtO)
			throws ScriptException {
		final AIHelper helper = waitForTick();
		List<? extends Entity> entities;
		try {
			entities = new EntitySelectorParser(new StringReader(entityDescr))
					.build().select(
							new CommandSource(ICommandSource.DUMMY,
									helper.getMinecraft().player.getPositionVec(),
									Vector2f.ZERO, null,
									2, helper.getMinecraft().player.getName().getString(),
									helper.getMinecraft().player.getDisplayName(),
									helper.getMinecraft().world.getServer(),
									helper.getMinecraft().player));
		} catch (CommandException | CommandSyntaxException e2) {
			throw new ScriptException(e2);
		}

		CompoundNBT nbt = null;
		if (nbtO != null) {
			String nbtS = jsonify(nbtO);
			try {
				nbt = JsonToNBT.getTagFromJson(nbtS);
			} catch (CommandSyntaxException e1) {
				throw new ScriptException(e1);
			}
		}
		ArrayList<FoundEntity> foundEntities = new ArrayList<FoundEntity>();
		for (Entity e : entities) {
			if (nbt != null) {
				CompoundNBT nbttagcompound1 = new CompoundNBT();
				e.writeWithoutTypeId(nbttagcompound1);

				// TODO: Where did that go?
				// if (!CommandTestForBlock.(nbt, nbttagcompound1,
				// true)) {
				// continue;
				// }
			}
			foundEntities.add(new FoundEntity(e));
		}

		return toJSArray(foundEntities);
	}

	private Object toJSArray(List<?> entities) throws ScriptException {
		try {
			ScriptEngine engine = tickProvider.getEngine();
			Object array = engine.eval("[]");
			for (Object e : entities) {
				((Invocable) engine).invokeMethod(array, "push", e);
			}
			return array;
		} catch (NoSuchMethodException e1) {
			throw new ScriptException(e1);
		}
	}

	public void displayChat(String message) {
		AIChatController.addChatLine(message);
	}

	private ChatMessage[] chatMessageCache = new ChatMessage[0];

	public ChatMessage[] getChatMessages() throws ScriptException {
		List<PersistentChat> messages = waitForTick().getNetworkHelper()
				.getChatMessages();
		int len = messages.size();
		if (len > chatMessageCache.length) {
			int oldLength = chatMessageCache.length;
			chatMessageCache = Arrays.copyOf(chatMessageCache, len);
			for (int i = oldLength; i < len; i++) {
				PersistentChat message = messages.get(i);
				chatMessageCache[i] = new ChatMessage(message,
						tickProvider.getEngine());
			}
		}
		return chatMessageCache;
	}

	public boolean isAlive() {
		return waitForTick().isAlive();
	}

	public int getExperienceLevel() {
		return waitForTick().getMinecraft().player.experienceLevel;
	}

	public int getCurrentTime() {
		return (int) (waitForTick().getMinecraft().world.getGameTime() % 24000l);
	}

	public InventoryDefinition getInventory() {
		return new InventoryDefinition(
				waitForTick().getMinecraft().player.inventory);
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
				throw new IllegalArgumentException("Class " + s.getClass()
						+ " is not strategy.");
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
		if (strategy == null) {
			throw new NullPointerException();
		}
		tickProvider.setActiveStrategy(strategy, waitForTick());
		tickProvider.tickDone();
		tickProvider.pauseForStrategy();
		if (strategy.hasFailed()) {
			throw new StrategyFailedException();
		}
	}

	public void setDescription(String description) {
		tickProvider.getDescription().setDescription(description);
	}

	public void setAddScriptNameToDescription(boolean val) {
		tickProvider.getDescription().setAddFileName(val);
	}

	public void setAddStrategyToDescription(boolean val) {
		tickProvider.getDescription().setAddStrategyDescription(val);
	}

	public void serverCommand(String command) {
		RunFileStrategy.runCommand(waitForTick(), command);
		tickProvider.tickDone();
	}

	public ToolRaterResult searchTool(Object toolRaterO) throws ScriptException {
		String toolRater = jsonify(toolRaterO);
		ToolRater rater = ToolRater.createToolRaterFromJson(toolRater);
		return waitForTick().searchToolFor(null, rater);
	}

	private String jsonify(Object toolRaterO) throws ScriptException {
		if (toolRaterO instanceof String) {
			return (String) toolRaterO;
		} else {
			Invocable inv = (Invocable) tickProvider.getEngine();
			try {
				return inv.invokeMethod(tickProvider.getEngine().eval("JSON"),
						"stringify", toolRaterO).toString();
			} catch (NoSuchMethodException e) {
				throw new ScriptException(e);
			}
		}
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
			throw new UnsupportedOperationException(
					"Persistence dir is not writeable.");
		} catch (UnsupportedEncodingException e) {
		}
	}

	public void getJSON(String key, String value) {
		// TODO
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