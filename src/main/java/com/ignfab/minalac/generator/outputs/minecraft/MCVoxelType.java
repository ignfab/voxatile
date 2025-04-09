package com.ignfab.minalac.generator.outputs.minecraft;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.StringTag;

import com.ignfab.minalac.generator.placeables.VoxelType;

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
     * Creates a new instance of {@link MCVoxelType} from a {@link CompoundTag} and a {@link MCVoxelWorld}.
     *
     * @param block the {@code CompoundTag} representing a block.
     * @param world the world in which the voxel can be placed
     * @return a new {@code MCVoxelType}
     */
    public static MCVoxelType fromBlockState(CompoundTag block, MCVoxelWorld world) {
        String type = block.getStringTag("Name").getValue();

        CompoundTag propertiesTag = block.getCompoundTag("Properties");
        if (propertiesTag != null) {
            Map<String, String> properties = new HashMap<>();
            propertiesTag.forEach(entry -> properties.put(
                entry.getKey(),
                ((StringTag) entry.getValue()).getValue()
            ));
            return new MCVoxelType(world, type, properties);
        }

        return new MCVoxelType(world, type);
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
        world.setBlockState(x, z, -y - 1, block); // X/Y/Z => X/Z/-Y
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MCVoxelType that = (MCVoxelType) o;
        return world == that.world && type.equals(that.type) && Objects.equals(properties, that.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(world, type, properties);
    }
}
