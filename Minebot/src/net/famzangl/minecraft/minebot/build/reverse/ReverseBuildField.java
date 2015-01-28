package net.famzangl.minecraft.minebot.build.reverse;

import net.famzangl.minecraft.minebot.build.blockbuild.TaskDescription;
import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;

public class ReverseBuildField {
	private final Block[][][] buildBlocks;
	private final TaskDescription[][][] buildNames;

	public ReverseBuildField(int lx, int ly, int lz) {
		buildBlocks = new Block[lx][ly][lz];
		buildNames = new TaskDescription[lx][ly][lz];
	}

	public void setBlockAt(BlockPos relativePos, Block block,
			TaskDescription taskString) {
		buildBlocks[relativePos.getX()][relativePos.getY()][relativePos.getZ()] = block;
		buildNames[relativePos.getX()][relativePos.getY()][relativePos.getZ()] = taskString;
	}

	public Block getBlock(int x, int y, int z) {
		return buildBlocks[x][y][z];
	}
}
