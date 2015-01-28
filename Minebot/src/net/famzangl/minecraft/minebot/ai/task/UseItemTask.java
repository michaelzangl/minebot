package net.famzangl.minecraft.minebot.ai.task;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.ItemFilter;
import net.famzangl.minecraft.minebot.ai.strategy.TaskOperations;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;

public class UseItemTask extends AITask {
	private boolean clicked;
	private final ItemFilter filter;

	public UseItemTask() {
		this(null);
	}

	public UseItemTask(ItemFilter filter) {
		this.filter = filter;
	}

	@Override
	public boolean isFinished(AIHelper h) {
		return clicked;
	}

	@Override
	public void runTick(AIHelper h, TaskOperations o) {
		if (!clicked) {
			if (filter != null) {
				if (!h.selectCurrentItem(filter)) {
					return;
				}
			}

			final MovingObjectPosition objectMouseOver = h.getObjectMouseOver();
			if (objectMouseOver == null
					|| objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) {
				notFacingBlock(h);
				return;
			}
			if (!isBlockAllowed(h, objectMouseOver.getBlockPos())) {
				notFacingBlock(h);
				return;
			}

			h.overrideUseItem();
			clicked = true;
		}
	}

	protected void notFacingBlock(AIHelper h) {
	}

	protected boolean isBlockAllowed(AIHelper h, BlockPos pos) {
		return true;
	}
}
