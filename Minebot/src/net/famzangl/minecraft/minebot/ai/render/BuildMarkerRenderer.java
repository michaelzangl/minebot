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
package net.famzangl.minecraft.minebot.ai.render;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.build.BuildManager;
import net.famzangl.minecraft.minebot.build.blockbuild.BuildTask;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;

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
			final BlockPos p1 = o1.getForPosition();
			final BlockPos p2 = o2.getForPosition();
			final int[] points = new int[] { p1.getX(), p1.getY(), p1.getZ() };
			final int[] points2 = new int[] { p2.getX(), p2.getY(), p2.getZ() };
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

	public void render(RenderTickEvent event, AIHelper helper) {
		renderStart(event, helper);
		final BuildManager buildManager = helper.buildManager;
		final List<BuildTask> scheduled = buildManager.getScheduled();
		if (scheduled.size() > 0) {
			final BuildTask nextTask = scheduled.get(0);
			final HashSet<BlockPos> corners = new HashSet<BlockPos>();

			renderMarker(nextTask.getForPosition(), 1, 1, 0, 0.7f);
			findCorners(corners, scheduled);
			corners.remove(nextTask.getForPosition());
			for (final BlockPos c : corners) {
				renderMarker(c, 0, 0, 1, 0.5f);
			}
		}
		renderEnd();
	}

	private void findCorners(HashSet<BlockPos> corners, List<BuildTask> scheduled) {
		for (int i = 0; i < 3; i++) {
			addWith(corners, scheduled, i, true);
			addWith(corners, scheduled, i, false);
		}
	}

	private void addWith(HashSet<BlockPos> corners, List<BuildTask> scheduled,
			int side1, boolean side1Inverted) {
		for (int i = 0; i < 3; i++) {
			if (i != side1) {
				addWith(corners, scheduled, side1, side1Inverted, i, true);
				addWith(corners, scheduled, side1, side1Inverted, i, false);
			}
		}
	}

	private void addWith(HashSet<BlockPos> corners, List<BuildTask> scheduled,
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

	private void addWith(HashSet<BlockPos> corners, List<BuildTask> scheduled,
			int side1, boolean side1Inverted, int side2, boolean side2Inverted,
			int side3, boolean side3Inverted) {
		final BuildTask task = Collections.min(scheduled, new CornerComparator(
				new int[] { side1, side2, side3 }, new boolean[] {
						side1Inverted, side2Inverted, side3Inverted }));
		corners.add(task.getForPosition());
	}

}
