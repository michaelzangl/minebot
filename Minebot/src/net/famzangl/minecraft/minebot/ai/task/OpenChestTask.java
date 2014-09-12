package net.famzangl.minecraft.minebot.ai.task;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.minecraft.client.gui.inventory.GuiChest;

public class OpenChestTask extends UseItemTask {

	private static final double SIDE_DIST = 0.1;
	private static final double TOP = 0.8;
	private static final double BOTTOM = 0.03;
	private final Pos p1;
	private final Pos p2;
	private int attempts;

	public OpenChestTask(Pos p1, Pos p2) {
		super();
		this.p1 = p1;
		this.p2 = p2;
	}
	
	@Override
	public boolean isFinished(AIHelper h) {
		return h.getMinecraft().currentScreen instanceof GuiChest;
	}

	@Override
	protected boolean isBlockAllowed(AIHelper h, int blockX, int blockY,
			int blockZ) {
		return p1 != null && blockX == p1.x && blockY == p1.y && blockZ == p1.z
				|| p2 != null && blockX == p2.x && blockY == p2.y
				&& blockZ == p2.z;
	}

	@Override
	protected void notFacingBlock(AIHelper h) {
		attempts++;
		attempts &= 15;
		Pos p;
		if ((attempts & 0x8) == 0 && p1 != null || p2 == null) {
			p = p1;
		} else {
			p = p2;
		}
		double dx = (attempts & 0x1) != 0 ? SIDE_DIST : 1 - SIDE_DIST;
		double dy = (attempts & 0x2) != 0 ? BOTTOM : TOP;
		double dz = (attempts & 0x4) != 0 ? SIDE_DIST : 1 - SIDE_DIST;
		h.face(p.x + dx, p.y + dy, p.z + dz);
	}
}
