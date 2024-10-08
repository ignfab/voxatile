package com.ignfab.minalac.generator.outputs.minecraft;

/**
 * An object linked to a {@link MCVoxelWorld Minecraft World}.
 */
public class MCObject {
    /**
     * The Minecraft World object.
     */
    protected final MCVoxelWorld world;

    /**
     * Constructs a new {@code MCObject}.
     *
     * @param world the {@link MCVoxelWorld} this object belongs to
     */
    public MCObject(MCVoxelWorld world) {
        this.world = world;
    }
}
