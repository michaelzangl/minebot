package net.famzangl.minecraft.minebot.ai.strategy.crafting;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipePlacer;
import net.minecraft.item.crafting.RecipeItemHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Stream;

public class RecipePlacement {
    private static final Logger LOGGER = LogManager
            .getLogger(RecipePlacement.class);
    private final ItemStack[][] stacksToPlace;

    public RecipePlacement(int width, int height) {
        this.stacksToPlace = new ItemStack[height][width];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                stacksToPlace[y][x] = new ItemStack(() -> Items.AIR, 0);
            }
        }
    }

    public int getWidht() {
        return stacksToPlace[0].length;
    }

    public int getHeight() {
        return stacksToPlace.length;
    }

    static RecipePlacement of(int size, IRecipe<?> recipe, Iterator<Integer> ingredients, int count) {
        RecipePlacement placement = new RecipePlacement(size, size);

        new IRecipePlacer<Integer>(){
            @Override
            public void setSlotContents(Iterator<Integer> ingredients, int slotIn, int maxAmount, int y, int x) {
                ItemStack itemstack = RecipeItemHelper.unpack(ingredients.next());
                LOGGER.trace("Placing stack at {},{}: {}", x, y, itemstack);
                placement.stacksToPlace[y][x] = itemstack;
            }
        }.placeRecipe(size, size, -1, recipe, ingredients, count);

        return placement;
    }

    @Nonnull
    public ItemStack getStack(int x, int y) {
        return stacksToPlace[y][x];
    }

    public Optional<RecipePlacement> getAs2x2() {
        if (getWidht() == 2 && getHeight() == 2) {
            return Optional.of(this);
        } else if (getWidht() == 3 && getHeight() == 3
            && isEmptyStack(0, 2) && isEmptyStack(1, 2) && isEmptyStack(2, 0)
                && isEmptyStack(2, 1) && isEmptyStack(2, 2)) {
            // we assume, that 2x2 recipes are always put in the top left area
            RecipePlacement newPlacement = new RecipePlacement(2, 2);
            for (int x = 0; x < 2; x++) {
                for (int y = 0; y < 2; y++) {
                    newPlacement.stacksToPlace[y][x] = stacksToPlace[y][x];
                }
            }
            return Optional.of(newPlacement);
        } else {
            return Optional.empty();
        }
    }

    private boolean isEmptyStack(int x, int y) {
        return getStack(x, y).isEmpty();
    }

    @Override
    public String toString() {
        return "RecipePlacement{" +
                "stacksToPlace=" + Arrays.toString(stacksToPlace) +
                '}';
    }

    public int getSlotsWithType(Item item) {
        return (int) Stream.of(stacksToPlace)
                .flatMap(Stream::of)
                .filter(it -> it.getItem() == item)
                .count();
    }
}
