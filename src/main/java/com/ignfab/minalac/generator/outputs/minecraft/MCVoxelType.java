package com.ignfab.minalac.generator.outputs.minecraft;

import com.ignfab.minalac.generator.world.VoxelType;
import net.querz.nbt.tag.CompoundTag;

import java.util.Map;

/**
 * {@code MCVoxelType} class provides the necessary structure and mechanism in order to implement {@link VoxelType} for Minecraft.
 * A voxel in Minecraft, known as block, consists of two parameters: type and state properties.
 */
public class MCVoxelType implements VoxelType {
    /**
     * The Minecraft World object.
     */
    protected final MCVoxelWorld world;
    /**
     * The block type string.
     * @see <a href="https://minecraft.wiki/w/Block">List of block types (Minecraft Wiki)</a>
     */
    protected final String type;
    /**
     * The block state properties.
     */
    protected final Map<String, String> properties;

    /**
     * Constructs a new {@code MCVoxelType}.
     *
     * @param world the {@link MCVoxelWorld} in which the voxel can be placed
     * @param type the block type string
     */
    public MCVoxelType(MCVoxelWorld world, String type) {
        this(world, type, null);
    }

    /**
     * Constructs a new {@code MCVoxelType}.
     *
     * @param world the {@link MCVoxelWorld} in which the voxel can be placed
     * @param type the block type string
     * @param properties the block state properties
     */
    public MCVoxelType(MCVoxelWorld world, String type, Map<String, String> properties) {
        this.world = world;
        this.type = type;
        this.properties = properties;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void place(int x, int y, int z)  {
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
