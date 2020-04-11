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

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;

/**
 * This class manages the description of a script (top right corner).
 * @author michael
 *
 */
public class DescriptionBuilder {
	private String description = "Running script";
	private boolean addStrategyDescription = false;
	private boolean addFileName = true;
	private AIStrategy activeStrategy;
	private final File fileName;

	public DescriptionBuilder(File fileName) {
		this.fileName = fileName;
	}

	public synchronized void setDescription(String description) {
		if (description == null) {
			throw new NullPointerException("Description is null.");
		}
		this.description = description;
	}

	public synchronized void setAddStrategyDescription(
			boolean addStrategyDescription) {
		this.addStrategyDescription = addStrategyDescription;
	}

	public synchronized void setAddFileName(boolean addFileName) {
		this.addFileName = addFileName;
	}

	public synchronized void setActiveStrategy(AIStrategy activeStrategy) {
		this.activeStrategy = activeStrategy;
	}

	public synchronized String getDescriptionString(AIHelper helper) {
		String str = description;
		if (addFileName) {
			str = fileName.getName() + ": " + str;
		}
		if (addStrategyDescription && activeStrategy != null) {
			str += "\n" + activeStrategy.getDescription(helper);
		}
		return str;
	}
	
	public synchronized void drawMarkers(RenderTickEvent event, AIHelper helper) {
		if (activeStrategy != null) {
			activeStrategy.drawMarkers(event, helper);
		}
	}
}
