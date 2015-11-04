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
import java.util.List;
import java.util.Scanner;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.command.AIChatController;
import net.famzangl.minecraft.minebot.ai.command.UnknownCommandException;
import net.famzangl.minecraft.minebot.ai.commands.CommandRun;
import net.famzangl.minecraft.minebot.ai.scripting.CommandJs.RunScriptStrategy;
import net.famzangl.minecraft.minebot.ai.scripting.CommandJs.ScriptStrategy;
import net.famzangl.minecraft.minebot.ai.scripting.CommandJs.TickProvider;
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
		return tickProvider.getHelper();
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

	public BlockPos getPlayerPosition() {
		return waitForTick().getPlayerPosition();
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
			return new Scanner(file, "UTF-8").useDelimiter("\\A").next();
		} catch (FileNotFoundException e) {
			return defaultValue;
		}
	}

	public void storeString(String key, String value) {
		File file = getPersistenceFile(key);

		try {
			new PrintWriter(file, "UTF-8").print(value);
		} catch (FileNotFoundException e) {
			throw new UnsupportedOperationException("Persistence dir is not writeable.");
		} catch (UnsupportedEncodingException e) {
		}
	}

	private File getPersistenceFile(String key) {
		if (!key.matches("[a-zA-Z0-8\\._-]")) {
			throw new IllegalArgumentException("Invalid key name: " + key);
		}
		File dir = AIHelper.getMinebotDir();
		File persistence = new File(dir, "js-persistence");
		if (!persistence.exists()) {
			persistence.mkdirs();
		}
		File file = new File(dir, key);
		return file;
	}
}