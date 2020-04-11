package net.famzangl.minecraft.minebot.ai.commands;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.command.AIChatController;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.ai.command.AICommandInvocation;
import net.famzangl.minecraft.minebot.ai.command.AICommandParameter;
import net.famzangl.minecraft.minebot.ai.command.ParameterType;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSets;
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.RunOnceStrategy;
import net.famzangl.minecraft.minebot.settings.MinebotSettings;
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.Blocks;
import net.minecraft.tileentity.SignTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@AICommand(helpText = "Dump all signs to a text file.", name = "minebot")
public class CommandDumpSigns {
	private static final BlockSet SIGNS = BlockSet.builder().add(BlockSets.SIGN).add(BlockSets.WALL_SIGN).build();
	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "dump", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "signs", description = "") String nameArg2,
			@AICommandParameter(type = ParameterType.NUMBER, description = "distance", optional = true) Integer distanceArg) {
		final int distance = distanceArg == null ? 100 : distanceArg;
		return new RunOnceStrategy() {
			@Override
			protected void singleRun(AIHelper helper) {
				final File dir = MinebotSettings.getDataDirFile("dumps");
				dir.mkdirs();
				final DateFormat df = new SimpleDateFormat(
						"yyyy-MM-dd-HH-mm-ss");
				final String date = df.format(Calendar.getInstance()
						.getTime());

				final File file = new File(dir, date + ".signdump.txt");
				dumpSignsTo(helper, file, distance);
			}

			private void dumpSignsTo(AIHelper helper, File file, int distance) {
				PrintStream out;
				try {
					out = new PrintStream(file);
					int signs = 0;

					for (BlockPos pos : SIGNS.findBlocks(helper.getWorld(), helper.getPlayerPosition(), distance)) {
						dumpAtPos(helper, out, pos);
						signs++;
					}
					AIChatController.addChatLine("Dumped " + signs + " signs (" + distance + " blocks radius).");
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}

			private void dumpAtPos(AIHelper helper, PrintStream out, BlockPos p) {
				AbstractSignBlock block = (AbstractSignBlock) helper.getBlock(p);
				TileEntity tileentity = helper.getMinecraft().world
						.getTileEntity(p);
				if (tileentity instanceof SignTileEntity) {
					out.print(p.getX());
					out.print("\t");
					out.print(p.getY());
					out.print("\t");
					out.print(p.getZ());
					ITextComponent[] texts = ((SignTileEntity) tileentity).signText;
					for (ITextComponent t : texts) {
						out.append("\t");
						out.print(t == null ? "" : t.getString());
					}
					out.println();
				} else {
					AIChatController.addChatLine("At " + p + ": Not a sign entity.");
				}
			}
		};
	}

}
