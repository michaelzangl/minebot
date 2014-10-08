package net.famzangl.minecraft.minebot.build.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Map.Entry;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.HumanReadableItemFilter;
import net.famzangl.minecraft.minebot.ai.ItemFilter;
import net.famzangl.minecraft.minebot.ai.command.AIChatController;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.ai.command.AICommandInvocation;
import net.famzangl.minecraft.minebot.ai.command.AICommandParameter;
import net.famzangl.minecraft.minebot.ai.command.ParameterType;
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy;
import net.famzangl.minecraft.minebot.build.blockbuild.BuildTask;

import com.google.common.base.Function;

@AICommand(helpText = "Count all stuff required for building.", name = "minebuild")
public class CommandCount {

	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "count", description = "") String nameArg2) {

		final Hashtable<ItemFilter, Integer> counts = new Hashtable<ItemFilter, Integer>();
		for (final BuildTask s : helper.buildManager.getScheduled()) {
			final ItemFilter required = s.getRequiredItem();
			final Integer count = counts.get(required);
			counts.put(required, count == null ? 1 : count + 1);
		}

		final ArrayList<Entry<ItemFilter, Integer>> list = new ArrayList<Entry<ItemFilter, Integer>>(
				counts.entrySet());
		Collections.sort(list, new Comparator<Entry<ItemFilter, Integer>>() {
			@Override
			public int compare(Entry<ItemFilter, Integer> o1,
					Entry<ItemFilter, Integer> o2) {
				return -o1.getValue().compareTo(o2.getValue());
			}
		});

		AIChatController.addToChatPaged("Required items", 1, list,
				new Function<Entry<ItemFilter, Integer>, String>() {
					@Override
					public String apply(Entry<ItemFilter, Integer> item) {
						final ItemFilter filter = item.getKey();
						final String key = niceFilterName(filter);
						return key + ": " + item.getValue();
					}

				});
		return null;
	}

	public static String niceFilterName(ItemFilter filter) {
		return filter instanceof HumanReadableItemFilter ? ((HumanReadableItemFilter) filter)
				.getDescription() : filter.toString();
	}
}
