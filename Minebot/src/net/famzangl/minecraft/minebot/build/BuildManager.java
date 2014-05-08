package net.famzangl.minecraft.minebot.build;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.build.blockbuild.BuildTask;

public class BuildManager {

	private ArrayList<Pos> buildArtefactPositions = new ArrayList<Pos>();

	private LinkedList<BuildTask> buildTasks = new LinkedList<BuildTask>();

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

	public Iterable<BuildTask> getScheduled() {
		return Collections.unmodifiableList(buildTasks);
	}
}
