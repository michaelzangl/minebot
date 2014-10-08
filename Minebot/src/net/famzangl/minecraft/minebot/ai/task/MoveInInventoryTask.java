package net.famzangl.minecraft.minebot.ai.task;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.strategy.TaskOperations;
import net.famzangl.minecraft.minebot.ai.task.error.StringTaskError;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;

/**
 * Move a given amount from one slot to an other, empty slot in the current open
 * container/inventory.
 * 
 * @author michael
 *
 */
public abstract class MoveInInventoryTask extends AITask {

	private boolean moveDone;

	/**
	 * All 3 methods should return constant values.
	 * 
	 * @return
	 */
	protected abstract int getFromStack(AIHelper h);

	protected abstract int getToStack(AIHelper h);

	/**
	 * How many items should be moved. Mind that items may be put back (might be
	 * a problem on get-only containers)
	 * @param currentCount 
	 * 
	 * @return
	 */
	protected abstract int getMissingAmount(AIHelper h, int currentCount);

	/**
	 * If not all items could be moved, this is called.
	 * 
	 * @param missing
	 *            Missing item count.
	 */
	protected void missingItems(int missing) {

	}

	@Override
	public boolean isFinished(AIHelper h) {
		return moveDone;
	}

	@Override
	public void runTick(AIHelper h, TaskOperations o) {
		final GuiContainer screen = (GuiContainer) h.getMinecraft().currentScreen;
		if (screen == null) {
			o.desync(new StringTaskError("Expected container to be open"));
			return;
		}
		int fromStack = getFromStack(h);
		int toStack = getToStack(h);
		if (fromStack < 0 || toStack < 0 || fromStack >= screen.inventorySlots.inventoryItemStacks.size() || toStack >= screen.inventorySlots.inventoryItemStacks.size()) {
			System.out.println("Attempet to move : " +fromStack + " -> " + toStack);
			o.desync(new StringTaskError("Invalid item move specification."));
			return;
		}
		Slot from = screen.inventorySlots.getSlot(fromStack);
		if (getSlotContentCount(from) <= 0) {
			o.desync(new StringTaskError("Nothing in source slot."));
			return;
		}

		Slot to = screen.inventorySlots.getSlot(toStack);
		int amount = getMissingAmount(h, getSlotContentCount(to));
		System.out.println("Move " + amount + " from " + fromStack + " to " + toStack);

		int limit = Math.min(to.getSlotStackLimit(), from.getStack()
				.getMaxStackSize());
		int missing = Math.min(amount, limit - getSlotContentCount(to));

		while (getSlotContentCount(from) <= missing
				&& getSlotContentCount(from) > 0) {
			missing -= moveAll(h, from, to);
		}

		System.out.println("Still missing (1): " + missing);
		while (getSlotContentCount(from) - getSlotContentCount(from) / 2 <= missing
				&& getSlotContentCount(from) > 0) {
			missing -= moveHalf(h, from, to);
		}
		System.out.println("Still missing (2): " + missing);
		if (missing > 0
				&& getSlotContentCount(from) > 0) {
			missing -= moveStackPart(h, from, to, missing);
		}
		System.out.println("Still missing (3): " + missing);
		if (missing > 0) {
			missingItems(missing);
		}
		moveDone = true;
	}

	private int moveAll(AIHelper h, Slot from, Slot to) {
		return moveStack(h, from, to, false);
	}

	private int moveHalf(AIHelper h, Slot from, Slot to) {
		return moveStack(h, from, to, true);
	}

	private int moveStack(AIHelper h, Slot from, Slot to,
			boolean rightclickOnStart) {
		int oldCount = getSlotContentCount(to);

		click(h, from.slotNumber,rightclickOnStart ? 1 : 0);
		
		click(h, to.slotNumber, 0);
		return getSlotContentCount(to) - oldCount;
	}

	private void click(AIHelper h, int slotNumber, int i) {
		System.out.println("Click on " + slotNumber + " using " + i);
		final GuiContainer screen = (GuiContainer) h.getMinecraft().currentScreen;
		h.getMinecraft().playerController.windowClick(
				screen.inventorySlots.windowId, slotNumber, i, 0, h.getMinecraft().thePlayer);
	}

	private int moveStackPart(AIHelper h, Slot from, Slot to, int count) {
		int oldCount = getSlotContentCount(to);

		click(h, from.slotNumber, 0);
		for (int i = 0; i < count; i++) {
			click(h, to.slotNumber, 1);
		}
		click(h, from.slotNumber, 0);
		return getSlotContentCount(to) - oldCount;
	}

	private int getSlotContentCount(Slot slot) {
		return slot.getHasStack() ? slot.getStack().stackSize : 0;
	}

}
