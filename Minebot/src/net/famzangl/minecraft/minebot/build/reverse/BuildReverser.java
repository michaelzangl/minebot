package net.famzangl.minecraft.minebot.build.reverse;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.build.blockbuild.BuildTask;
import net.famzangl.minecraft.minebot.build.blockbuild.TaskDescription;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.util.ForgeDirection;

public class BuildReverser {
	private AIHelper helper;
	private Pos pos1;
	private Pos pos2;
	private ReverseBuildField field;

	public BuildReverser(AIHelper helper) {
		this(helper, minPos(helper.getPos1(), helper.getPos2()), maxPos(
				helper.getPos1(), helper.getPos2()));
	}

	private static Pos minPos(Pos p1, Pos p2) {
		return new Pos(Math.min(p1.x, p2.x), Math.min(p1.y, p2.y), Math.min(
				p1.z, p2.z));
	}

	private static Pos maxPos(Pos p1, Pos p2) {
		return new Pos(Math.max(p1.x, p2.x), Math.max(p1.y, p2.y), Math.max(
				p1.z, p2.z));
	}

	public BuildReverser(AIHelper helper, Pos pos1, Pos pos2) {
		this.helper = helper;
		this.pos1 = pos1;
		this.pos2 = pos2;
		field = new ReverseBuildField(pos2.x - pos1.x + 1, pos2.y - pos1.y + 1,
				pos2.z - pos1.z + 1);
	}

	public void run() {
		for (int y = pos1.y; y <= pos2.y; y++) {
			for (int x = pos1.x; x <= pos2.x; x++) {
				addRow(new Pos(x, y, pos1.z), ForgeDirection.SOUTH, pos2.z
						- pos1.z + 1);
			}
		}
	}

	public void addRow(Pos start, ForgeDirection direction, int length) {
		for (int i = 0; i < length; i++) {
			addBuildPlace(start.x + direction.offsetX * i, start.y
					+ direction.offsetY * i, start.z + direction.offsetZ * i);
		}
	}

	private void addBuildPlace(int x, int y, int z) {
		int lx = x - pos1.x;
		int ly = y - pos1.y;
		int lz = z - pos1.z;

		Block b = helper.getBlock(x, y, z);
		if (b != Blocks.air) {
			TaskDescription taskString = BuildTask.getTaskDescription(b, helper, x, y, z);
			field.setBlockAt(lx, ly, lz, b, taskString);
			System.out.println("/minebot build:schedule ~" + lx + " ~" + ly
					+ " ~" + lz + " " + taskString.getCommandArgs());
		}
	}
}
