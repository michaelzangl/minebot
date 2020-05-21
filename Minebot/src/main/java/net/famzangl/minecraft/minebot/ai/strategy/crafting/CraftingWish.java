package net.famzangl.minecraft.minebot.ai.strategy.crafting;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.minecraft.client.gui.recipebook.RecipeList;
import net.minecraft.client.util.ClientRecipeBook;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class CraftingWish {
    private static final Logger LOGGER = LogManager
            .getLogger(CraftingWish.class);
    private final int amount;
    private final Item item;

    public CraftingWish(int amount, Item item) {
        this.amount = amount;
        this.item = item;
    }

    public List<CraftingPossibility> getPossibility(AIHelper helper) {
        ClientRecipeBook book = helper.getMinecraft().player.getRecipeBook();
        // Minecraft stores a list of list of recipies.
        List<RecipeList> available = book.getRecipes();

        // Now find the one we need
        List<RecipeWithListAndCount> found = new ArrayList<>();
        available.forEach(list ->
                list.getRecipes().forEach(recipe -> {
                    ItemStack out = recipe.getRecipeOutput();
                    if (out.getItem() == item) {
                        found.add(new RecipeWithListAndCount(list, recipe,
                                // ceil
                                (amount + out.getCount() - 1) / out.getCount()));
                    }
                }));

        LOGGER.debug("For crafting {}, the following recipes were found: {}", item, found);


        return found.stream().map(it -> new CraftingPossibility(Collections.singletonList(it))).collect(Collectors.toList());
    }

    public int getAmount() {
        return amount;
    }

    public Item getItem() {
        return item;
    }

    @Override
    public String toString() {
        return "CraftingWish [amount=" + amount + ", item=" + item + "]";
    }
}
