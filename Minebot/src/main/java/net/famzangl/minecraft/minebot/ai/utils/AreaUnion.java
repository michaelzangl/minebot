package net.famzangl.minecraft.minebot.ai.utils;

import net.famzangl.minecraft.minebot.ai.path.world.WorldData;

public class AreaUnion<WorldT extends WorldData> extends BlockArea<WorldT> {

    private final BlockArea<? super WorldT> a;
    private final BlockArea<? super WorldT> b;

    public AreaUnion(BlockArea<? super WorldT> a, BlockArea<? super WorldT> b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public <WorldT2 extends WorldT> void accept(AreaVisitor<? super WorldT2> visitor, WorldT2 world) {
        a.accept(visitor, world);
        b.<WorldT2>accept((world1, x, y, z) -> {
            if (!a.contains(world1, x, y, z)) {
                visitor.visit(world1, x, y, z);
            }
        }, world);
    }

    @Override
    public boolean contains(WorldT world, int x, int y, int z) {
        return a.contains(world, x, y, z) || b.contains(world, x, y, z);
    }
}
