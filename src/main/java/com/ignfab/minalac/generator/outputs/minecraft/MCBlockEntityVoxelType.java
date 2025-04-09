package com.ignfab.minalac.generator.outputs.minecraft;

import java.util.Map;

import net.querz.nbt.tag.CompoundTag;

import com.ignfab.minalac.generator.world.VoxelWorld;

/**
 * {@code MCBlockEntityVoxelType} abstract class represents a Minecraft block with additional data associated with it.
 * That additional information is known as block entity.
 * @see <a href="https://minecraft.wiki/w/Block_entity"> Block entity (Minecraft wiki)</a>
 */
public abstract class MCBlockEntityVoxelType extends MCVoxelType {
    /**
     * Constructs a new {@code MCBlockEntityVoxelType}.
     *
     * @param type the block type string
     */
    public MCBlockEntityVoxelType(String type) {
        super(type);
    }

    /**
     * Constructs a new {@code MCBlockEntityVoxelType}.
     *
     * @param type the block type string
     * @param properties the block state properties
     */
    public MCBlockEntityVoxelType(String type, Map<String, String> properties) {
        super(type, properties);
    }

    protected abstract void serialize(CompoundTag tag);

    /**
     * {@inheritDoc}
     */
    @Override
    public void place(VoxelWorld world, int x, int y, int z)  {
        super.place(world, x, y, z);
        CompoundTag block = new CompoundTag();
        block.putString("id", type);
        block.putBoolean("keepPacked", false);
        // X/Y/Z => X/Z/-Y
        block.putInt("x", x);
        block.putInt("y", z);
        block.putInt("z", -y - 1);
        serialize(block);
        ((MCVoxelWorld) world).addBlockEntity(x, z, -y - 1, block); // X/Y/Z => X/Z/-Y
    }
}
