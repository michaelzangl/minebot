package net.famzangl.minecraft.minebot.ai.strategy.crafting;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.minecraft.client.gui.recipebook.RecipeList;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeItemHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

/**
 * A recipe and how often it should be used
 */
public class RecipeWithListAndCount {
    private static final Logger LOGGER = LogManager
            .getLogger(RecipeWithListAndCount.class);
    private final RecipeList list;
    private final IRecipe<?> recipe;
    private final int count;
    private final RecipeItemHelper helper = new RecipeItemHelper();

    public RecipeWithListAndCount(RecipeList list, IRecipe<?> recipe, int count) {
        this.list = list;
        this.recipe = recipe;
        this.count = count;
    }

    public Optional<RecipePlacement> getPlacement(AIHelper aiHelper) {
        LOGGER.trace("Attempt to place recipe {} ", recipe);

        helper.clear();
        // Now determine all te items we have in the inventory
        aiHelper.getMinecraft().player.inventory.accountStacks(helper);
        int realMaxCount = Math.min(count, helper.getBiggestCraftableStack(recipe, null));
        if (realMaxCount == 0) {
            LOGGER.debug("Attempt to craft {} items, but biggest craftable stack is empty => we do not have the items in the inventory", count);
            return Optional.empty();
        }
        LOGGER.trace("Requested {} stacks. Attempting to craft {} stacks.", count, realMaxCount);

        IntList intlist = new IntArrayList();
        if (helper.canCraft(recipe, intlist, realMaxCount)) {
            return Optional.of(RecipePlacement.of(3, recipe, intlist.iterator(), realMaxCount));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public String toString() {
        return "RecipeWithListAndCount{" +
                "list=" + list +
                ", recipe=" + recipe +
                ", count=" + count +
                '}';
    }
}
