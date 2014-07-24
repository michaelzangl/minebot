package net.famzangl.minecraft.minebot.ai.path;

import net.famzangl.minecraft.minebot.MinebotSettings;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.famzangl.minecraft.minebot.ai.task.LogOutTask;
import net.famzangl.minecraft.minebot.ai.task.SendCommandTask;

public class HealthWatcher {
	private static final float EPSILON = 0.01f;

	private final MinebotSettings settings = new MinebotSettings();

	private boolean commandSend;
	private boolean logoutSend;

	public AITask getOverrideTask(float health) {
		health -= EPSILON;
		final int commandTh = settings.getInt("on_damage_command_level", -1);
		if (health <= commandTh && !commandSend) {
			commandSend = true;
			return new SendCommandTask(settings.get("on_damage_command", ""));
		} else if (health > commandTh) {
			commandSend = false;
		}
		final int logoutTh = settings.getInt("on_damage_logout_level", -1);
		if (health <= logoutTh && !logoutSend) {
			logoutSend = true;
			System.out.println("Logging out. Current health: " + health);
			return new LogOutTask();
		} else if (health > commandTh) {
			logoutSend = false;
		}

		return null;
	}
}
