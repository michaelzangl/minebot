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
package net.famzangl.minecraft.minebot.ai.command;

import java.util.ArrayList;
import java.util.Collection;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.command.AICommandParameter.AnyBlockFilter;
import net.famzangl.minecraft.minebot.ai.command.AICommandParameter.BlockFilter;
import net.famzangl.minecraft.minebot.ai.command.BlockWithDataOrDontcare.IllegalBlockNameException;
import net.minecraft.util.ResourceLocation;

public class BlockNameBuilder extends ParameterBuilder {

	public static String toString(ResourceLocation l) {
		return l.getResourceDomain() + ":" + l.getResourcePath();
	}

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
			try {
				BlockWithDataOrDontcare block = BlockWithDataOrDontcare.getFromString(string);
				return blockFilter.matches(block);
			} catch (IllegalBlockNameException e) {
				return false;
			}
		}

		@Override
		public void getTabCompleteOptions(String currentStart,
				Collection<String> addTo) {
			super.getTabCompleteOptions(currentStart, addTo);
			
			for (String s : BlockWithDataOrDontcare.getAllStrings()) {
				String noPrefix = s.replaceFirst("^minecraft:", "");
				if (s.startsWith(currentStart) || noPrefix.startsWith(currentStart)) {
					addTo.add(noPrefix);
				}
			}
			
//			@SuppressWarnings("unchecked")
			// BIG TODO: Get a list of all blocks for tab complete.
//			final Set<ResourceLocation> keys = Block.blockRegistry.getKeys();
//			for (final ResourceLocation k : keys) {
//				final Object block = Block.blockRegistry.getObject(k);
//				if (blockFilter.matches((Block) block)) {
//					if (k.getResourceDomain().equals(MINECRAFT_PREFIX)) {
//						final String subKey = k.getResourcePath();
//						addKey(currentStart, addTo, subKey);
//					} else {
//						addKey(currentStart, addTo,
//								BlockNameBuilder.toString(k));
//					}
//				}
//			}
		}

		private void addKey(String currentStart, Collection<String> addTo,
				String subKey) {
			if (subKey.startsWith(currentStart)) {
				addTo.add(subKey);
			}
		}
	}

	private static final String MINECRAFT_PREFIX = "minecraft";

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
		return BlockWithDataOrDontcare.getFromString(arguments[0]);
	}

	@Override
	protected Class<?> getRequiredParameterClass() {
		return BlockWithDataOrDontcare.class;
	}

}
