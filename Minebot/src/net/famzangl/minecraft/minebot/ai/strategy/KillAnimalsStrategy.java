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

import java.util.HashSet;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.ItemFilter;
import net.famzangl.minecraft.minebot.ai.animals.AnimalyType;
import net.famzangl.minecraft.minebot.ai.selectors.AndSelector;
import net.famzangl.minecraft.minebot.ai.selectors.ColorSelector;
import net.famzangl.minecraft.minebot.ai.selectors.ItemSelector;
import net.famzangl.minecraft.minebot.ai.selectors.NotSelector;
import net.famzangl.minecraft.minebot.ai.selectors.OrSelector;
import net.famzangl.minecraft.minebot.ai.selectors.OwnTameableSelector;
import net.famzangl.minecraft.minebot.ai.selectors.XPOrbSelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

import com.google.common.base.Predicate;

/**
 * Kills all animals in range by mving towards them and hitting them.
 * 
 * @author michael
 * 
 */
public class KillAnimalsStrategy extends FaceInteractStrategy {

	private final class KillableSelector implements Predicate<Entity> {
		@Override
		public boolean apply(Entity e) {
			if (!type.hasAnimalClass(e)) {
				return false;
			}

			return ((EntityAnimal) e).getGrowingAge() >= 0
					&& ((EntityAnimal) e).getHealth() > 0;
		}
	}

	private final int maxKills;
	private final AnimalyType type;
	private final HashSet<Entity> hitEntities = new HashSet<Entity>();
	private final EnumDyeColor color;
	private int cooldown;
	private int lastKills;

	public KillAnimalsStrategy() {
		this(-1, AnimalyType.ANY, null);
	}

	public KillAnimalsStrategy(int maxKills, AnimalyType type, EnumDyeColor color) {
		this.maxKills = maxKills;
		this.type = type;
		this.color = color;
	}

	@Override
	protected Predicate<Entity> entitiesToInteract(AIHelper helper) {
		if (maxKillsReached()) {
			return new Predicate<Entity>() {
				@Override
				public boolean apply(Entity var1) {
					return false;
				}
			};
		} else {
			Predicate<Entity> s = new KillableSelector();
			if (color != null) {
				s = new AndSelector(new ColorSelector(color), s);
			}
			return new AndSelector(s, new NotSelector(new OwnTameableSelector(
					helper.getMinecraft().thePlayer)));
		}
	}

	@Override
	protected Predicate<Entity> entitiesToFace(AIHelper helper) {
		return new OrSelector(super.entitiesToFace(helper), new ItemSelector(),
				new XPOrbSelector());
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
					return itemStack != null && itemStack.getItem() instanceof ItemSword;
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
			if (e instanceof EntityAnimal
					&& ((EntityAnimal) e).getHealth() <= 0) {
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
