package net.famzangl.minecraft.minebot.build;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.ItemFilter;
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
