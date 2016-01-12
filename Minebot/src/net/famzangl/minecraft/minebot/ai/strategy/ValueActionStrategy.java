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
package net.famzangl.minecraft.minebot.ai.strategy;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.settings.MinebotSettings;
import net.famzangl.minecraft.minebot.settings.MinebotSettingsRoot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.multiplayer.WorldClient;

/**
 * This stategy takes over whenever an event happens.
 * 
 * @author michael
 * 
 */
public abstract class ValueActionStrategy extends AIStrategy {

	private double lastValue = -1;
	private final MinebotSettingsRoot settings;
	private boolean shouldLogOut;
	private boolean shouldRunCommand;
	private boolean shouldStop;
	private static final float EPSILON = 0.01f;

	public ValueActionStrategy() {
		settings = MinebotSettings.getSettings();
	}

	@Override
	public boolean takesOverAnyTime() {
		return true;
	}

	@Override
	public boolean checkShouldTakeOver(AIHelper helper) {
		final double currentValue = getValue(helper);
		if (passedTh(lastValue, currentValue, getSetting("stop"))) {
			shouldStop = true;
		} else if (passedTh(lastValue, currentValue, getSetting("logout"))) {
			shouldLogOut = true;
		} else if (passedTh(lastValue, currentValue, getSetting("command"))) {
			shouldRunCommand = true;
		}

		lastValue = currentValue;
		return shouldLogOut || shouldStop || shouldRunCommand;
	}

	private boolean passedTh(double lastValue, double currentValue, double value) {
		final double v = value - EPSILON;
		return lastValue > v && currentValue <= v;
	}

	private double getSetting(String string) {
		return -1;// FIXME settings.getFloat(getSettingPrefix() + string +
					// "_value", -1);
	}

	protected abstract double getValue(AIHelper helper);

	protected abstract String getSettingPrefix();

	@Override
	protected TickResult onGameTick(AIHelper helper) {
		if (shouldRunCommand) {
			// FIXME CommandLoad.runCommand(helper,
			// settings.get(getSettingPrefix() + "_command", ""));
			shouldRunCommand = false;
			return TickResult.TICK_AGAIN;
		} else if (shouldLogOut) {
			final Minecraft mc = helper.getMinecraft();

			mc.theWorld.sendQuittingDisconnectingPacket();
			mc.loadWorld((WorldClient) null);
			mc.displayGuiScreen(new GuiMainMenu());
			shouldLogOut = false;
			return TickResult.TICK_HANDLED;
		} else if (shouldStop) {
			// Freeze here.
			return TickResult.TICK_HANDLED;
		} else {
			return TickResult.NO_MORE_WORK;
		}
	}

	@Override
	public String getDescription(AIHelper helper) {
		return shouldStop ? "Stopped because of " + getSettingPrefix() + "."
				: "Handling " + getSettingPrefix() + ".";
	}

}
