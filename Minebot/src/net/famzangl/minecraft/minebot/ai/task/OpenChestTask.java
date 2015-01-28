package net.famzangl.minecraft.minebot.ai.task;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.util.BlockPos;

public class OpenChestTask extends UseItemTask {

	private static final double SIDE_DIST = 0.07;
	private static final double TOP = 0.85;
	private static final double BOTTOM = 0.03;
	private final BlockPos p1;
	private final BlockPos p2;
	private int attempts;

	public OpenChestTask(BlockPos p1, BlockPos p2) {
		super();
		this.p1 = p1;
		this.p2 = p2;
	}

	@Override
	public boolean isFinished(AIHelper h) {
		return h.getMinecraft().currentScreen instanceof GuiChest;
	}

	@Override
	protected boolean isBlockAllowed(AIHelper h, BlockPos pos) {
		return p1 != null && p1.equals(pos) || p2 != null && p2.equals(pos);
	}

	@Override
	protected void notFacingBlock(AIHelper h) {
		attempts++;
		attempts &= 15;
		BlockPos p;
		if ((attempts & 0x8) == 0 && p1 != null || p2 == null) {
			p = p1;
		} else {
			p = p2;
		}
		double dx = (attempts & 0x1) != 0 ? SIDE_DIST : 1 - SIDE_DIST;
		double dy = (attempts & 0x2) != 0 ? BOTTOM : TOP;
		double dz = (attempts & 0x4) != 0 ? SIDE_DIST : 1 - SIDE_DIST;
		h.face(p.getX() + dx, p.getY() + dy, p.getZ() + dz);
	}
}
