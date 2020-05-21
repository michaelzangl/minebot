package net.famzangl.minecraft.minebot.ai.strategy.crafting;

import java.util.List;

/**
 * Apply all RecipeWithListAndCount in that order (used for multiple crafting steps)
 */
public final class CraftingPossibility {

    private final List<RecipeWithListAndCount> recipesToApply;

    public CraftingPossibility(List<RecipeWithListAndCount> recipesToApply) {
        this.recipesToApply = recipesToApply;
    }

    public List<RecipeWithListAndCount> getRecipesToApply() {
        return recipesToApply;
    }

    @Override
    public String toString() {
        return "CraftingPossibility{" +
                "recipesToApply=" + recipesToApply +
                '}';
    }
}
