package net.famzangl.minecraft.minebot.ai.task.place;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.ClassItemFilter;
import net.famzangl.minecraft.minebot.ai.task.TaskOperations;
import net.minecraft.item.ItemSign;
import net.minecraft.util.BlockPos;

/**
 * Place a sign on the ground.
 * Use an {@link SetSignTextTask} afterwards!
 * 
 * @author michael
 *
 */
public class SignPlaceOnGroundTask extends PlaceBlockAtFloorTask {

	private final int direction;
	private final SetSignTextTask textTask;

	public SignPlaceOnGroundTask(BlockPos pos, int direction, String[] text) {
		super(pos, new ClassItemFilter(ItemSign.class));
		this.direction = direction;
		textTask = new SetSignTextTask(pos, text);
	}
	
	@Override
	public boolean isFinished(AIHelper h) {
		return super.isFinished(h) && textTask.isFinished(h);
	}

	@Override
	public void runTick(AIHelper h, TaskOperations o) {
		if (super.isFinished(h)) {
			textTask.runTick(h, o);
		} else {
			super.runTick(h, o);
		}
	}
	
	@Override
	protected void faceBlock(AIHelper h, TaskOperations o) {
		// TODO Auto-generated method stub
		super.faceBlock(h, o);
	}
	
	@Override
	protected boolean isFacingRightBlock(AIHelper h) {
		return super.isFacingRightBlock(h) && isGoodForDirection(h.getMinecraft().thePlayer.rotationYaw);
	}

	private boolean isGoodForDirection(float rotationYaw) {
		float myYaw = rotationYaw / 260 * 16 + .5f;
		int myDirection = ((int) Math.floor(myYaw)) & 15;
		return myDirection == direction;
	}
}
