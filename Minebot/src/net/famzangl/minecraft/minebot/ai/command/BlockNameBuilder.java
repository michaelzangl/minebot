package net.famzangl.minecraft.minebot.ai.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.minecraft.block.Block;

public class BlockNameBuilder extends ParameterBuilder {

	private final static class BlockArgumentDefinition extends ArgumentDefinition {
		public BlockArgumentDefinition(String description) {
			super("Block", description);
		}

		@Override
		public boolean couldEvaluateAgainst(String string) {
			return Block.blockRegistry.getObject(string) != null;
		}

		@Override
		public void getTabCompleteOptions(String currentStart,
				Collection<String> addTo) {
			super.getTabCompleteOptions(currentStart, addTo);
			Set<String> keys = Block.blockRegistry.getKeys();
			for (String k : keys) {
				if (k.startsWith(MINECRAFT_PREFIX)) {
					String subKey = k.substring(MINECRAFT_PREFIX.length());
					addKey(currentStart, addTo, subKey);
				} else {
					addKey(currentStart, addTo, k);
				} 
			}
		}

		private void addKey(String currentStart,
				Collection<String> addTo, String subKey) {
			if (subKey.startsWith(currentStart)) {
				addTo.add(subKey);
			}
		}
	}

	private static final String MINECRAFT_PREFIX = "minecraft:";

	public BlockNameBuilder(AICommandParameter annot) {
		super(annot);
	}

	@Override
	public void addArguments(ArrayList<ArgumentDefinition> list) {
		list.add(new BlockArgumentDefinition(annot.description()));
	}

	@Override
	public Object getParameter(AIHelper helper, String[] arguments) {
		return Block.blockRegistry.getObject(arguments[0]);
	}

}
