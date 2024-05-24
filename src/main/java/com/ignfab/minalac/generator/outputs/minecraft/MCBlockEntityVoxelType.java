package com.ignfab.minalac.generator.outputs.minecraft;

import com.ignfab.minalac.generator.world.OutOfWorldException;
import net.querz.nbt.tag.CompoundTag;

import java.util.Map;

public abstract class MCBlockEntityVoxelType extends MCVoxelType {
    public MCBlockEntityVoxelType(MCVoxelWorld world, String type) {
        super(world, type);
    }

    public MCBlockEntityVoxelType(MCVoxelWorld world, String type, Map<String, String> properties) {
        super(world, type, properties);
    }

    protected abstract void serialize(CompoundTag tag);

    @Override
    public void place(int x, int y, int z) throws OutOfWorldException {
        super.place(x, y, z);
        CompoundTag block = new CompoundTag();
        block.putString("id", type);
        block.putBoolean("keepPacked", false);
        // XYZ => XZY
        block.putInt("x", x);
        block.putInt("y", z);
        block.putInt("z", y);
        serialize(block);
        world.addBlockEntity(x, z, y, block); // XYZ => XZY
    }
}
