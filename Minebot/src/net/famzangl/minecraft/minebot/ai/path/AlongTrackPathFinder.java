package net.famzangl.minecraft.minebot.ai.path;

public class AlongTrackPathFinder extends MovePathFinder {
	protected final int dx;
	protected final int dz;
	protected final int cx;
	protected final int cy;
	protected final int cz;
	protected final Integer length;

	public AlongTrackPathFinder(int dx, int dz, int cx, int cy, int cz, int length) {
		this.dx = dx;
		this.dz = dz;
		this.cx = cx;
		this.cy = cy;
		this.cz = cz;
		this.length = length;
	}

	protected boolean isOnTrack(int x, int z) {
		return dz != 0 && x == cx && dz * (z - cz) >= 0 || dx != 0 && z == cz
				&& dx * (x - cx) >= 0 && (length < 0 || getStepNumber(x, z) <= length);
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
