package net.famzangl.minecraft.minebot.build;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;


import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.build.blockbuild.BuildTask;

public class BuildManager {

	private ArrayList<Pos> buildArtefactPositions = new ArrayList<Pos>();

	private LinkedList<BuildTask> buildTaksk = new LinkedList<BuildTask>();

	public BuildManager() {
		reset();
	}

	public void reset() {
		buildTaksk.clear();
	}

	public BuildTask peekNextTask() {
		return buildTaksk.peek();
	}

	public void addTask(BuildTask task) {
		buildTaksk.add(task);
		System.out.println("Added " + task);
	}

	public BuildTask popNextTask() {
		return buildTaksk.pop();
	}

	public Iterable<BuildTask> getScheduled() {
		return Collections.unmodifiableList(buildTaksk);
	}
}
