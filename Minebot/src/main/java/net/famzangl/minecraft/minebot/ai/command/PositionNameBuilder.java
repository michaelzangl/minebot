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
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

public class PositionNameBuilder extends ParameterBuilder {

	private final static class PositionArgumentDefinition extends
			ArgumentDefinition {
		public PositionArgumentDefinition(String description, String dir) {
			super("pos." + dir, description);
		}

		@Override
		public boolean couldEvaluateAgainst(String string) {
			return true;
		}
	}

	public PositionNameBuilder(AICommandParameter annot) {
		super(annot);
	}

	@Override
	public void addArguments(ArrayList<ArgumentDefinition> list) {
		list.add(new PositionArgumentDefinition(annot.description(), "x"));
		list.add(new PositionArgumentDefinition(annot.description(), "y"));
		list.add(new PositionArgumentDefinition(annot.description(), "z"));
	}

	@Override
	public Object getParameter(AIHelper helper, String[] arguments) {
		try {
			return new BlockPosArgument().parse(new StringReader(String.join(" ", arguments))).getBlockPos(
					helper.getMinecraft().player.getCommandSource()
			);
		} catch (final CommandSyntaxException e) {
			throw new CommandEvaluationException("Number format not supported.");
		}
	}
	
	@Override
	protected Class<?> getRequiredParameterClass() {
		return BlockPos.class;
	}
}
