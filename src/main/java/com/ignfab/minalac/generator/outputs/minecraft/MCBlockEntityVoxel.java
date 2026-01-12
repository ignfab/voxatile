package com.ignfab.minalac.generator.outputs.minecraft;

import java.util.Map;

import io.github.ensgijs.nbt.tag.CompoundTag;

// TODO This class probably needs a rework
// It does not need to be abstract: it could handle raw NBT,
// which would not prevent a specific implementation to provide
// higher-level API
/**
 * {@code MCBlockEntityVoxel} abstract class represents a Minecraft block with additional data associated with it.
 * That additional information is known as block entity.
 * @see <a href="https://minecraft.wiki/w/Block_entity"> Block entity (Minecraft wiki)</a>
 */
public abstract class MCBlockEntityVoxel extends MCVoxel {
    protected final String id;

    /**
     * Constructs a new {@code MCBlockEntityVoxel}.
     * Block type will be used as the block entity ID.
     *
     * @param type the block type string
     */
    public MCBlockEntityVoxel(String type) {
        this(type, type);
    }

    /**
     * Constructs a new {@code MCBlockEntityVoxel}.
     * Block type will be used as the block entity ID.
     *
     * @param type the block type string
     * @param properties the block state properties
     */
    public MCBlockEntityVoxel(String type, Map<String, String> properties) {
        this(type, type, properties);
    }

    /**
     * Constructs a new {@code MCBlockEntityVoxel}.
     *
     * @param type the block type string
     * @param id the block entity ID
     */
    public MCBlockEntityVoxel(String type, String id) {
        this(type, id, null);
    }

    /**
     * Constructs a new {@code MCBlockEntityVoxel}.
     *
     * @param type the block type string
     * @param id the block entity ID
     * @param properties the block state properties
     */
    public MCBlockEntityVoxel(String type, String id, Map<String, String> properties) {
        super(type, properties);
        this.id = id;
    }

    protected abstract void serialize(CompoundTag tag);

    @Override
    protected void place(MCVoxelTile tile, int x, int y, int z)  {
        super.place(tile, x, y, z);
        CompoundTag block = new CompoundTag();
        block.putString("id", id);
        block.putBoolean("keepPacked", false);
        // TODO Position could be set by the MCVoxelTile class to ensure the same XYZ values are being used
        // X/Y/Z => X/Z/-Y
        block.putInt("x", x);
        block.putInt("y", z);
        block.putInt("z", -y - 1);
        serialize(block);
        tile.addBlockEntity(x, z, -y - 1, block); // X/Y/Z => X/Z/-Y
    }

    // TODO Simple implementation could be loaded from SNBT
}
