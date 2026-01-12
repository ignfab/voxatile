package com.ignfab.minalac.generator.outputs.minecraft;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import io.github.ensgijs.nbt.io.NamedTag;
import io.github.ensgijs.nbt.io.TextNbtHelpers;
import io.github.ensgijs.nbt.tag.CompoundTag;

/**
 * {@code MCBlockEntityVoxel} class represents a Minecraft block with additional data associated with it.
 * That additional information is known as block entity.
 * <p>
 * Like {@link MCVoxel}, it is immutable. A builder pattern may help to abstract the raw NBT structure
 * used to represent its data.
 * @see <a href="https://minecraft.wiki/w/Block_entity"> Block entity (Minecraft Wiki)</a>
 */
public class MCBlockEntityVoxel extends MCVoxel {
    /**
     * The block entity ID.
     * @see <a href="https://minecraft.wiki/w/Block_entity_format#Types">List of block entities (Minecraft Wiki)</a>
     */
    private final String id;
    /**
     * The block entity data, according to its {@link #id}.
     */
    private final CompoundTag data; // NBT data has no order, which may cause trouble with equality checks

    /**
     * Constructs a new {@code MCBlockEntityVoxel}.
     *
     * @param voxel the block state as an existing voxel
     * @param id the block entity ID
     * @param data the block entity data
     */
    public MCBlockEntityVoxel(MCVoxel voxel, String id, CompoundTag data) {
        this(voxel.type(), id, voxel.properties(), data);
    }

    /**
     * Constructs a new {@code MCBlockEntityVoxel}.
     *
     * @param type the block type string
     * @param id the block entity ID
     * @param properties the block state properties
     * @param data the block entity data
     */
    public MCBlockEntityVoxel(String type, String id, Map<String, String> properties, CompoundTag data) {
        super(type, properties);
        this.id = MCHelpers.ensureNamespaced(id);
        this.data = data == null ? new CompoundTag() : data.clone();
    }

    @Override
    protected void place(MCVoxelTile tile, int x, int y, int z)  {
        super.place(tile, x, y, z);
        CompoundTag block = new CompoundTag();
        block.putString("id", id);
        block.putBoolean("keepPacked", false);
        for (NamedTag tag : data)
            block.put(tag);
        tile.addBlockEntity(x, z, -y - 1, block); // X/Y/Z => X/Z/-Y
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) return false;
        MCBlockEntityVoxel that = (MCBlockEntityVoxel) o;
        return id.equals(that.id) && data.equals(that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, data);
    }

    /**
     * Creates a new instance of {@link MCBlockEntityVoxel} from a {@link CompoundTag} and an existing {@link MCVoxel}.
     *
     * @param block the {@code CompoundTag} representing a block entity.
     * @param voxel the {@code MCVoxel} representing the block state to use as a base.
     * @return a new {@code MCBlockEntityVoxel}
     */
    public static MCBlockEntityVoxel fromBlockEntity(CompoundTag block, MCVoxel voxel) {
        String id = block.getStringTag("id").getValue();
        CompoundTag data = block.clone();
        data.remove("id");
        data.remove("keepPacked");
        data.remove("x");
        data.remove("y");
        data.remove("z");
        return new MCBlockEntityVoxel(voxel, id, data);
    }

    /**
     * Creates a new instance of {@link MCBlockEntityVoxel} from a serialized block state string and a block entity ID.
     * Example: {@code minecraft:jukebox[has_record=true]{RecordItem: {id: "minecraft:music_disc_cat"}, ticks_since_song_started: 0}}.
     * Extra whitespaces outside of data tags are not allowed!
     *
     * @param block the string representing a block.
     * @param id the block entity ID.
     * @return a new {@code MCBlockEntityVoxel}
     */
    public static MCBlockEntityVoxel fromString(String block, String id) {
        int bracket = block.indexOf('{');
        if (bracket == -1) {
            MCVoxel voxel = MCVoxel.fromString(block);
            return new MCBlockEntityVoxel(voxel, id, null);
        }
        if (block.indexOf('}') != block.length() - 1)
            throw new IllegalArgumentException("Invalid block entity definition: " + block);
        MCVoxel voxel = MCVoxel.fromString(block.substring(0, bracket));
        String dataTags = block.substring(bracket);
        CompoundTag data;
        try {
            data = TextNbtHelpers.fromTextNbt(dataTags).getTagAutoCast();
        } catch (IOException e) {
            throw new IllegalArgumentException("Malformed data tags: " + dataTags, e);
        }
        return new MCBlockEntityVoxel(voxel, id, data);
    }
}
