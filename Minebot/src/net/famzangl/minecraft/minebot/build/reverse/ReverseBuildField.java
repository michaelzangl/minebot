package net.famzangl.minecraft.minebot.build.reverse;

import net.famzangl.minecraft.minebot.build.blockbuild.TaskDescription;
import net.minecraft.block.Block;

public class ReverseBuildField {
	private Block[][][] buildBlocks;
	private TaskDescription[][][] buildNames;
	
	public ReverseBuildField(int lx, int ly, int lz) {
		buildBlocks = new Block[lx][ly][lz];
		buildNames = new TaskDescription[lx][ly][lz];
	}
	
	public void setBlockAt(int x, int y, int z, Block block, TaskDescription taskString) {
		buildBlocks[x][y][z] = block;
		buildNames[x][y][z] = taskString;
	}
	
	public Block getBlock(int x, int y, int z) {
		return buildBlocks[x][y][z];
	}
}
