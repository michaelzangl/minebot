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
import java.util.Set;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.task.inventory.ItemWithSubtype;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

/**
 * Builds the name for an item.
 * <p>
 * The following names are recognized:
 * <ul>
 * <li>wool : Any dirt block.
 * <li>35 : Any dirt block.
 * <li>wool:red : Red wool block
 * <li>wool:14 : Red wool block
 * <li>35:14 : Red wool block
 * </ul>
 * 
 * @author michael
 *
 */
public class ItemNameBuilder extends ParameterBuilder {

	private final static class ItemArgumentDefinition extends
			ArgumentDefinition {

		public ItemArgumentDefinition(String description) {
			super("Item", description);
		}

		@Override
		public boolean couldEvaluateAgainst(String string) {
			final ItemWithSubtype item = parse(string);
			return item != null;
		}

		@Override
		public void getTabCompleteOptions(String currentStart,
				Collection<String> addTo) {
			super.getTabCompleteOptions(currentStart, addTo);
			@SuppressWarnings("unchecked")
			final Set<ResourceLocation> keys = Item.REGISTRY.getKeys();
			for (final ResourceLocation k : keys) {
				final Object block = Item.REGISTRY.getObject(k);
				if (k.getResourceDomain().equals(MINECRAFT_PREFIX)) {
					final String subKey = k.getResourcePath();
					addKey(currentStart, addTo, subKey);
				} else {
					addKey(currentStart, addTo, BlockNameBuilder.toString(k));
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

	private static final String MINECRAFT_PREFIX = "minecraft";

	public ItemNameBuilder(AICommandParameter annot) {
		super(annot);
	}

	@Override
	public void addArguments(ArrayList<ArgumentDefinition> list) {
		list.add(new ItemArgumentDefinition(annot.description()));
	}

	@Override
	public Object getParameter(AIHelper helper, String[] arguments) {
		final Object block = parse(arguments[0]);
		if (block == null) {
			throw new CommandEvaluationException("Item " + arguments[0]
					+ " is unknown");
		}
		return block;
	}

	public static ItemWithSubtype parse(String string) {
		String[] parts = string.split(":");

		ItemWithSubtype item;
		// if (parts[0].matches("\\d{1,4}")) {
		// item = new ItemWithSubtype(Integer.parseInt(parts[0]), 0);
		// } else {
		item = ItemWithSubtype.fromTypeName(parts[0]);
		if (item == null) {
			if (parts.length == 2) {
				parts = new String[] { parts[0] + ":" + parts[1] };
			} else if (parts.length == 3) {
				parts = new String[] { parts[0] + ":" + parts[1], parts[2] };
			} else {
				return null;
			}
			item = ItemWithSubtype.fromTypeName(parts[0]);
			if (item == null) {
				return null;
			}
		}
		// }

		if (parts.length > 2) {
			return null;
		} else if (parts.length > 1) {
			// Subtype
			return item.withSubtype(parts[1]);
		} else {
			return item;
		}
	}

	@Override
	protected Class<?> getRequiredParameterClass() {
		return ItemWithSubtype.class;
	}
}
