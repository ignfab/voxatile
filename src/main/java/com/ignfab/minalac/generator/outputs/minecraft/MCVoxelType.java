package com.ignfab.minalac.generator.outputs.minecraft;

import com.ignfab.minalac.generator.world.OutOfWorldException;
import com.ignfab.minalac.generator.world.VoxelType;
import net.querz.nbt.tag.CompoundTag;

import java.util.Map;

public class MCVoxelType implements VoxelType {
    protected final MCVoxelWorld world;
    protected final String type;
    protected final Map<String, String> properties;

    public MCVoxelType(MCVoxelWorld world, String type) {
        this(world, type, null);
    }

    public MCVoxelType(MCVoxelWorld world, String type, Map<String, String> properties) {
        this.world = world;
        this.type = type;
        this.properties = properties;
    }

    @Override
    public void place(int x, int y, int z) throws OutOfWorldException {
        CompoundTag block = new CompoundTag();
        block.putString("Name", type);
        if (properties != null) {
            CompoundTag state = new CompoundTag();
            properties.forEach(state::putString);
            block.put("Properties", state);
        }
        world.setBlockState(x, z, y, block); // XYZ => XZY
    }
}
