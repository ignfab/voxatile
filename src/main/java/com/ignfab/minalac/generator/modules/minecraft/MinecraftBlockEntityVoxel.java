package com.ignfab.minalac.generator.modules.minecraft;

import java.util.Map;

import net.querz.nbt.tag.CompoundTag;

/**
 * {@code MinecraftBlockEntityVoxel} abstract class represents a Minecraft block with additional data associated with it.
 * That additional information is known as block entity.
 * @see <a href="https://minecraft.wiki/w/Block_entity"> Block entity (Minecraft wiki)</a>
 */
public abstract class MinecraftBlockEntityVoxel extends MinecraftVoxel {
    /**
     * Constructs a new {@code MinecraftBlockEntityVoxel}.
     *
     * @param type the block type string
     */
    public MinecraftBlockEntityVoxel(String type) {
        super(type);
    }

    /**
     * Constructs a new {@code MinecraftBlockEntityVoxel}.
     *
     * @param type the block type string
     * @param properties the block state properties
     */
    public MinecraftBlockEntityVoxel(String type, Map<String, String> properties) {
        super(type, properties);
    }

    protected abstract void serialize(CompoundTag tag);

    @Override
    protected void place(MinecraftVoxelTile tile, int x, int y, int z)  {
        super.place(tile, x, y, z);
        CompoundTag block = new CompoundTag();
        block.putString("id", type);
        block.putBoolean("keepPacked", false);
        // X/Y/Z => X/Z/-Y
        block.putInt("x", x);
        block.putInt("y", z);
        block.putInt("z", -y - 1);
        serialize(block);
        tile.addBlockEntity(x, z, -y - 1, block); // X/Y/Z => X/Z/-Y
    }
}
