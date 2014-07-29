package net.famzangl.minecraft.minebot.ai.command;

import java.util.ArrayList;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.minecraft.command.CommandBase;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;

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

		ChunkCoordinates ppos = helper.getMinecraft().thePlayer
				.getPlayerCoordinates();
		int i = ppos.posX;
		int j = ppos.posY - 2;
		int k = ppos.posZ;
		i = MathHelper.floor_double(CommandBase.func_110666_a(
				helper.getMinecraft().thePlayer, i, arguments[0]));
		j = MathHelper.floor_double(CommandBase.func_110666_a(
				helper.getMinecraft().thePlayer, j, arguments[1]));
		k = MathHelper.floor_double(CommandBase.func_110666_a(
				helper.getMinecraft().thePlayer, k, arguments[2]));
		return new Pos(i, j, k);

	}

}
