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
package net.famzangl.minecraft.minebot.ai.task.inventory;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.famzangl.minecraft.minebot.ai.task.TaskOperations;
import net.famzangl.minecraft.minebot.ai.task.error.StringTaskError;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.LinkedList;

/**
 * Move a given amount from one slot to an other, empty slot in the current open
 * container/inventory.
 * 
 * @author michael
 *
 */
public abstract class MoveInInventoryTask extends AITask {

	private static final Marker MARKER_MOVE = MarkerManager.getMarker("move");
	private boolean moveDone;

	private int delay;
	private LinkedList<AbstractClickAction> clicks = new LinkedList<>();

	public static final int DELAY = 5;

	/**
	 * All 3 methods should return constant values.
	 * 
	 * @return
	 */
	protected abstract int getFromStack(AIHelper aiHelper);

	protected int findItemInInventory(AIHelper aiHelper, Item item) {
		NonNullList<ItemStack> mainInventory = aiHelper.getMinecraft().player.inventory.mainInventory;
		int inventorySlot = -1;
		for (int i = 0; i < mainInventory.size(); i++) {
			if (item.equals(mainInventory.get(i).getItem())) {
				inventorySlot = i;
			}
		}
		return inventorySlot;
	}

	protected abstract int getToStack(AIHelper aiHelper);

	/**
	 * How many items should be moved. Mind that items may be put back (might be
	 * a problem on get-only containers)
	 * 
	 * @param currentCount
	 * 
	 * @return
	 */
	protected abstract int getMissingAmount(AIHelper aiHelper, int currentCount);

	/**
	 * If not all items could be moved, this is called.
	 * 
	 * @param missing
	 *            Missing item count.
	 */
	protected void missingItems(int missing) {

	}

	@Override
	public boolean isFinished(AIHelper aiHelper) {
		return moveDone && delay == 0 && clicks.isEmpty();
	}

	@Override
	public void runTick(AIHelper aiHelper, TaskOperations taskOperations) {
		final ContainerScreen<?> screen = (ContainerScreen<?>) aiHelper.getMinecraft().currentScreen;
		if (screen == null) {
			taskOperations.desync(new StringTaskError("Expected container to be open"));
			return;
		}
		if (delay > 0) {
			delay--;
		} else if (!clicks.isEmpty()) {
			clicks.removeFirst().click(aiHelper);
			delay = 1;
		} else {
			int toStack = getToStack(aiHelper);
			if (toStack < 0
					|| toStack >= screen.getContainer().inventorySlots
							.size()) {
				LOGGER.error("Invalid stack to put to : {}", toStack);
				taskOperations.desync(new StringTaskError("Attempted to put item into an invalid stack."));
				return;
			}

			Slot to = screen.getContainer().getSlot(toStack);
			
			int amount = getMissingAmount(aiHelper, getSlotContentCount(to));
			if (amount <= 0) {
				moveDone = true;
				return;
			}

			// Only compute from stack after amount has been checked => some tasks don't specify a from stack when amount is 0
			int fromStack = getFromStack(aiHelper);
			if (fromStack < 0
					|| fromStack >= screen.getContainer().inventorySlots
					.size()) {
				LOGGER.error("Invalid stack to take from: {}", fromStack);
				taskOperations.desync(new StringTaskError("Attempted to take an item from an invalid stack."));
				return;
			}

			Slot from = screen.getContainer().getSlot(fromStack);

			if (getSlotContentCount(from) <= 0) {
				taskOperations.desync(new StringTaskError("Nothing in source slot."));
				LOGGER.error(MARKER_MOVE, "Attempted to move from slot "
						+ fromStack + " but it was empty (" + from.slotNumber
						+ ", " + from.getStack() + ")");
				return;
			}
			
			LOGGER.debug(MARKER_MOVE, "Move " + amount + " from " + fromStack
					+ " to " + toStack);

			int limit = Math.min(to.getSlotStackLimit(), from.getStack().getMaxStackSize());
			int missing = Math.min(amount, limit - getSlotContentCount(to));

			LOGGER.debug("Still missing items: " + missing);
			if (getSlotContentCount(from) <= missing && getSlotContentCount(from) > 0) {
				LOGGER.debug(MARKER_MOVE, "moving all items (left klick to take, left click to put)");
				missing -= moveAll(aiHelper, from, to);
			} else if (getSlotContentCount(from) - getSlotContentCount(from) / 2 <= missing
					&& getSlotContentCount(from) > 0) {
				LOGGER.debug(MARKER_MOVE, "moving half of the items (right klick to take, left click to put)");
				missing -= moveHalf(aiHelper, from, to);
			} else if (missing > 0 && getSlotContentCount(from) > 0) {
				LOGGER.debug(MARKER_MOVE, "moving {} items (left klick to take, {}x right click to put, left click to put back)", missing, missing);
				missing -= moveStackPart(aiHelper, from, to, missing);
			} else if (missing > 0) {
				missingItems(missing);
			} else {
				moveDone = true;
			}
			delay = DELAY;
		}
	}

	private int moveAll(AIHelper aiHelper, Slot from, Slot to) {
		int oldCount = getSlotContentCount(to);

		addClick(new LeftClickAction(from.slotNumber));
		addClick(new LeftClickAction(to.slotNumber));
		
		return oldCount;
	}

	private int moveHalf(AIHelper aiHelper, Slot from, Slot to) {
		int oldCount = getSlotContentCount(to);
		
		addClick(new RightClickAction(from.slotNumber));
		addClick(new LeftClickAction(to.slotNumber));
		
		return oldCount / 2;
	}

	private int moveStackPart(AIHelper aiHelper, Slot from, Slot to, int count) {
		int oldCount = getSlotContentCount(to);

		addClick(new LeftClickAction(from.slotNumber));
		for (int i = 0; i < count; i++) {
			addClick(new RightClickAction(to.slotNumber));
		}
		addClick(new LeftClickAction(from.slotNumber));
		return getSlotContentCount(to) - oldCount;
	}

	protected int getSlotContentCount(Slot slot) {
		return slot.getHasStack() ? slot.getStack().getCount() : 0;
	}

	protected static int convertPlayerInventorySlot(int inventorySlot) {
		// Offset: 10 blocks.
		if (inventorySlot < 9) {
			return inventorySlot + 9 * 3;
		} else {
			return inventorySlot - 9;
		}
	}
	
	protected void addClick(AbstractClickAction action) {
		clicks.add(action);
	}
	
	/**
	 * Click on an item slot.
	 */
	private static abstract class AbstractClickAction {
		protected final int slotNumber;

		public AbstractClickAction(int slotNumber) {
			super();
			this.slotNumber = slotNumber;
		}

		protected void click(AIHelper aiHelper) {
			int clickKey = getClickKey();
			ClickType clickType = getClickType();
			LOGGER.trace(MARKER_MOVE, "Click on " + slotNumber + " using " + clickKey + "," + clickType);
			final ContainerScreen<?> screen = (ContainerScreen<?>) aiHelper.getMinecraft().currentScreen;
			aiHelper.getMinecraft().playerController.windowClick(
					screen.getContainer().windowId, slotNumber, clickKey, clickType,
					aiHelper.getMinecraft().player);
		}

		abstract ClickType getClickType();

		abstract int getClickKey();
	}
	
	private static class RightClickAction extends AbstractClickAction {
		public RightClickAction(int slotNumber) {
			super(slotNumber);
		}

		@Override
		ClickType getClickType() {
			return ClickType.PICKUP;
		}

		@Override
		int getClickKey() {
			return 1;
		}

		@Override
		public String toString() {
			return "RightClickAction [slotNumber=" + slotNumber + "]";
		}
	}
	
	private static class LeftClickAction extends AbstractClickAction {
		public LeftClickAction(int slotNumber) {
			super(slotNumber);
		}

		@Override
		ClickType getClickType() {
			return ClickType.PICKUP;
		}

		@Override
		int getClickKey() {
			return 0;
		}

		@Override
		public String toString() {
			return "LeftClickAction [slotNumber=" + slotNumber + "]";
		}
	}
}
