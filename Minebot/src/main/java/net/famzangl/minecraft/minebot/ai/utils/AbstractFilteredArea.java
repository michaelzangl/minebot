package net.famzangl.minecraft.minebot.ai.utils;

import net.famzangl.minecraft.minebot.ai.path.world.WorldData;

public abstract class AbstractFilteredArea<W extends WorldData> extends BlockArea<W> {

    protected final BlockArea<W> base;

    public AbstractFilteredArea(BlockArea<W> base) {
        this.base = base;
    }


    @Override
    public <WorldT2 extends W> void accept(AreaVisitor<? super WorldT2> visitor, WorldT2 world) {
        base.accept(new FilteredVisitor<>(visitor), world);
    }

    @Override
    public boolean contains(W world, int x, int y, int z) {
        return base.contains(world, x, y, z) && test(world, x, y, z);
    }

    protected abstract boolean test(W world, int x, int y, int z);

    private class FilteredVisitor<W2 extends W> implements AreaVisitor<W2> {
        private final AreaVisitor<? super W2> visitor;

        public FilteredVisitor(AreaVisitor<? super W2> visitor) {
            this.visitor = visitor;
        }

        @Override
        public void visit(W2 world, int x, int y, int z) {
            if (test(world, x, y, z)) {
                visitor.visit(world, x, y, z);
            }
        }
    }
}
