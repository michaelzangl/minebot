package net.famzangl.minecraft.minebot.ai.strategy;

import java.util.ArrayList;

import net.famzangl.minecraft.minebot.ai.AIHelper;

public class GetItemsStrategy extends AIStrategy {
	private final ArrayList<ItemCount> list;

	public static class ItemCount {
		private int acquired;
		private int requested;
		private boolean alsoCountInventory;
		
		public int getAcquired(AIHelper helper) {
			if (alsoCountInventory) {
				throw new UnsupportedOperationException("Unimplemented");
			} else {
				return acquired;
			}
		}
		
		public int getMissing(AIHelper helper) {
			return Math.max(0, requested -  getAcquired(helper));
		}
	}
	
	public GetItemsStrategy(ArrayList<ItemCount> list) {
		this.list = list;
	}

	@Override
	protected TickResult onGameTick(AIHelper helper) {
		return null;
	}
	
	

}
