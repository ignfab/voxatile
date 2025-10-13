package com.ignfab.minalac.generator.outputs.minecraft;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import io.github.ensgijs.nbt.tag.CompoundTag;
import io.github.ensgijs.nbt.tag.StringTag;

import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.world.VoxelTile;

/**
 * {@code MCVoxel} class implements a {@link Placeable} voxel for Minecraft.
 * A voxel in Minecraft, known as block, consists of two parameters: type and state properties.
 */
public class MCVoxel implements Placeable {
    /**
     * The block type string.
     * @see <a href="https://minecraft.wiki/w/Block">List of block types (Minecraft Wiki)</a>
     */
    protected final String type;
    /**
     * The block state properties.
     */
    protected final Map<String, String> properties;

    // Lazily-initialized reusable block data
    private CompoundTag block;

    /**
     * Constructs a new {@code MCVoxel}.
     *
     * @param type the block type string
     */
    public MCVoxel(String type) {
        this(type, null);
    }

    /**
     * Constructs a new {@code MCVoxel}.
     *
     * @param type the block type string
     * @param properties the block state properties
     */
    public MCVoxel(String type, Map<String, String> properties) {
        this.type = type;
        this.properties = properties;
    }

    /**
     * Creates a new instance of {@link MCVoxel} from a {@link CompoundTag} and a {@link MCVoxelWorld}.
     *
     * @param block the {@code CompoundTag} representing a block.
     * @return a new {@code MCVoxel}
     */
    public static MCVoxel fromBlockState(CompoundTag block) {
        String type = block.getStringTag("Name").getValue();

        CompoundTag propertiesTag = block.getCompoundTag("Properties");
        if (propertiesTag != null) {
            Map<String, String> properties = new HashMap<>();
            propertiesTag.forEach(entry -> properties.put(
                entry.getName(),
                entry.<StringTag>getTagAutoCast().getValue()
            ));
            return new MCVoxel(type, properties);
        }

        return new MCVoxel(type);
    }

    @Override
    public void place(VoxelTile tile, int x, int y, int z)  {
        if (tile instanceof MCVoxelTile mcTile) {
            place(mcTile, x, y, z);
        } else {
            throw new IllegalArgumentException("Voxel does not match voxel tile output format");
        }
    }

    /**
     * Places this voxel on a {@link MCVoxelTile} at given position.
     *
     * @param tile tile to place into
     * @param x position x-coordinate
     * @param y position y-coordinate
     * @param z position z-coordinate
     */
    protected void place(MCVoxelTile tile, int x, int y, int z)  {
        if (block == null) {
            block = new CompoundTag();
            block.putString("Name", type);
            if (properties != null) {
                CompoundTag state = new CompoundTag();
                properties.forEach(state::putString);
                block.put("Properties", state);
            }
        }
        tile.setBlockState(x, z, -y - 1, block); // X/Y/Z => X/Z/-Y
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MCVoxel that = (MCVoxel) o;
        return type.equals(that.type) && Objects.equals(properties, that.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, properties);
    }
}
