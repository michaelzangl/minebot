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
package net.famzangl.minecraft.minebot.ai.task.place;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.BlockItemFilter;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.famzangl.minecraft.minebot.build.block.WoodType;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;

/**
 * Replant by placing tree saplings on the floor.
 * 
 * @author michael
 *
 */
public class PlantSaplingTask extends PlaceBlockAtFloorTask {

	private static final class SaplingFilter extends BlockItemFilter {
		private WoodType type;

		private SaplingFilter(WoodType type) {
			super(Blocks.sapling);
			this.type = type;
		}
		
		@Override
		public boolean matches(ItemStack itemStack) {
			return super.matches(itemStack) && isOfWood(itemStack);
		}

		private boolean isOfWood(ItemStack itemStack) {
			return type == null || itemStack.getItemDamage() == type.plankType.getMetadata();
		}
	}

	private final static BlockSet PLANTABLE = new BlockSet(
			Blocks.dirt, Blocks.grass);

	public PlantSaplingTask(BlockPos pos, WoodType type) {
		super(pos, new SaplingFilter(type));
	}

	@Override
	public boolean isFinished(AIHelper h) {
		if (!PLANTABLE.contains(h.getBlock(pos.add(0, -1, 0)))) {
			return true;
		}

		return super.isFinished(h);
	}

	@Override
	public String toString() {
		return "PlantSaplingTask []";
	}
}
