package net.famzangl.minecraft.minebot.build.reverse;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

import net.famzangl.minecraft.minebot.MinebotMod;
import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.command.AIChatController;
import net.famzangl.minecraft.minebot.build.blockbuild.BuildTask;
import net.famzangl.minecraft.minebot.build.blockbuild.TaskDescription;
import net.famzangl.minecraft.minebot.build.blockbuild.UnknownBlockException;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.util.ForgeDirection;

public class BuildReverser {
	private final AIHelper helper;
	private final Pos pos1;
	private final Pos pos2;
	private final ReverseBuildField field;
	private String outFile;
	private PrintStream out;
	private int missingBlocks;

	public BuildReverser(AIHelper helper, String outFile) {
		this(helper, Pos.minPos(helper.getPos1(), helper.getPos2()), Pos
				.maxPos(helper.getPos1(), helper.getPos2()), outFile);
	}

	public BuildReverser(AIHelper helper, Pos pos1, Pos pos2, String outFile) {
		this.helper = helper;
		this.pos1 = pos1;
		this.pos2 = pos2;
		field = new ReverseBuildField(pos2.x - pos1.x + 1, pos2.y - pos1.y + 1,
				pos2.z - pos1.z + 1);
		this.outFile = outFile;
	}

	public void run() {
		try {
			if (outFile == null || outFile.isEmpty() || "-".equals(outFile)) {
				this.outFile = "-";
				this.out = System.out;
			} else {
				this.out = new PrintStream(outFile);
			}
			out.println("# Minebot reverse build script "
					+ MinebotMod.getVersion());
			out.println("# Pos1: " + pos1);
			out.println("# Pos2: " + pos2);
			out.println("");
			out.println("/minebuild reset");
			out.println("");
			for (int y = pos1.y; y <= pos2.y; y++) {
				out.println("# Layer " + (y - pos1.y));
				for (int x = pos1.x; x <= pos2.x; x++) {
					final boolean row2 = (x - pos1.x & 1) == 1;
					addRow(new Pos(x, y, row2 ? pos2.z : pos1.z),
							row2 ? ForgeDirection.NORTH : ForgeDirection.SOUTH,
							pos2.z - pos1.z + 1);
				}
				out.println("");
			}
			out.println("#/minebuild build");

			if (missingBlocks > 0) {
				AIChatController.addChatLine("Could not convert "
						+ missingBlocks + "blocks. They will be missing.");
			}
			AIChatController.addChatLine("Output written to: " + this.outFile);
		} catch (final FileNotFoundException e) {
			AIChatController.addChatLine("File/dir does not exist: " + outFile);
		} catch (final IOException e) {
			AIChatController.addChatLine("IO-Error for: " + outFile);
		} finally {
			if (out != null) {
				out.close();
				out = null;
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
		final int lx = x - pos1.x;
		final int ly = y - pos1.y;
		final int lz = z - pos1.z;

		final Block b = helper.getBlock(x, y, z);
		if (b != Blocks.air) {
			try {
				final TaskDescription taskString = BuildTask
						.getTaskDescription(b, helper, x, y, z);
				field.setBlockAt(lx, ly, lz, b, taskString);
				out.println("/minebuild schedule ~" + lx + " ~" + ly + " ~"
						+ lz + " " + taskString.getCommandArgs());
			} catch (final UnknownBlockException e) {
				out.println("# Missing: ~" + lx + " ~" + ly + " ~" + lz + " "
						+ b.getLocalizedName());
				missingBlocks++;
			}
		}
	}
}
