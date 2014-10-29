package net.famzangl.minecraft.minebot.ai.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.command.AICommandParameter.AnyBlockFilter;
import net.famzangl.minecraft.minebot.ai.command.AICommandParameter.BlockFilter;
import net.minecraft.block.Block;

public class BlockNameBuilder extends ParameterBuilder {

	private final static class BlockArgumentDefinition extends
			ArgumentDefinition {

		private final BlockFilter blockFilter;

		public BlockArgumentDefinition(String description,
				Class<? extends BlockFilter> blockFilterClass) {
			super("Block", description);
			BlockFilter blockFilter;
			try {
				blockFilter = blockFilterClass.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
				blockFilter = new AnyBlockFilter();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				blockFilter = new AnyBlockFilter();
			}
			this.blockFilter = blockFilter;
		}

		@Override
		public boolean couldEvaluateAgainst(String string) {
			final Object block = Block.blockRegistry.getObject(string);
			return block != null && blockFilter.matches((Block) block);
		}

		@Override
		public void getTabCompleteOptions(String currentStart,
				Collection<String> addTo) {
			super.getTabCompleteOptions(currentStart, addTo);
			@SuppressWarnings("unchecked")
			final Set<String> keys = Block.blockRegistry.getKeys();
			for (final String k : keys) {
				final Object block = Block.blockRegistry.getObject(k);
				if (blockFilter.matches((Block) block)) {
					if (k.startsWith(MINECRAFT_PREFIX)) {
						final String subKey = k.substring(MINECRAFT_PREFIX
								.length());
						addKey(currentStart, addTo, subKey);
					} else {
						addKey(currentStart, addTo, k);
					}
				}
			}
		}

		private void addKey(String currentStart, Collection<String> addTo,
				String subKey) {
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
		list.add(new BlockArgumentDefinition(annot.description(), annot
				.blockFilter()));
	}

	@Override
	public Object getParameter(AIHelper helper, String[] arguments) {
		final Object block = Block.blockRegistry.getObject(arguments[0]);
		if (block == null) {
			throw new CommandEvaluationException("Block " + arguments[0]
					+ " is unknown");
		}
		return block;
	}

}
