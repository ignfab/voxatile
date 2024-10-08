package com.ignfab.minalac.generator.outputs.minecraft;

import com.ignfab.minalac.generator.world.EntityType;
import net.querz.nbt.tag.CompoundTag;

import java.util.UUID;

/**
 * {@code MCEntityType} class provides the necessary structure and mechanism in order to implement {@link EntityType} for Minecraft.
 * An entity in Minecraft consists of raw NBT data, including at least {@code id} and {@code Pos}.
 */
public abstract class MCEntityType extends MCObject implements EntityType {
    /**
     * The entity type string.
     * @see <a href="https://minecraft.wiki/w/Entity">List of entity types (Minecraft Wiki)</a>
     */
    protected final String id;
    /**
     * The entity UUID.
     */
    protected final UUID uuid;

    /**
     * Constructs a new {@code MCEntityType}.
     * A random UUID is generated for this entity.
     *
     * @param world the {@link MCVoxelWorld} in which the entity can be placed
     * @param id the entity type string
     */
    public MCEntityType(MCVoxelWorld world, String id) {
        this(world, id, UUID.randomUUID());
    }

    /**
     * Constructs a new {@code MCEntityType}.
     *
     * @param world the {@link MCVoxelWorld} in which the entity can be placed
     * @param id the entity type string
     * @param uuid the entity UUID
     */
    public MCEntityType(MCVoxelWorld world, String id, UUID uuid) {
        super(world);
        this.id = id;
        this.uuid = uuid;
    }

    protected abstract void serialize(CompoundTag tag);

    @Override
    public void place(double x, double y, double z)  {
        CompoundTag entity = new CompoundTag();
        entity.putString("id", id);
        entity.putIntArray("UUID", MCUtils.uuidAsFourInts(uuid));
        entity.put("Pos", MCUtils.nbtPos(x, z, -y)); // X/Y/Z => X/Z/-Y
        serialize(entity);
        world.addEntity(x, z, -y, entity); // X/Y/Z => X/Z/-Y
    }
}
