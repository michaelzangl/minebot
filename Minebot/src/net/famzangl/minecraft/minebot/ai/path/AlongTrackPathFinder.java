package net.famzangl.minecraft.minebot.ai.path;

import net.famzangl.minecraft.minebot.ai.AIHelper;

public class AlongTrackPathFinder extends MovePathFinder {
	protected final int dx;
	protected final int dz;
	protected final int cx;
	protected final int cy;
	protected final int cz;

	public AlongTrackPathFinder(AIHelper helper, int dx, int dz, int cx,
			int cy, int cz) {
		super(helper);
		this.dx = dx;
		this.dz = dz;
		this.cx = cx;
		this.cy = cy;
		this.cz = cz;
	}

	protected boolean isOnTrack(int x, int z) {
		return dz != 0 && x == cx && dz * (z - cz) >= 0 || dx != 0 && z == cz
				&& dx * (x - cx) >= 0;
	}

	/**
	 * Only works if (x, y, z) is on track.
	 * 
	 * @param x
	 * @param z
	 * @return
	 */
	protected int getStepNumber(int x, int z) {
		return Math.abs(x - cx + z - cz);
	}

}
