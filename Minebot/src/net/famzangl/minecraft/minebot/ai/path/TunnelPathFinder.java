package net.famzangl.minecraft.minebot.ai.path;

import java.util.ArrayList;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.task.DestroyInRangeTask;
import net.famzangl.minecraft.minebot.ai.task.PlaceTorchSomewhereTask;
import net.minecraft.init.Blocks;

public class TunnelPathFinder extends AlongTrackPathFinder {

	private final int addToSide;
	private final int addToTop;
	private final TorchSide torches;

	public static enum TorchSide {
		NONE(false, false),
		LEFT(true, false),
		RIGHT(false, true),
		BOTH(true, true);

		private final boolean left;
		private final boolean right;

		private TorchSide(boolean left, boolean right) {
			this.left = left;
			this.right = right;

		}
	}

	public TunnelPathFinder(AIHelper helper, int dx, int dz, int cx, int cy,
			int cz, int addToSide, int addToTop, TorchSide torches) {
		super(helper, dx, dz, cx, cy, cz);
		this.addToSide = addToSide;
		this.addToTop = addToTop;
		this.torches = torches;
	}

	@Override
	protected float rateDestination(int distance, int x, int y, int z) {
		if (isOnTrack(x, z) && y == cy && !AIHelper.blockIsOneOf(helper.getBlock(x, y, z), Blocks.air, Blocks.torch)) {
			return distance + 1;
		} else {
			return -1;
		}
	}

	@Override
	protected void addTasksForTarget(Pos currentPos) {
		Pos p1, p2;
		if (dx == 0) {
			p1 = new Pos(currentPos.x + addToSide, currentPos.y, currentPos.z);
			p2 = new Pos(currentPos.x - addToSide, currentPos.y + 1 + addToTop,
					currentPos.z);
		} else {
			p1 = new Pos(currentPos.x, currentPos.y, currentPos.z + addToSide);
			p2 = new Pos(currentPos.x, currentPos.y + 1 + addToTop,
					currentPos.z - addToSide);
		}
		helper.addTask(new DestroyInRangeTask(p1, p2));

		boolean isTorchStep = getStepNumber(currentPos.x, currentPos.z) % 8 == 0;
		if (torches.right && isTorchStep) {
			addTorchesTask(currentPos, -dz, dx);
			System.out.println("Adding torches");
		}
		if (torches.left && isTorchStep) {
			addTorchesTask(currentPos, dz, -dx);
			System.out.println("Adding torches");
		}
	}

	private void addTorchesTask(Pos currentPos, int dirX, int dirZ) {
		ArrayList<Pos> positions = new ArrayList<Pos>();
		positions.add(new Pos(currentPos.x + dirX * addToSide,
				currentPos.y + 1, currentPos.z + dirZ * addToSide));

		for (int i = addToSide; i >= 0; i--) {
			positions.add(new Pos(currentPos.x + dirX * i, currentPos.y,
					currentPos.z + dirZ * i));
		}
		helper.addTask(new PlaceTorchSomewhereTask(positions, AIHelper.getDirectionForXZ(dirX, dirZ)));
	}

	@Override
	public String toString() {
		return "TunnelPathFinder [addToSide=" + addToSide + ", addToTop="
				+ addToTop + ", dx=" + dx + ", dz=" + dz + ", cx=" + cx
				+ ", cy=" + cy + ", cz=" + cz + ", torches=" + torches + "]";
	}

}
