package net.famzangl.minecraft.minebot.ai.task.place;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.ItemFilter;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.minecraftforge.common.util.ForgeDirection;

public class PlaceBlockAtFloorTask extends AITask {
	protected int x;
	protected int y;
	protected int z;
	private final ItemFilter filter;
	private int faceTimer;

	public PlaceBlockAtFloorTask(int x, int y, int z, ItemFilter filter) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.filter = filter;
	}

	protected int getPlaceAtY() {
		return y;
	}

	@Override
	public boolean isFinished(AIHelper h) {
		return !h.isAirBlock(x, getPlaceAtY(), z);
	}

	@Override
	public void runTick(AIHelper h) {
		if (faceTimer > 0) {
			faceTimer--;
		}
		if (h.isAirBlock(x, getPlaceAtY(), z)) {
			if (!h.selectCurrentItem(filter)) {
				System.out.println("Cannot select " + filter);
				h.desync();
			} else {
				if (faceTimer == 0) {
					faceBlock(h);
					faceTimer = 2;
				} else {
					tryPlaceBlock(h);
				}
			}
		}
	}

	protected void faceBlock(AIHelper h) {
		h.faceSideOf(x, getPlaceAtY() - 1, z, ForgeDirection.UP);
	}

	protected void tryPlaceBlock(AIHelper h) {
		if (h.getMinecraft().thePlayer.boundingBox.minY >= getPlaceAtY()
				&& isFacingRightBlock(h)) {
			h.overrideUseItem();
		}
	}

	protected boolean isFacingRightBlock(AIHelper h) {
		return h.isFacingBlock(x, getPlaceAtY() - 1, z, ForgeDirection.UP);
	}

	@Override
	public String toString() {
		return "UpwardsMoveTask [x=" + x + ", y=" + y + ", z=" + z + "]";
	}
}