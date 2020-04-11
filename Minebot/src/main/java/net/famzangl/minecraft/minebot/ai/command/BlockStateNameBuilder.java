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

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.command.AICommandParameter.BlockFilter;
import net.minecraft.block.BlockState;
import net.minecraft.command.arguments.BlockStateParser;

import java.util.ArrayList;
import java.util.Collection;

public class BlockStateNameBuilder extends ParameterBuilder {


	private static final String DEFAULT_STATE = "default";

	public BlockStateNameBuilder(AICommandParameter annot) {
		super(annot);
	}

	@Override
	public void addArguments(ArrayList<ArgumentDefinition> list) {
		Class<? extends BlockFilter> blockFilterClass = annot.blockFilter();
		list.add(new ArgumentDefinition("Block", annot.description()) {
			private final BlockFilter blockFilter;
			{
				BlockFilter blockFilter;
				try {
					blockFilter = blockFilterClass.newInstance();
				} catch (InstantiationException e) {
					e.printStackTrace();
					blockFilter = new AICommandParameter.AnyBlockFilter();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
					blockFilter = new AICommandParameter.AnyBlockFilter();
				}
				this.blockFilter = blockFilter;
			}
			
			@Override
			public boolean couldEvaluateAgainst(String string) {
				try {
					BlockState blockState = parseArgument(string);
					return blockFilter.matches(blockState);
				} catch (CommandSyntaxException e) {
					return false;
				}
			}

			@Override
			public void getTabCompleteOptions(String currentStart,
					Collection<String> addTo) {
				// TODO
				/*Block.REGISTRY.getKeys()
					.stream()
					.filter(name -> blockFilter.matches(new BlockWithDontcare(Block.REGISTRY.getObject(name))))
					.map(Object::toString)
					.filter(name -> name.startsWith(currentStart))
					.forEach(addTo::add);*/
			}
		});
		/* TODO
		list.add(new ArgumentDefinition("Meta", "Meta value for that block, '" + DEFAULT_STATE + "' for default") {

			public boolean couldEvaluateAgainst(List<String> previousArguments, String string) {
				if (DEFAULT_STATE.equals(string)) {
					return true;
				}
				try {
					Block block = CommandBase.getBlockByText(null, previousArguments.get(previousArguments.size() - 1));
					CommandFill.convertArgToBlockState(block, string);
					return true;
				} catch (NumberInvalidException | InvalidBlockStateException e) {
					return false;
				}
				
			};
			
			@Override
			public void getTabCompleteOptions(List<String> previousArguments, String currentStart,
					Collection<String> addTo) {
				if (DEFAULT_STATE.startsWith(currentStart)) {
					addTo.add(DEFAULT_STATE);
				}

				try {
					Block block = CommandBase.getBlockByText(null, previousArguments.get(previousArguments.size() - 1));
				
					Matcher inValuePart = Pattern.compile("^(<start>.*,(<key>[^,=]*)=)(<value>[^=,]*)$").matcher(currentStart);
					if (inValuePart.matches()) {
						String key = inValuePart.group("key");
						String valueStart = inValuePart.group("value");

						IProperty<?> property = block.getBlockState().getProperty(key);
						if (property != null) {
							propertyNames(property)
								.filter(name -> name.startsWith(valueStart))
								.forEach(name -> addTo.add(inValuePart.group("start") + name));
						}
						
					} else {
						Matcher inKeyPart = Pattern.compile("^(<start>.*,|)(<key>[^,=]*)$").matcher(currentStart);
						if (inKeyPart.matches()) {
							String keyStart = inValuePart.group("key");
							
							block.getBlockState().getProperties().stream()
								.map(p -> p.getName())
								.filter(name -> name.startsWith(keyStart))
								.forEach(name -> addTo.add(inKeyPart.group("start") + name));
						}
					}
				} catch (NumberInvalidException e) {
					//ignore
				}
			}
			
			private <T extends Comparable<T>> Stream<String> propertyNames(IProperty<T> property) {
					return property.getAllowedValues()
						.stream()
						.map(value -> property.getName(value));
			}
		});
		*/
	}

	private BlockState parseArgument(String string) throws CommandSyntaxException {
		BlockStateParser state = new BlockStateParser(new StringReader(string), false).parse(false);
		return state.getState();
	}

	@Override
	public Object getParameter(AIHelper helper, String[] arguments) {
		try {
			return parseArgument(arguments[0]);
		} catch (CommandSyntaxException e) {
			// Should not happen - we have an arg def for this
			throw new RuntimeException(e);
		}
	}

}
