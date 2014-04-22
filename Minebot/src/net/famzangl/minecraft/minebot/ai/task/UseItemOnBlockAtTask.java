package net.famzangl.minecraft.minebot.ai.task;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.ItemFilter;

public class UseItemOnBlockAtTask extends UseItemTask {

	private final int x, y, z;

	public UseItemOnBlockAtTask(ItemFilter filter, int x, int y, int z) {
		super(filter);
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public UseItemOnBlockAtTask(int x, int y, int z) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public String toString() {
		return "UseItemOnBlockAtTask [x=" + x + ", y=" + y + ", z=" + z + "]";
	}

	@Override
	protected boolean isBlockAllowed(AIHelper h, int blockX, int blockY,
			int blockZ) {
		return blockX == x && blockY == y && blockZ == z;
	}

	@Override
	protected void notFacingBlock(AIHelper h) {
		h.faceBlock(x, y, z);
	}
}
