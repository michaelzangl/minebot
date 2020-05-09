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
package net.famzangl.minecraft.minebot.ai.strategy;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.ItemFilter;
import net.famzangl.minecraft.minebot.ai.animals.AnimalyType;
import net.famzangl.minecraft.minebot.ai.selectors.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;

import java.util.HashSet;
import java.util.function.Predicate;

/**
 * Kills all animals in range by mving towards them and hitting them.
 * 
 * @author michael
 * 
 */
public class KillAnimalsStrategy extends FaceInteractStrategy {

	private final class KillableSelector implements Predicate<Entity> {
		@Override
		public boolean test(Entity entity) {
			if (!type.hasAnimalClass(entity)) {
				return false;
			}

			return ((AnimalEntity) entity).getGrowingAge() >= 0
					&& ((AnimalEntity) entity).getHealth() > 0;
		}
	}

	private final int maxKills;
	private final AnimalyType type;
	private final HashSet<Entity> hitEntities = new HashSet<Entity>();
	private final DyeColor color;
	private int cooldown;
	private int lastKills;

	public KillAnimalsStrategy() {
		this(-1, AnimalyType.ANY, null);
	}

	public KillAnimalsStrategy(int maxKills, AnimalyType type, DyeColor color) {
		this.maxKills = maxKills;
		this.type = type;
		this.color = color;
	}

	@Override
	protected Predicate<Entity> entitiesToInteract(AIHelper helper) {
		if (maxKillsReached()) {
			return __ -> false;
		} else {
			Predicate<Entity> selector = new KillableSelector();
			if (color != null) {
				selector = new ColorSelector(color).and(selector);
			}
			return selector.and(new OwnTameableSelector(
					helper.getMinecraft().player).negate());
		}
	}

	@Override
	protected Predicate<Entity> entitiesToFace(AIHelper helper) {
		return super.entitiesToFace(helper).or(new ItemSelector()).or(new XPOrbSelector());
	}

	@Override
	protected boolean doInteractWithCurrent(Entity entityHit, AIHelper helper) {
		if (cooldown > 0) {
			cooldown--;
			return false;
		} else {
			helper.selectCurrentItem(new ItemFilter() {
				@Override
				public boolean matches(ItemStack itemStack) {
					return itemStack != null && itemStack.getItem() instanceof SwordItem;
				}
			});
			boolean interacted = super.doInteractWithCurrent(entityHit, helper);
			if (interacted) {
				cooldown = 5;
			}
			return interacted;
		}
	}

	@Override
	protected void doInteract(Entity entityHit, AIHelper helper) {
		hitEntities.add(entityHit);
		helper.overrideAttack();
	}

	public boolean maxKillsReached() {
		if (maxKills >= 0) {
			lastKills = countKills();
			if (lastKills >= maxKills) {
				return true;
			}
		}
		return false;
	}

	private int countKills() {
		int kills = 0;
		for (Entity e : hitEntities) {
			if (e instanceof AnimalEntity
					&& ((AnimalEntity) e).getHealth() <= 0) {
				kills++;
			}
		}
		return kills;
	}

	@Override
	public String getDescription(AIHelper helper) {
		if (maxKills < 0) {
			return "Killing";
		} else {
			return "Killing " + lastKills + "/" + maxKills;
		}
	}

	@Override
	public boolean hasFailed() {
		return maxKills >= 0 && !maxKillsReached();
	}
}
