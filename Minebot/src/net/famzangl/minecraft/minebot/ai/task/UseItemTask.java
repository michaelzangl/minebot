package net.famzangl.minecraft.minebot.ai.task;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.ItemFilter;
import net.minecraft.util.MovingObjectPosition;

public class UseItemTask implements AITask {
	private boolean clicked;
	private ItemFilter filter;

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
	public void runTick(AIHelper h) {
		if (!clicked) {
			if (filter != null) {
				if (!h.selectCurrentItem(filter)) {
					return;
				}
			}

			MovingObjectPosition objectMouseOver = h.getObjectMouseOver();
			if (objectMouseOver == null
					|| objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) {
				notFacingBlock(h);
				return;
			}
			if (!isBlockAllowed(h, objectMouseOver.blockX,
					objectMouseOver.blockY, objectMouseOver.blockZ)) {
				notFacingBlock(h);
				return;
			}

			h.overrideUseItem();
			clicked = true;
		}
	}

	protected void notFacingBlock(AIHelper h) {
	}

	protected boolean isBlockAllowed(AIHelper h, int blockX, int blockY,
			int blockZ) {
		return true;
	}
}
