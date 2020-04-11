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
package net.famzangl.minecraft.minebot.build.blockbuild;

import java.util.Arrays;

import net.famzangl.minecraft.minebot.ai.ClassItemFilter;
import net.famzangl.minecraft.minebot.ai.ItemFilter;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.famzangl.minecraft.minebot.ai.task.place.SignPlaceOnGroundTask;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemSign;
import net.minecraft.util.math.BlockPos;

public class StandingSignBuildTask extends BuildFlatOnGroundTask {

	public static final BlockSet BLOCKS = new BlockSet(Blocks.STANDING_SIGN);
	
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

	public StandingSignBuildTask(BlockPos forPosition, SignDirection direction, String[] texts) {
		super(forPosition);
		this.direction = direction;
		this.texts = texts;
	}

	@Override
	public ItemFilter getRequiredItem() {
		return new ClassItemFilter(ItemSign.class);
	}

	@Override
	public AITask getPlaceBlockTask(BlockPos relativeFromPos) {
		return new SignPlaceOnGroundTask(forPosition, direction.ordinal(), texts);
	}

	@Override
	public BuildTask withPositionAndRotation(BlockPos add, int rotateSteps,
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
