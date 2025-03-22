package dev.redstudio.fbp.node;

import io.netty.util.internal.ConcurrentSet;
import net.minecraft.util.math.BlockPos;

public class BlockPosNode {

    ConcurrentSet<BlockPos> possible = new ConcurrentSet<>();

    public boolean checked = false;

    public void add(BlockPos pos) {
        possible.add(pos);
    }

    public boolean hasPos(BlockPos p1) {
        return possible.contains(p1);
    }
}
