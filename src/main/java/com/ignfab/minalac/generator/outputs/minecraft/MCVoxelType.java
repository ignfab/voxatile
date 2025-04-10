package com.ignfab.minalac.generator.outputs.minecraft;

import java.util.Map;

import net.querz.nbt.tag.CompoundTag;

import com.ignfab.minalac.generator.placeables.VoxelType;
import com.ignfab.minalac.generator.world.VoxelWorldTile;

/**
 * {@code MCVoxelType} class provides the necessary structure and mechanism in order to implement {@link VoxelType} for Minecraft.
 * A voxel in Minecraft, known as block, consists of two parameters: type and state properties.
 */
public class MCVoxelType implements VoxelType {
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
     * @param type the block type string
     */
    public MCVoxelType(String type) {
        this(type, null);
    }

    /**
     * Constructs a new {@code MCVoxelType}.
     *
     * @param type the block type string
     * @param properties the block state properties
     */
    public MCVoxelType(String type, Map<String, String> properties) {
        this.type = type;
        this.properties = properties;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void place(VoxelWorldTile tile, int x, int y, int z)  {
        CompoundTag block = new CompoundTag();
        block.putString("Name", type);
        if (properties != null) {
            CompoundTag state = new CompoundTag();
            properties.forEach(state::putString);
            block.put("Properties", state);
        }
        ((MCVoxelWorldTile) tile).setBlockState(x, z, -y - 1, block); // X/Y/Z => X/Z/-Y
    }
}
