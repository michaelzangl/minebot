package net.famzangl.minecraft.minebot.ai.strategy.crafting;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.command.AIChatController;
import net.famzangl.minecraft.minebot.ai.enchanting.CloseScreenTask;
import net.famzangl.minecraft.minebot.ai.scanner.BlockRangeFinder;
import net.famzangl.minecraft.minebot.ai.scanner.BlockRangeScanner;
import net.famzangl.minecraft.minebot.ai.task.RunOnceTask;
import net.famzangl.minecraft.minebot.ai.task.TaskOperations;
import net.famzangl.minecraft.minebot.ai.task.UseItemOnBlockAtTask;
import net.famzangl.minecraft.minebot.ai.task.WaitTask;
import net.famzangl.minecraft.minebot.ai.task.inventory.MoveInInventoryTask;
import net.famzangl.minecraft.minebot.ai.task.inventory.PutInInventoryCraftingSlotTask;
import net.famzangl.minecraft.minebot.ai.task.inventory.PutOnCraftingTableTask;
import net.famzangl.minecraft.minebot.ai.task.inventory.TakeResultItem;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.inventory.CraftingScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class CraftingTableFinder extends BlockRangeFinder {
    private static final Logger LOGGER = LogManager
            .getLogger(CraftingTableFinder.class);

    private final CraftingWish wish;
    CraftStrategy.CraftingTableHandler craftingTableHandler = new CraftStrategy.CraftingTableHandler();
    private boolean failed;
    private int oldItemCount = -1;

    public CraftingTableFinder(CraftingWish wish) {
        this.wish = wish;
    }

    @Override
    protected boolean runSearch(BlockPos playerPosition) {
        int missing = getMissing();
        if (failed || missing <= 0) {
            return true;
        }
        // Quick: If crafting thing is 2x2, we can use the inventory.
        Optional<RecipePlacement> inventoryCraftable = wish.getPossibility(helper)
                .stream()
                .filter(it -> it.getRecipesToApply().size() == 1)
                .map(it -> it.getRecipesToApply().get(0).getPlacement(helper)
                        .flatMap(RecipePlacement::getAs2x2))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
        return inventoryCraftable
                .map(this::addTasksForInventoryCraft)
                .orElseGet(() -> super.runSearch(playerPosition));
    }

    @Override
    protected void noPathFound() {
        super.noPathFound();
        AIChatController.addChatLine("Could not find any crafting table to walk to / interact with");
    }

    /**
     *
     * @param recipePlacement The recipe to place. Has width and height 2
     * @return always false (try again) for now => next attempt will realize that we are done
     */
    private boolean addTasksForInventoryCraft(RecipePlacement recipePlacement) {
        // Open inventory
        addTask(new RunOnceTask() {
            @Override
            protected void runOnce(AIHelper aiHelper, TaskOperations taskOperations) {
                aiHelper.getMinecraft().displayGuiScreen(
                        new InventoryScreen(aiHelper.getMinecraft().player));
            }
        });

        // Put on crafting slots
        addCraftTaks(recipePlacement, PutInInventoryCraftingSlotTask::new);

        // Take result
        addTask(new TakeResultItem(InventoryScreen.class, 0));
        addTask(new WaitTask(5));

        // close inventory
        addTask(new CloseScreenTask());

        return false;
    }

    /**
     * Might be negative.
     *
     * @return
     */
    private int getMissing() {
        int itemCount = countInInventory(wish.getItem());
        if (oldItemCount < 0) {
            oldItemCount = itemCount;
        }
        return oldItemCount + wish.getAmount() - itemCount;
    }

    @Override
    protected BlockRangeScanner constructScanner(BlockPos playerPosition) {
        BlockRangeScanner scanner = super.constructScanner(playerPosition);
        scanner.addHandler(craftingTableHandler);
        return scanner;
    }

    @Override
    protected float rateDestination(int distance, int x, int y, int z) {
        ArrayList<CraftStrategy.CraftingTableData> tables = craftingTableHandler
                .getReachableForPos(new BlockPos(x, y, z));
        return !failed && tables != null && tables.size() > 0 ? distance
                : -1;
    }

    @Override
    protected void addTasksForTarget(BlockPos currentPos) {
        ArrayList<CraftStrategy.CraftingTableData> tables = craftingTableHandler
                .getReachableForPos(currentPos);
        CraftStrategy.CraftingTableData table = tables.get(0);
        List<CraftingPossibility> possibilities = wish.getPossibility(helper);
        LOGGER.trace("Crafting one of: " + possibilities);

        Optional<RecipePlacement> grid = possibilities
            .stream()
                // For now, only support one recipe. TODO: Find out how to best support multiple of them
                .map(p -> p.getRecipesToApply().get(0))
                .map(p -> p.getPlacement(helper))
                .flatMap(optional -> optional.isPresent() ? Stream.of(optional.get()) : Stream.of())
                .findAny();
        if (!grid.isPresent()) {
            failed = true;
            AIChatController.addChatLine("Could not find a way to craft this item");
            return;
        }

        LOGGER.trace("Crafting: {}", grid.get());

        addTask(new UseItemOnBlockAtTask(table.pos) {
            @Override
            protected boolean isBlockAllowed(AIHelper aiHelper, BlockPos pos) {
                return aiHelper.getBlock(pos) == Blocks.CRAFTING_TABLE;
            }

            @Override
            public boolean isFinished(AIHelper aiHelper) {
                return super.isFinished(aiHelper)
                        && aiHelper.getMinecraft().currentScreen instanceof CraftingScreen;
            }
        });
        addCraftOnCraftingTableTaks(grid.get());
        addTask(new WaitTask(5));
        addTask(new TakeResultItem(CraftingScreen.class, 0));
        addTask(new WaitTask(5));
        addTask(new CloseScreenTask());
    }

    private void addCraftOnCraftingTableTaks(RecipePlacement grid) {
        addCraftTaks(grid, PutOnCraftingTableTask::new);
    }

    private void addCraftTaks(RecipePlacement grid, PutOnCraftingGenerator generator) {
        int missing = getMissing();
        if (missing <= 0) {
            return;
        }
        for (int x = 0; x < grid.getWidht(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                ItemStack stack = grid.getStack(x, y);
                if (stack.getItem() != Items.AIR) {
                    int inventoryTotal = countInInventory(stack.getItem());
                    int slotsWithThatType = grid.getSlotsWithType(stack.getItem());
                    int itemCount = Math.min(inventoryTotal / slotsWithThatType,
                            missing);
                    addTask(generator.get(x, y,
                            stack.getItem(), itemCount));
                    addTask(new WaitTask(3));
                }
            }
        }
    }

    @FunctionalInterface
    interface PutOnCraftingGenerator {
        MoveInInventoryTask get(int x, int y, Item item, int itemCount);
    }

    private int countInInventory(Item itemWithSubtype) {
        int count = 0;
        for (ItemStack stack : helper.getMinecraft().player.inventory.mainInventory) {
            if (itemWithSubtype.equals(stack.getItem())) {
                count += stack.getCount();
            }
        }
        return count;
    }

    @Override
    public String toString() {
        return "CraftingTableFinder [wish=" + wish + ", failed=" + failed
                + ", oldItemCount=" + oldItemCount + "]";
    }
}
