/*******************************************************************************
 * This file is part of Minebot.
 *
 * Minebot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Minebot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Minebot.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package net.famzangl.minecraft.minebot.ai.task.place;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.ClassItemFilter;
import net.famzangl.minecraft.minebot.ai.task.TaskOperations;
import net.minecraft.item.SignItem;
import net.minecraft.util.math.BlockPos;

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
		super(pos, new ClassItemFilter(SignItem.class));
		this.direction = direction;
		textTask = new SetSignTextTask(pos, text);
	}
	
	@Override
	public boolean isFinished(AIHelper aiHelper) {
		return super.isFinished(aiHelper) && textTask.isFinished(aiHelper);
	}

	@Override
	public void runTick(AIHelper aiHelper, TaskOperations taskOperations) {
		if (super.isFinished(aiHelper)) {
			textTask.runTick(aiHelper, taskOperations);
		} else {
			super.runTick(aiHelper, taskOperations);
		}
	}
	
	@Override
	protected void faceBlock(AIHelper aiHelper, TaskOperations o) {
		// TODO Auto-generated method stub
		super.faceBlock(aiHelper, o);
	}
	
	@Override
	protected boolean isFacingRightBlock(AIHelper aiHelper) {
		return super.isFacingRightBlock(aiHelper) && isGoodForDirection(aiHelper.getMinecraft().player.rotationYaw);
	}

	private boolean isGoodForDirection(float rotationYaw) {
		float myYaw = rotationYaw / 260 * 16 + .5f;
		int myDirection = ((int) Math.floor(myYaw)) & 15;
		return myDirection == direction;
	}
}
