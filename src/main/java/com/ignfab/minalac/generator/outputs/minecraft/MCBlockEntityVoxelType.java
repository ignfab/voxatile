package com.ignfab.minalac.generator.outputs.minecraft;

import net.querz.nbt.tag.CompoundTag;

import java.util.Map;

/**
 * {@code MCBlockEntityVoxelType} abstract class represents a Minecraft block with additional data associated with it.
 * That additional information is known as block entity.
 * @see <a href="https://minecraft.wiki/w/Block_entity"> Block entity (Minecraft wiki)</a>
 */
public abstract class MCBlockEntityVoxelType extends MCVoxelType {
    /**
     * Constructs a new {@code MCBlockEntityVoxelType}.
     *
     * @param world the {@link MCVoxelWorld} in which the voxel can be placed
     * @param type the block type string
     */
    public MCBlockEntityVoxelType(MCVoxelWorld world, String type) {
        super(world, type);
    }

    /**
     * Constructs a new {@code MCBlockEntityVoxelType}.
     *
     * @param world the {@link MCVoxelWorld} in which the voxel can be placed
     * @param type the block type string
     * @param properties the block state properties
     */
    public MCBlockEntityVoxelType(MCVoxelWorld world, String type, Map<String, String> properties) {
        super(world, type, properties);
    }

    protected abstract void serialize(CompoundTag tag);

    /**
     * {@inheritDoc}
     */
    @Override
    public void place(int x, int y, int z)  {
        super.place(x, y, z);
        CompoundTag block = new CompoundTag();
        block.putString("id", type);
        block.putBoolean("keepPacked", false);
        // X/Y/Z => X/Z/-Y
        block.putInt("x", x);
        block.putInt("y", z);
        block.putInt("z", -y);
        serialize(block);
        world.addBlockEntity(x, z, -y, block); // X/Y/Z => X/Z/-Y
    }
}
