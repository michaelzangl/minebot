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
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

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
		field = new ReverseBuildField(pos2.getX() - pos1.getX() + 1,
				pos2.getY() - pos1.getY() + 1, pos2.getZ() - pos1.getZ() + 1);
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
			for (int y = pos1.getY(); y <= pos2.getY(); y++) {
				out.println("# Layer " + (y - pos1.getY()));
				for (int x = pos1.getX(); x <= pos2.getX(); x++) {
					final boolean row2 = (x - pos1.getX() & 1) == 1;
					addRow(new Pos(x, y, row2 ? pos2.getZ() : pos1.getZ()),
							row2 ? EnumFacing.NORTH : EnumFacing.SOUTH,
							pos2.getZ() - pos1.getZ() + 1);
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

	public void addRow(Pos start, EnumFacing direction, int length) {
		for (int i = 0; i < length; i++) {
			addBuildPlace(start.offset(direction, length));
		}
	}

	private void addBuildPlace(BlockPos pos) {
		BlockPos localPos = pos.subtract(pos1);

		final Block b = helper.getBlock(pos);
		if (b != Blocks.air) {
			try {
				final TaskDescription taskString = BuildTask
						.getTaskDescription(b, helper, pos);
				field.setBlockAt(localPos, b, taskString);
				out.println("/minebuild schedule ~" + localPos.getX() + " ~"
						+ localPos.getY() + " ~" + localPos.getZ() + " "
						+ taskString.getCommandArgs());
			} catch (final UnknownBlockException e) {
				out.println("# Missing: ~" + localPos.getX() + " ~"
						+ localPos.getY() + " ~" + localPos.getZ() + " "
						+ b.getLocalizedName());
				missingBlocks++;
			}
		}
	}
}
