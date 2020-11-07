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

import javax.annotation.Nullable;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.ItemFilter;
import net.famzangl.minecraft.minebot.ai.command.AIChatController;
import net.famzangl.minecraft.minebot.ai.selectors.ColorSelector;
import net.famzangl.minecraft.minebot.ai.selectors.XPOrbSelector;
import net.famzangl.minecraft.minebot.ai.task.FaceAndInteractTask;
import net.famzangl.minecraft.minebot.ai.task.TaskOperations;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.passive.horse.DonkeyEntity;
import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraft.entity.passive.horse.LlamaEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FeedAnimalsStrategy extends TaskStrategy {
	private static final Logger LOGGER = LogManager.getLogger(FeedAnimalsStrategy.class);

	private static final int DISTANCE = 20;
	private final DyeColor color;
	private final Set<FeedableType> feedableTypes;
	private final Set<Entity> alreadyInteracted = new HashSet<>();
	private boolean endMessageSent;

	public FeedAnimalsStrategy(@Nullable DyeColor color, @Nullable FeedableType feedableType) {
		this.color = color;
		this.feedableTypes = feedableType == null ? EnumSet.allOf(FeedableType.class) : EnumSet.of(feedableType);
	}

	@Override
	public void searchTasks(AIHelper helper) {
		feedWithFood(helper);
	}

	private void feedWithFood(AIHelper helper) {
		Predicate<Entity> selector = generateEntitySelector((type, entity) -> type.canFeedAnimal(entity, helper));

		final Predicate<Entity> collect = new XPOrbSelector();

		final Entity found = helper.getClosestEntity(DISTANCE, selector.or(collect));

		if (found != null) {
			addTask(new FaceAndInteractTask(found, selector) {
				@Override
				public void runTick(AIHelper aiHelper, TaskOperations taskOperations) {
					// Select an item that none of the animals follow.
					aiHelper.selectCurrentItem(itemStack -> itemStack == null
							|| itemStack.isEmpty()
							|| Stream.of(FeedableType.values()).noneMatch(val -> val.items.contains(itemStack.getItem())));
					super.runTick(aiHelper, taskOperations);
				}

				@Override
				protected void doInteractWithCurrent(AIHelper aiHelper) {
					RayTraceResult rayTrace = aiHelper.getObjectMouseOver();
					if (rayTrace instanceof EntityRayTraceResult) {
						Entity over = ((EntityRayTraceResult) rayTrace).getEntity();
						Optional<FeedableType> type = feedableTypes.stream().filter(t -> t.canFeedAnimal(over, helper)).findFirst();
						if (type.isPresent() && aiHelper.selectCurrentItem(type.get().getItemFilter())) {
							super.doInteractWithCurrent(aiHelper);
							alreadyInteracted.add(over);
						} else if (found == over) {
							interacted = true;
						}
					}
				}
			});
		} else if (!endMessageSent) {
			Predicate<Entity> possible = generateEntitySelector(FeedableType::couldFeedAnimalIfWeHadFood);
			Entity closestEntity = helper.getClosestEntity(DISTANCE, possible);
			if (closestEntity != null) {
				Optional<FeedableType> type = feedableTypes.stream().filter(t -> t.couldFeedAnimalIfWeHadFood(closestEntity)).findFirst();
				AIChatController.addChatLine("Cannot feed more animals because I do not have the matching food. To feed that " +
						closestEntity.getType().getName().getString()
						+ ", I need " +
						type.orElseThrow(() -> new IllegalStateException("Expected a type to be found for " + closestEntity))
								.items
								.stream()
								.map(item -> item.getName().getString())
								.collect(Collectors.joining(" or "))
						+ " on the hotbar.");
			} else {
				AIChatController.addChatLine("No animals found to feed.");
			}
			endMessageSent = true;
		}
	}

	private Predicate<Entity> generateEntitySelector(BiPredicate<FeedableType, Entity> feedablePredicate) {
		Predicate<Entity> selector = entity -> feedableTypes.stream().anyMatch(type -> feedablePredicate.test(type,entity));
		// Not already fed
		selector = selector.and(entity -> !alreadyInteracted.contains(entity));
		if (color != null) {
			selector = selector.and(new ColorSelector(color));
		}
		return selector;
	}

	@Override
	public String getDescription(AIHelper helper) {
		return "Feeding";
	}

	@Override
	public String toString() {
		return "FeedAnimalsStrategy{" +
				"color=" + color +
				", feedableTypes=" + feedableTypes +
				'}';
	}

	public enum FeedableType {
		// See https://minecraft.gamepedia.com/Breeding
		HORSE(HorseEntity.class, Items.GOLDEN_APPLE, Items.GOLDEN_CARROT),
		DONKEY(DonkeyEntity.class, Items.GOLDEN_APPLE, Items.GOLDEN_CARROT),
		SHEEP(SheepEntity.class, Items.WHEAT),
		COW(CowEntity.class, Items.WHEAT),
		MOOSHROOM(MooshroomEntity.class, Items.WHEAT),
		PIG(PigEntity.class, Items.CARROT, Items.POTATO, Items.BEETROOT),
		CHICKEN(ChickenEntity.class, Items.WHEAT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS, Items.BEETROOT_SEEDS),
		WOLF(WolfEntity.class, Items.PORKCHOP,Items.COOKED_PORKCHOP, Items.BEEF, Items.COOKED_BEEF,
				Items.CHICKEN, Items.COOKED_CHICKEN, Items.RABBIT, Items.COOKED_RABBIT,
				Items.MUTTON, Items.COOKED_MUTTON, Items.ROTTEN_FLESH),
		CAT(CatEntity.class, Items.COD, Items.SALMON),
		OCELOT(OcelotEntity.class, Items.COD, Items.SALMON),
		RABBIT(RabbitEntity.class, Items.DANDELION, Items.CARROT, Items.GOLDEN_CARROT),
		LLAMA(LlamaEntity.class, Items.HAY_BLOCK),
		TURTLE(TurtleEntity.class,Items.SEAGRASS),
		PANDA(PandaEntity.class, Items.BAMBOO),
		FOX(FoxEntity.class, Items.SWEET_BERRIES),
		// Full list is at https://minecraft.gamepedia.com/Flowers
		BEE(BeeEntity.class, Items.DANDELION, Items.POPPY, Items.BLUE_ORCHID, Items.ALLIUM, Items.AZURE_BLUET,
				Items.ORANGE_TULIP, Items.PINK_TULIP, Items.RED_TULIP, Items.WHITE_TULIP, Items.OXEYE_DAISY,
				Items.CORNFLOWER, Items.LILY_OF_THE_VALLEY, Items.WITHER_ROSE,Items.SUNFLOWER, Items.LILAC, Items.ROSE_BUSH, Items.PEONY);

		private final Class<? extends Entity> entityClass;
		private final List<Item> items;

		FeedableType(Class<? extends Entity> entityClass, Item... item) {
			this.entityClass = entityClass;
			this.items = Arrays.asList(item);
		}

		boolean canFeedAnimal(Entity animal, AIHelper helper) {
			if (!couldFeedAnimalIfWeHadFood(animal)) {
				return false;
			}

			// Check if we have the food we need
			if (!helper.canSelectItem(getItemFilter())) {
				LOGGER.debug("Cannot feed {} because we do not have any of the required items: {}", animal, items);
				return false;
			}

			return true;
		}

		private boolean couldFeedAnimalIfWeHadFood(Entity animal) {
			if (entityClass != animal.getClass()) {
				return false;
			}

			// Check if animal is grown up
			if (animal instanceof AnimalEntity && ((AnimalEntity) animal).getGrowingAge() < 0 && !isHungryWolf(animal)) {
				LOGGER.debug("Cannot feed {} because it is not grown up", animal);
				return false;
			}

			if (animal instanceof AnimalEntity && ((AnimalEntity) animal).getHealth() <= 0) {
				LOGGER.debug("Cannot feed {} because it has died", animal);
				return false;
			}
			return true;
		}

		private ItemFilter getItemFilter() {
			return item -> items.contains(item.getItem());
		}

		private boolean isHungryWolf(Entity animal) {
			return animal instanceof WolfEntity
					&& ((WolfEntity) animal).getHealth() < ((WolfEntity) animal).getMaxHealth();
		}
	}
}