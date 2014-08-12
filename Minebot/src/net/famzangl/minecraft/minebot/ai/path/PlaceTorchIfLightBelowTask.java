package net.famzangl.minecraft.minebot.ai.path;

import java.util.ArrayList;
import java.util.Arrays;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.BlockItemFilter;
import net.famzangl.minecraft.minebot.ai.task.PlaceTorchSomewhereTask;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.util.ForgeDirection;

public class PlaceTorchIfLightBelowTask extends PlaceTorchSomewhereTask {

	private static final BlockItemFilter TORCH_FILTER = new BlockItemFilter(
			Blocks.torch);
	private final Pos currentPos;
	private final float torchLightLevel;

	public PlaceTorchIfLightBelowTask(Pos currentPos,
			ForgeDirection doNotPlaceAt, float torchLightLevel) {
		super(Arrays.asList(currentPos, currentPos.add(0, 1, 0)),
				getDirections(doNotPlaceAt));
		this.currentPos = currentPos;
		this.torchLightLevel = torchLightLevel;
	}

	private static ForgeDirection[] getDirections(ForgeDirection except) {
		ArrayList<ForgeDirection> allowed = new ArrayList<ForgeDirection>();
		for (ForgeDirection d : ForgeDirection.VALID_DIRECTIONS) {
			if (d != except && d != ForgeDirection.UP) {
				allowed.add(d);
			}
		}
		return allowed.toArray(new ForgeDirection[allowed.size()]);
	}

	@Override
	public boolean isFinished(AIHelper h) {
		return h.getLightAt(currentPos) > torchLightLevel
				|| !h.canSelectItem(TORCH_FILTER) || super.isFinished(h);
	}

}
