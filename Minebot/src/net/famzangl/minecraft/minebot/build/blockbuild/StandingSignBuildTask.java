package net.famzangl.minecraft.minebot.build.blockbuild;

import java.util.Arrays;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.BlockWhitelist;
import net.famzangl.minecraft.minebot.ai.ClassItemFilter;
import net.famzangl.minecraft.minebot.ai.ItemFilter;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.famzangl.minecraft.minebot.ai.task.place.SignPlaceOnGroundTask;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemSign;

public class StandingSignBuildTask extends BuildFlatOnGroundTask {

	public static final BlockWhitelist BLOCKS = new BlockWhitelist(Blocks.standing_sign);
	
	private final SignDirection direction;

	private final String[] texts;

	public enum SignDirection {
		SOUTH,
		SOUTHSOUTHWEST,
		SOUTHWEST,
		WESTSOUTHWEST,
		WEST,
		WESTNORTHWEST,
		NORTHWEST,
		NORTHNORTHWEST,
		NORTH,
		NORTHNORTHEAST,
		NORTHEAST,
		EASTNORTHEAST,
		EAST,
		EASTSOUTHEAST,
		SOUTHEAST,
		SOUTHSOUTHEAST
	}

	public StandingSignBuildTask(Pos forPosition, SignDirection direction, String[] texts) {
		super(forPosition);
		this.direction = direction;
		this.texts = texts;
	}

	@Override
	public ItemFilter getRequiredItem() {
		return new ClassItemFilter(ItemSign.class);
	}

	@Override
	public AITask getPlaceBlockTask(Pos relativeFromPos) {
		return new SignPlaceOnGroundTask(forPosition.x, forPosition.y,
				forPosition.z, direction.ordinal(), texts);
	}

	@Override
	public BuildTask withPositionAndRotation(Pos add, int rotateSteps,
			MirrorDirection mirror) {
		// TODO: Rotate and mirror
		return new StandingSignBuildTask(forPosition, direction, texts);
	}

	@Override
	public String toString() {
		return "StandingSignBuildTask [direction=" + direction + ", texts="
				+ Arrays.toString(texts) + "]";
	}

}
