package net.famzangl.minecraft.minebot.ai.strategy;

import net.famzangl.minecraft.minebot.MinebotSettings;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.commands.CommandRun;
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
	private final MinebotSettings settings;
	private boolean shouldLogOut;
	private boolean shouldRunCommand;
	private boolean shouldStop;
	private static final float EPSILON = 0.01f;

	public ValueActionStrategy() {
		settings = new MinebotSettings();
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
		return settings.getFloat(getSettingPrefix() + string + "_value", -1);
	}

	protected abstract double getValue(AIHelper helper);

	protected abstract String getSettingPrefix();

	@Override
	protected TickResult onGameTick(AIHelper helper) {
		if (shouldRunCommand) {
			CommandRun.runCommand(helper,
					settings.get(getSettingPrefix() + "_command", ""));
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

	public static AIStrategy makeSafe(AIStrategy baseStrategy, boolean hard) {
		final StrategyStack stack = new StrategyStack();
		if (hard) {
			stack.addStrategy(new DoNotSuffocateStrategy());
		}
		stack.addStrategy(new DamageTakenStrategy());
		stack.addStrategy(new PlayerComesActionStrategy());
		stack.addStrategy(new CreeperComesActionStrategy());
		stack.addStrategy(baseStrategy);
		return new StackStrategy(stack);
	}

}
