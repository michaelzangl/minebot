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
package net.famzangl.minecraft.minebot.build;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import net.famzangl.minecraft.minebot.ai.ItemFilter;
import net.famzangl.minecraft.minebot.ai.path.world.Pos;
import net.famzangl.minecraft.minebot.build.blockbuild.BuildTask;

/**
 * This build manager exists once per AI helper (that means once per game). It
 * is the state machine for all build tasks and basically hodls a list of things
 * to build
 * 
 * @author michael
 *
 */
public class BuildManager {

	private final ArrayList<Pos> buildArtefactPositions = new ArrayList<Pos>();

	private final LinkedList<BuildTask> buildTasks = new LinkedList<BuildTask>();

	private ItemFilter lastMissing;

	public BuildManager() {
		reset();
	}

	public void reset() {
		buildTasks.clear();
	}

	public BuildTask peekNextTask() {
		return buildTasks.peek();
	}

	public void addTask(BuildTask task) {
		buildTasks.add(task);
		System.out.println("Added " + task);
	}

	public BuildTask popNextTask() {
		return buildTasks.pop();
	}

	public List<BuildTask> getScheduled() {
		return Collections.unmodifiableList(buildTasks);
	}

	// public void missingItem(ItemFilter itemFiler) {
	// if (itemFiler != lastMissing) {
	// AIChatController.addChatLine("Cannot handle missing item: " +
	// CommandCount.niceFilterName(itemFiler));
	// }
	// lastMissing = itemFiler;
	// }
}
