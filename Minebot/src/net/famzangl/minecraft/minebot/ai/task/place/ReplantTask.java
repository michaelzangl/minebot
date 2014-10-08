package net.famzangl.minecraft.minebot.ai.task.place;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.BlockItemFilter;
import net.minecraft.init.Blocks;

public class ReplantTask extends PlaceBlockAtFloorTask {

	public ReplantTask(int x, int y, int z) {
		super(x, y, z, new BlockItemFilter(Blocks.sapling));
	}
	
	@Override
	public boolean isFinished(AIHelper h) {
		if (!AIHelper.blockIsOneOf(h.getBlock(x, y - 1, z), Blocks.dirt, Blocks.grass)) {
			return true;
		}
		
		return super.isFinished(h);
	}

}
