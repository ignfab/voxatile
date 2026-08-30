package com.ignfab.minalac.generator.modules.minecraft;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.StringTag;

import com.ignfab.minalac.generator.world.Voxel;
import com.ignfab.minalac.generator.world.VoxelTile;

/**
 * {@code MinecraftVoxel} class implements a {@link Voxel} for Minecraft.
 * A voxel in Minecraft, known as block, consists of two parameters: type and state properties.
 */
public class MinecraftVoxel implements Voxel {

    /**
     * Default voxel used on map initialization.
     */
    public static final MinecraftVoxel DEFAULT_VOXEL = new MinecraftVoxel("minecraft:air");

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
     * Constructs a new {@code MinecraftVoxel}.
     *
     * @param type the block type string
     */
    public MinecraftVoxel(String type) {
        this(type, null);
    }

    /**
     * Constructs a new {@code MinecraftVoxel}.
     *
     * @param type the block type string
     * @param properties the block state properties
     */
    public MinecraftVoxel(String type, Map<String, String> properties) {
        this.type = type;
        this.properties = properties;
    }

    /**
     * Creates a new instance of {@link MinecraftVoxel} from a {@link CompoundTag} and a {@link MinecraftVoxelWorld}.
     *
     * @param block the {@code CompoundTag} representing a block.
     * @return a new {@code MinecraftVoxel}
     */
    public static MinecraftVoxel fromBlockState(CompoundTag block) {
        String type = block.getStringTag("Name").getValue();

        CompoundTag propertiesTag = block.getCompoundTag("Properties");
        if (propertiesTag != null) {
            Map<String, String> properties = new HashMap<>();
            propertiesTag.forEach(entry -> properties.put(
                entry.getKey(),
                ((StringTag) entry.getValue()).getValue()
            ));
            return new MinecraftVoxel(type, properties);
        }

        return new MinecraftVoxel(type);
    }

    @Override
    public String getTypeIdentifier() {
        return type;
    }

    @Override
    public void place(VoxelTile tile, int x, int y, int z)  {
        if (tile instanceof MinecraftVoxelTile mcTile) {
            place(mcTile, x, y, z);
        } else {
            throw new IllegalArgumentException("Voxel does not match voxel tile output format");
        }
    }

    /**
     * Places this voxel on a {@link MinecraftVoxelTile} at given position.
     *
     * @param tile tile to place into
     * @param x position x-coordinate
     * @param y position y-coordinate
     * @param z position z-coordinate
     */
    protected void place(MinecraftVoxelTile tile, int x, int y, int z)  {
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
        MinecraftVoxel that = (MinecraftVoxel) o;
        return type.equals(that.type) && Objects.equals(properties, that.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, properties);
    }
}
