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
package net.famzangl.minecraft.minebot.ai.task;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.net.NetworkHelper;
import net.minecraft.entity.projectile.EntityFishHook;

/**
 * This task right-clicks as soon as a fish bit on the fishing rod. It assumes
 * the player is already holding the rod in water.
 * 
 * @author michael
 *
 */
public class DoFishTask extends AITask {
	private static final Marker MARKER_FISH = MarkerManager.getMarker("fish");

	private boolean revoked;
	private int rightMotion = 2;
	private boolean inThrowingPhase = true;
	private boolean sendReset = true;

	@Override
	public boolean isFinished(AIHelper h) {
		return revoked || h.getMinecraft().thePlayer.fishEntity == null;
	}

	@Override
	public void runTick(AIHelper h, TaskOperations o) {
		if (fishIsCaptured(h)) {
			h.overrideUseItem();
			revoked = true;
		}
	}

	@Override
	public int getGameTickTimeout(AIHelper helper) {
		return 1000; // max 900
	}

	private boolean fishIsCaptured(AIHelper helper) {
		// Called in client start thick phase.
		// We know that the next call to the player controller is to check for
		// network packages.
		// Best would be to intercept a wake particle package with i > 1 or e ==
		// g or h == 0.2
		NetworkHelper minebotNetHandler = helper.getNetworkHelper();
		if (minebotNetHandler == null) {
			return false;
		}
		if (sendReset) {
			minebotNetHandler.resetFishState();
			sendReset = false;
		}
		return minebotNetHandler
				.fishIsCaptured(helper.getMinecraft().thePlayer.fishEntity);
	}

	/**
	 * Only works in single player. Injecting the net handler is our best
	 * option.
	 * 
	 * @param helper
	 * @return
	 */
	private boolean fishIsCapturedSP(AIHelper helper) {
		final EntityFishHook fishEntity = helper.getMinecraft().thePlayer.fishEntity;
		if (fishEntity == null) {
			return false;
		}
		if (fishEntity.motionY < -0.05) {
			LOGGER.trace(MARKER_FISH, "Fish motion: " + fishEntity.motionY + ", " + fishEntity.posY);
			rightMotion--;
		} else {
			rightMotion = 2;
			inThrowingPhase = false;
		}
		return !inThrowingPhase && rightMotion <= 0;
		// try {
		// Field field = fishEntity.getClass().getDeclaredField(
		// "field_146045_ax");
		// field.setAccessible(true);
		// int value = field.getInt(fishEntity);
		// return value == 0;
		// } catch (Throwable e) {
		// for (Field f : fishEntity.getClass().getDeclaredFields()) {
		// System.out.println("Field: " + f.getName() + " is "
		// + f.getType());
		// }
		//
		// e.printStackTrace();
		// return false;
		// }
	}

}
