package net.famzangl.minecraft.minebot.ai.render;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.build.BuildManager;
import net.famzangl.minecraft.minebot.build.blockbuild.BuildTask;
import net.minecraftforge.client.event.RenderWorldLastEvent;

public class BuildMarkerRenderer extends RenderHelper {

	private final class CornerComparator implements Comparator<BuildTask> {
		private final int[] orders;
		private final boolean[] inverts;

		public CornerComparator(int[] orders, boolean[] inverts) {
			super();
			this.orders = orders;
			this.inverts = inverts;
		}

		@Override
		public int compare(BuildTask o1, BuildTask o2) {
			final Pos p1 = o1.getForPosition();
			final Pos p2 = o2.getForPosition();
			final int[] points = new int[] { p1.x, p1.y, p1.z };
			final int[] points2 = new int[] { p2.x, p2.y, p2.z };
			for (int i = 0; i < 3; i++) {
				final int res = ((Integer) points[orders[i]])
						.compareTo(points2[orders[i]]);
				if (res != 0) {
					return inverts[i] ? -res : res;
				}
			}
			return 0;
		}
	}

	public void render(RenderWorldLastEvent event, AIHelper helper) {
		renderStart(event, helper);
		final BuildManager buildManager = helper.buildManager;
		final List<BuildTask> scheduled = buildManager.getScheduled();
		if (scheduled.size() > 0) {
			final BuildTask nextTask = scheduled.get(0);
			final HashSet<Pos> corners = new HashSet<Pos>();

			renderMarker(nextTask.getForPosition(), 1, 1, 0, 0.7f);
			findCorners(corners, scheduled);
			corners.remove(nextTask.getForPosition());
			for (final Pos c : corners) {
				renderMarker(c, 0, 0, 1, 0.5f);
			}
		}
		renderEnd();
	}

	private void findCorners(HashSet<Pos> corners, List<BuildTask> scheduled) {
		for (int i = 0; i < 3; i++) {
			addWith(corners, scheduled, i, true);
			addWith(corners, scheduled, i, false);
		}
	}

	private void addWith(HashSet<Pos> corners, List<BuildTask> scheduled,
			int side1, boolean side1Inverted) {
		for (int i = 0; i < 3; i++) {
			if (i != side1) {
				addWith(corners, scheduled, side1, side1Inverted, i, true);
				addWith(corners, scheduled, side1, side1Inverted, i, false);
			}
		}
	}

	private void addWith(HashSet<Pos> corners, List<BuildTask> scheduled,
			int side1, boolean side1Inverted, int side2, boolean side2Inverted) {
		for (int i = 0; i < 3; i++) {
			if (i != side1 && i != side2) {
				addWith(corners, scheduled, side1, side1Inverted, side2,
						side2Inverted, i, true);
				addWith(corners, scheduled, side1, side1Inverted, side2,
						side2Inverted, i, false);
			}
		}
	}

	private void addWith(HashSet<Pos> corners, List<BuildTask> scheduled,
			int side1, boolean side1Inverted, int side2, boolean side2Inverted,
			int side3, boolean side3Inverted) {
		final BuildTask p = Collections.min(scheduled, new CornerComparator(
				new int[] { side1, side2, side3 }, new boolean[] {
						side1Inverted, side2Inverted, side3Inverted }));
		corners.add(p.getForPosition());
	}

}
