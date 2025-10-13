package com.ignfab.minalac.generator.modules.minecraft;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import io.github.ensgijs.nbt.io.NamedTag;
import io.github.ensgijs.nbt.io.TextNbtHelpers;
import io.github.ensgijs.nbt.tag.CompoundTag;

/**
 * {@code MinecraftBlockEntityVoxel} class represents a Minecraft block with additional data associated with it.
 * That additional information is known as block entity.
 * <p>
 * Like {@link MinecraftVoxel}, it is immutable. A builder pattern may help to abstract the raw NBT structure
 * used to represent its data.
 * @see <a href="https://minecraft.wiki/w/Block_entity"> Block entity (Minecraft Wiki)</a>
 */
public class MinecraftBlockEntityVoxel extends MinecraftVoxel {
    private final String id;
    private final CompoundTag data;

    /**
     * Constructs a new {@code MinecraftBlockEntityVoxel}.
     *
     * @param voxel the block state as an existing voxel
     * @param id the block entity ID
     * @param data the block entity data
     */
    public MinecraftBlockEntityVoxel(MinecraftVoxel voxel, String id, CompoundTag data) {
        this(voxel.type(), id, voxel.properties(), data);
    }

    /**
     * Constructs a new {@code MinecraftBlockEntityVoxel}.
     *
     * @param type the block type string
     * @param id the block entity ID
     * @param properties the block state properties
     * @param data the block entity data
     */
    public MinecraftBlockEntityVoxel(String type, String id, Map<String, String> properties, CompoundTag data) {
        super(type, properties);
        this.id = MinecraftHelpers.ensureNamespaced(id);
        this.data = data == null ? new CompoundTag() : data.clone();
    }

    /**
     * {@return the block entity ID}
     * @see <a href="https://minecraft.wiki/w/Block_entity_format#Types">List of block entities (Minecraft Wiki)</a>
     */
    public String id() {
        return id;
    }

    /**
     * {@return a copy of the block entity data}
     * The meaning of that data is entirely dependent on the {@link #id}.
     */
    public CompoundTag data() {
        return data.clone();
    }

    /**
     * Returns the voxel representing this block, without additional block entity data.
     *
     * @return simple voxel with block entity data stripped
     */
    public MinecraftVoxel stripBlockEntity() {
        return new MinecraftVoxel(type(), properties());
    }

    @Override
    protected void place(MinecraftVoxelTile tile, int x, int y, int z)  {
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
        MinecraftBlockEntityVoxel that = (MinecraftBlockEntityVoxel) o;
        return id.equals(that.id) && data.equals(that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, data);
    }

    /**
     * Serializes this voxel to a block state string including block entity data.
     *
     * @return a string representing the block
     * @see #fromString(String)
     */
    @Override
    public String toString() {
        String str = super.toString();
        if (data.isEmpty())
            return str;
        return str + TextNbtHelpers.toTextNbt(data, false, true);
    }

    /**
     * Creates a new instance of {@link MinecraftBlockEntityVoxel} from a serialized block state string.
     * The block entity ID will be the same as the block type name.
     * Example: {@code minecraft:jukebox[has_record=true]{RecordItem: {id: "minecraft:music_disc_cat"}, ticks_since_song_started: 0L}}.
     * Extra whitespaces outside of data tags are not allowed!
     *
     * @param block the string representing a block
     * @return a new {@code MinecraftBlockEntityVoxel}
     * @see #toString()
     */
    public static MinecraftBlockEntityVoxel fromString(String block) {
        return fromString(block, null);
    }

    /**
     * Creates a new instance of {@link MinecraftBlockEntityVoxel} from a serialized block state string and a block entity ID.
     * Example: {@code minecraft:jukebox[has_record=true]{RecordItem: {id: "minecraft:music_disc_cat"}, ticks_since_song_started: 0L}}.
     * Extra whitespaces outside of data tags are not allowed!
     *
     * @param block the string representing a block
     * @param id the block entity ID
     * @return a new {@code MinecraftBlockEntityVoxel}
     * @see #toString()
     */
    public static MinecraftBlockEntityVoxel fromString(String block, String id) {
        int bracket = block.indexOf('{');
        if (bracket == -1) {
            MinecraftVoxel voxel = MinecraftVoxel.fromString(block);
            return new MinecraftBlockEntityVoxel(voxel, Objects.requireNonNullElse(id, voxel.type()), null);
        }
        if (block.lastIndexOf('}') != block.length() - 1)
            throw new IllegalArgumentException("Invalid block entity definition: " + block);
        MinecraftVoxel voxel = MinecraftVoxel.fromString(block.substring(0, bracket));
        String dataTags = block.substring(bracket);
        CompoundTag data;
        try {
            data = TextNbtHelpers.fromTextNbt(dataTags).getTagAutoCast();
        } catch (IOException e) {
            throw new IllegalArgumentException("Malformed data tags: " + dataTags, e);
        }
        return new MinecraftBlockEntityVoxel(voxel, Objects.requireNonNullElse(id, voxel.type()), data);
    }

    /**
     * Creates a new instance of {@link MinecraftBlockEntityVoxel} from
     * a {@link CompoundTag} and an existing {@link MinecraftVoxel}.
     *
     * @param block the {@code CompoundTag} representing a block entity
     * @param voxel the {@code MinecraftVoxel} representing the block state to use as a base
     * @return a new {@code MinecraftBlockEntityVoxel}
     */
    public static MinecraftBlockEntityVoxel fromBlockEntity(CompoundTag block, MinecraftVoxel voxel) {
        String id = block.getStringTag("id").getValue();
        CompoundTag data = block.clone();
        data.remove("id");
        data.remove("keepPacked");
        data.remove("x");
        data.remove("y");
        data.remove("z");
        return new MinecraftBlockEntityVoxel(voxel, id, data);
    }
}
