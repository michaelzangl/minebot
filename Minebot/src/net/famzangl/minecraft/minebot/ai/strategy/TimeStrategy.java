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

/**
 * A strategy that needs to measure time.
 * 
 * @author michael
 *
 */
public abstract class TimeStrategy extends AIStrategy {

	private long startTime = -1;

	/**
	 * Gets how many ticks happend since the first time this was called.
	 * 
	 * @return 0 or more.
	 */
	protected long getTimeElapsed(AIHelper helper) {
		long time = helper.getMinecraft().theWorld.getTotalWorldTime();
		if (startTime < 0) {
			startTime = time;
			return 0;
		} else {
			return time - startTime;
		}

	}

}
